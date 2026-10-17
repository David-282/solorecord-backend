package com.solorecord.solorecord_backend.patient.service;

import com.solorecord.solorecord_backend.events.model.PatientLookedUpEvent;
import com.solorecord.solorecord_backend.events.publisher.EventPublisher;
import com.solorecord.solorecord_backend.identity.dto.response.UserContactResponse;
import com.solorecord.solorecord_backend.identity.exception.TooManyLookupAttemptsException;
import com.solorecord.solorecord_backend.identity.service.UserService;
import com.solorecord.solorecord_backend.patient.data.model.Patient;
import com.solorecord.solorecord_backend.patient.data.repository.PatientRepository;
import com.solorecord.solorecord_backend.patient.dto.request.PatientLookUpRequest;
import com.solorecord.solorecord_backend.patient.dto.response.PatientLookUpResponse;
import com.solorecord.solorecord_backend.patient.exception.InvalidAccessRequestException;
import com.solorecord.solorecord_backend.patient.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PatientLookUpImpl implements PatientLookUpService{


    private static final String KEY_PREFIX = "lookup:attempts:";
    private static final int MAX_ATTEMPTS = 10;
    private static final Duration WINDOW = Duration.ofMinutes(10);

    private final PatientRepository patientRepository;
    private final UserService userService;
    private final EventPublisher eventPublisher;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public PatientLookUpResponse lookup(UUID doctorId, UUID doctorFacilityId, PatientLookUpRequest request) {
        if (doctorFacilityId == null) {
            throw new InvalidAccessRequestException("Doctor is not attached to a facility");
        }

        enforceRateLimit(doctorId);

        Optional<Patient> found = patientRepository
                .findByNationalIdAndDateOfBirth(request.nationalId(), request.dateOfBirth());

        if (found.isEmpty()) {
            eventPublisher.publish(PatientLookedUpEvent.of(doctorId, doctorFacilityId, false));
            throw new UserNotFoundException("No matching patient");
        }

        Patient patient = found.get();
        UserContactResponse contact = userService.getUserContact(patient.getId());

        eventPublisher.publish(PatientLookedUpEvent.of(doctorId, doctorFacilityId, true));

        return PatientLookUpResponse.of(patient.getId(), contact.fullName());
    }

    private void enforceRateLimit(UUID doctorId) {
        String key = KEY_PREFIX + doctorId;

        Long attempts = redisTemplate.opsForValue().increment(key);

        if (attempts != null && attempts == 1) {
            redisTemplate.expire(key, WINDOW);
        }

        if (attempts != null && attempts > MAX_ATTEMPTS) {
            throw new TooManyLookupAttemptsException("Too many lookup attempts. Try again later");
        }
    }
}
