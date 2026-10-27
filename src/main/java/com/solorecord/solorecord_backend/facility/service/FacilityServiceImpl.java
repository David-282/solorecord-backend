package com.solorecord.solorecord_backend.facility.service;

import com.solorecord.solorecord_backend.events.model.FacilityApprovedEvent;
import com.solorecord.solorecord_backend.events.publisher.EventPublisher;
import com.solorecord.solorecord_backend.facility.data.model.Facility;
import com.solorecord.solorecord_backend.facility.data.model.FacilityStatus;
import com.solorecord.solorecord_backend.facility.data.repository.FacilityRepository;
import com.solorecord.solorecord_backend.facility.dto.request.FacilityOnboardingRequest;
import com.solorecord.solorecord_backend.facility.dto.request.FacilityRejectRequest;
import com.solorecord.solorecord_backend.facility.dto.response.AdminContactResponse;
import com.solorecord.solorecord_backend.facility.dto.response.FacilityOnboardingResponse;
import com.solorecord.solorecord_backend.facility.dto.response.PendingFacilityResponse;
import com.solorecord.solorecord_backend.facility.exception.*;
import com.solorecord.solorecord_backend.facility.location.repository.LgaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.solorecord.solorecord_backend.facility.Mapper.*;

@Service
@RequiredArgsConstructor
public class FacilityServiceImpl implements FacilityService{

    private final FacilityRepository facilityRepo;
    private final LgaRepository lgaRepo;
    private final EventPublisher eventPublisher;


    @Override
    @Transactional
    public FacilityOnboardingResponse onboardFacility(FacilityOnboardingRequest request) {

        String licenseNumber = request.getLicenseNumber().toLowerCase().strip();

        if(!lgaRepo.existsById(request.getLgaId())){
            throw new LGADoesNotExistException("Pls input a valid LGA");
        }

         if (facilityRepo.existsByLicenseNumberAndStatus(licenseNumber, FacilityStatus.PENDING) ){
          throw new FacilityInReviewException("The facility is under review, we would send a mail as soon as possible");
         }

        if (facilityRepo.existsByLicenseNumberAndStatus(licenseNumber, FacilityStatus.APPROVED)) {
            throw new FacilityAlreadyApprovedException("The facility already exist, thanks for using our facility ");
        }

        Facility facility =  facilityRepo.save(map(request, licenseNumber));
        String message = "Facility has been successfully submitted for review, we would get back to u soonest by email. Thank u";


        return map(facility, message);
    }

    @Override
    @Transactional
    public void  approveFacility(UUID facilityId, UUID reviewerId){

        Facility facility =  facilityRepo.findById(facilityId).orElseThrow(() ->  new FacilityDoesNotExsitEception("The said facility is not valid "));

        facility.approve(reviewerId);
        facilityRepo.save(facility);
        eventPublisher.publish(FacilityApprovedEvent.of(
                facilityId,
                reviewerId
        ));
    }

    @Transactional
    @Override
    public void disapproveFacility(UUID facilityId, UUID reviewerId, FacilityRejectRequest request){

        Facility facility =  facilityRepo.findById(facilityId).orElseThrow(() ->  new FacilityDoesNotExsitEception("The said facility is not valid "));

        facility.reject(reviewerId, request.rejectionMessage());
        facilityRepo.save(facility);
    }


    @Override
    @Transactional(readOnly = true)
    public AdminContactResponse getAdminContact(UUID facilityId) {
        Facility facility = facilityRepo.findById(facilityId)
                .orElseThrow(() -> new FacilityDoesNotExistException("The said facility is not valid"));

        if (facility.getStatus() != FacilityStatus.APPROVED) {
            throw new FacilityNotApprovedException("Admin contact is only available for approved facilities");
        }

        return new AdminContactResponse(
                facility.getAdminFullName(),
                facility.getAdminEmail(),
                facility.getAdminPhone());
    }

    @Override
    public String getFacilityName(UUID id) {
        Facility facility = facilityRepo.findById(id).get();
        return facility.getFacilityName();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PendingFacilityResponse> getPendingFacilities() {
        return facilityRepo.findByStatusOrderBySubmittedAtAsc(FacilityStatus.PENDING).stream()
                .map(facility -> new PendingFacilityResponse(
                        facility.getId(), facility.getFacilityName(), facility.getLicenseNumber(),
                        facility.getLgaId(), facility.getAdminEmail(), facility.getSubmittedAt()))
                .toList();
    }
}