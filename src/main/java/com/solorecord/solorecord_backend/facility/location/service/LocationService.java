package com.solorecord.solorecord_backend.facility.location.service;

import com.solorecord.solorecord_backend.facility.location.dto.response.LgaResponse;
import com.solorecord.solorecord_backend.facility.location.dto.response.StateResponse;
import com.solorecord.solorecord_backend.facility.location.models.Lga;
import com.solorecord.solorecord_backend.facility.location.models.State;
import com.solorecord.solorecord_backend.facility.location.repository.LgaRepository;
import com.solorecord.solorecord_backend.facility.location.repository.StateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class LocationService {

    private final LgaRepository lgaRepository;
    private final StateRepository stateRepository;


    public List<LgaResponse> getLgasByState(UUID stateId){
        List<Lga> lgas = lgaRepository.findByStateIdOrderByName(stateId);

         return lgas.stream().map(lga -> new LgaResponse(
                 lga.getId(),lga.getName(),lga.getCode())).toList();
    }

    public List<StateResponse> getStates(){
        List<State> states = stateRepository.findAllByOrderByName();
        List<StateResponse> response = new ArrayList<>();

        for (State state : states){
            StateResponse stateResponse = new StateResponse(
                    state.getId(),state.getCode(),state.getName());
            response.add(stateResponse);
        }
        return response;
    }

}
