package com.solorecord.solorecord_backend.lab.service;

import com.solorecord.solorecord_backend.events.model.TestResultAssignedEvent;
import com.solorecord.solorecord_backend.events.model.TestResultCompletedEvent;
import com.solorecord.solorecord_backend.events.publisher.EventPublisher;
import com.solorecord.solorecord_backend.identity.data.model.AccountStatus;
import com.solorecord.solorecord_backend.identity.service.UserService;
import com.solorecord.solorecord_backend.lab.data.model.OrderStatus;
import com.solorecord.solorecord_backend.lab.data.model.ResultStatus;
import com.solorecord.solorecord_backend.lab.data.model.TestOrder;
import com.solorecord.solorecord_backend.lab.data.model.TestResult;
import com.solorecord.solorecord_backend.lab.data.model.TestType;
import com.solorecord.solorecord_backend.lab.data.repository.TestOrderRepository;
import com.solorecord.solorecord_backend.lab.data.repository.TestResultRepository;
import com.solorecord.solorecord_backend.lab.dto.request.LabInitiatedResultRequest;
import com.solorecord.solorecord_backend.lab.dto.request.ResultValueRequest;
import com.solorecord.solorecord_backend.lab.dto.request.TestOrderRequest;
import com.solorecord.solorecord_backend.lab.dto.response.TestOrderResponse;
import com.solorecord.solorecord_backend.lab.dto.response.TestResultResponse;
import com.solorecord.solorecord_backend.lab.exception.InvalidLabStateException;
import com.solorecord.solorecord_backend.lab.exception.InvalidReviewerException;
import com.solorecord.solorecord_backend.lab.exception.InvalidTestTypeException;
import com.solorecord.solorecord_backend.lab.exception.LabAccessDeniedException;
import com.solorecord.solorecord_backend.lab.exception.OrderNotFoundException;
import com.solorecord.solorecord_backend.lab.exception.PatientNotFoundException;
import com.solorecord.solorecord_backend.lab.exception.ResultNotFoundException;
import com.solorecord.solorecord_backend.patient.service.AccessGrantService;
import com.solorecord.solorecord_backend.patient.service.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class LabServiceImpl implements LabService {

    private final TestOrderRepository orderRepository;
    private final TestResultRepository resultRepository;
    private final AccessGrantService accessGrantService;
    private final PatientService patientService;
    private final UserService userService;
    private final EventPublisher eventPublisher;

    @Override
    @Transactional
    public TestOrderResponse createOrder(UUID doctorId, UUID facilityId, TestOrderRequest request) {
        if (!accessGrantService.hasActiveAccess(doctorId, facilityId, request.patientId())) {
            throw new LabAccessDeniedException("You do not have active access to this patient's record");
        }

        String otherName = resolveOtherName(request.testType(), request.otherTestName());

        TestOrder saved = orderRepository.save(new TestOrder(
                request.patientId(), doctorId, facilityId, request.testType(), otherName, request.notes()));
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestOrderResponse> listPendingOrders(UUID facilityId) {
        return orderRepository.findByFacilityIdAndStatusOrderByOrderedAtAsc(facilityId, OrderStatus.PENDING)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public TestResultResponse enterOrderResult(UUID labTechId, UUID facilityId, UUID orderId, ResultValueRequest request) {
        // Another facility's order is "not found", the same as an order that doesn't exist.
        TestOrder order = orderRepository.findByIdAndFacilityId(orderId, facilityId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING || resultRepository.existsByTestOrderId(orderId)) {
            throw new InvalidLabStateException("A result has already been entered for this order");
        }

        TestResult saved = resultRepository.save(new TestResult(
                order.getPatientId(), labTechId, facilityId, order.getId(),
                order.getTestType(), order.getOtherTestName(),
                request.resultValue().strip(), order.getDoctorId()));
        return toResponse(saved);
    }

    @Override
    @Transactional
    public TestResultResponse enterLabInitiatedResult(UUID labTechId, UUID facilityId, LabInitiatedResultRequest request) {
        if (!patientService.hasCompletedProfile(request.patientId())) {
            throw new PatientNotFoundException("Patient not found");
        }

        String otherName = resolveOtherName(request.testType(), request.otherTestName());

        TestResult saved = resultRepository.save(new TestResult(
                request.patientId(), labTechId, facilityId, null,
                request.testType(), otherName, request.resultValue().strip(), null));
        return toResponse(saved);
    }

    @Override
    @Transactional
    public TestResultResponse updateResultValue(UUID labTechId, UUID facilityId, UUID resultId, ResultValueRequest request) {
        TestResult result = loadOwnResult(labTechId, facilityId, resultId);
        if (result.isCompleted()) {
            throw new InvalidLabStateException("A completed result cannot be changed");
        }
        result.updateResultValue(request.resultValue().strip());
        return toResponse(result);
    }

    @Override
    @Transactional
    public TestResultResponse completeResult(UUID labTechId, UUID facilityId, UUID resultId) {
        TestResult result = loadOwnResult(labTechId, facilityId, resultId);
        if (result.isCompleted()) {
            throw new InvalidLabStateException("This result is already completed");
        }

        result.markCompleted();

        if (result.getTestOrderId() != null) {
            orderRepository.findById(result.getTestOrderId()).ifPresent(TestOrder::fulfil);
        }

        eventPublisher.publish(TestResultCompletedEvent.of(
                result.getId(), result.getPatientId(), result.getFacilityId(),
                result.getTestType().name(), result.getReviewingDoctorId()));
        return toResponse(result);
    }

    @Override
    @Transactional
    public TestResultResponse routeResult(UUID adminFacilityId, UUID resultId, UUID doctorId) {
        TestResult result = resultRepository.findByIdAndFacilityId(resultId, adminFacilityId)
                .orElseThrow(() -> new ResultNotFoundException("Result not found"));

        if (result.getTestOrderId() != null) {
            throw new InvalidLabStateException("Only lab-initiated results can be routed; this one has an ordering doctor");
        }
        if (!result.isCompleted()) {
            throw new InvalidLabStateException("Only completed results can be routed");
        }

        boolean validDoctor = userService.getUserByFacilityId(adminFacilityId).stream()
                .anyMatch(u -> doctorId.equals(u.getId())
                        && "DOCTOR".equals(u.getRole())
                        && u.getStatus() == AccountStatus.ACTIVE);
        if (!validDoctor) {
            throw new InvalidReviewerException("Reviewer must be an active doctor at your facility");
        }

        result.routeTo(doctorId);
        eventPublisher.publish(TestResultAssignedEvent.of(result.getId(), doctorId, adminFacilityId));
        return toResponse(result);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestResultResponse> getResultsForDoctor(UUID doctorId, UUID facilityId, UUID patientId) {
        if (!accessGrantService.hasActiveAccess(doctorId, facilityId, patientId)) {
            throw new LabAccessDeniedException("You do not have active access to this patient's record");
        }
        return completedResults(patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestResultResponse> getOwnResults(UUID patientId) {
        return completedResults(patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCompletedResultForPatient(UUID resultId, UUID patientId) {
        return resultRepository.existsByIdAndPatientIdAndStatus(resultId, patientId, ResultStatus.COMPLETED);
    }

    private List<TestResultResponse> completedResults(UUID patientId) {
        return resultRepository.findByPatientIdAndStatusOrderByCompletedAtDesc(patientId, ResultStatus.COMPLETED)
                .stream().map(this::toResponse).toList();
    }

    /** Only the technician who entered a result can change or complete it. */
    private TestResult loadOwnResult(UUID labTechId, UUID facilityId, UUID resultId) {
        return resultRepository.findByIdAndFacilityId(resultId, facilityId)
                .filter(r -> r.getLabTechId().equals(labTechId))
                .orElseThrow(() -> new ResultNotFoundException("Result not found"));
    }

    /** OTHER needs a name, which is trimmed and lower-cased; every other type must not carry one. */
    private String resolveOtherName(TestType type, String otherTestName) {
        if (type != TestType.OTHER) {
            return null;
        }
        if (otherTestName == null || otherTestName.isBlank()) {
            throw new InvalidTestTypeException("otherTestName is required when testType is OTHER");
        }
        return otherTestName.strip().toLowerCase();
    }

    private TestOrderResponse toResponse(TestOrder o) {
        return new TestOrderResponse(o.getId(), o.getPatientId(), o.getDoctorId(), o.getFacilityId(),
                o.getTestType(), o.getOtherTestName(), o.getNotes(), o.getStatus(), o.getOrderedAt());
    }

    private TestResultResponse toResponse(TestResult r) {
        return new TestResultResponse(r.getId(), r.getPatientId(), r.getLabTechId(), r.getFacilityId(),
                r.getTestOrderId(), r.getTestType(), r.getOtherTestName(), r.getResultValue(), r.getStatus(),
                r.getEnteredAt(), r.getCompletedAt(), r.getReviewingDoctorId());
    }
}
