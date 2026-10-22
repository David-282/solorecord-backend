package com.solorecord.solorecord_backend.identity.eventListner;

import com.solorecord.solorecord_backend.events.model.FacilityApprovedEvent;
import com.solorecord.solorecord_backend.facility.dto.response.AdminContactResponse;
import com.solorecord.solorecord_backend.facility.service.FacilityService;
import com.solorecord.solorecord_backend.identity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FacilityApprovedListener {

    private final UserService userService;
    private final FacilityService facilityService;


    @EventListener
    public void onFacilityApproved(FacilityApprovedEvent event) {
        AdminContactResponse contact = facilityService.getAdminContact(event.facilityId());

        userService.createFacilityAdmin(
                contact.adminFullName(),
                contact.adminEmail(),
                contact.adminPhone(),
                event.facilityId());
    }
}
