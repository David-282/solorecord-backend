package com.solorecord.solorecord_backend.facility.location.controller;

import com.solorecord.solorecord_backend.facility.location.dto.response.LgaResponse;
import com.solorecord.solorecord_backend.facility.location.dto.response.StateResponse;
import com.solorecord.solorecord_backend.facility.location.service.LocationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/location/states")
@AllArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/{stateId}/lgas")
    public ResponseEntity<List<LgaResponse>> getLgasByState (@PathVariable UUID stateId){
        return ResponseEntity.ok(locationService.getLgasByState(stateId));
    }

    @GetMapping
    public ResponseEntity<List<StateResponse>> getStates (){
        return ResponseEntity.ok(locationService.getStates());
    }
}
