package com.solorecord.solorecord_backend.facility.location;




import com.solorecord.solorecord_backend.facility.location.models.Lga;
import com.solorecord.solorecord_backend.facility.location.models.State;
import com.solorecord.solorecord_backend.facility.location.repository.LgaRepository;
import com.solorecord.solorecord_backend.facility.location.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocationSeeder implements ApplicationRunner {

    private static final String SEED_FILE = "locations/nigeria-locations.json";

    private final StateRepository stateRepository;
    private final LgaRepository lgaRepository;
    private final ObjectMapper objectMapper;

    record LgaData(String name, String code, BigDecimal latitude,
                   BigDecimal longitude, String wikidataId) {}

    record StateData(String name, String code, List<LgaData> lgas) {}

    @Override
    @Transactional
    public void run(@NonNull ApplicationArguments args) {
        List<StateData> states = readSeedFile();

        Map<String, State> statesByCode = new HashMap<>();
        stateRepository.findAll().forEach(s -> statesByCode.put(s.getCode(), s));

        Set<String> lgaCodes = new HashSet<>();
        lgaRepository.findAll().forEach(l -> lgaCodes.add(l.getCode()));

        int newStates = 0;
        int newLgas = 0;

        for (StateData stateData : states) {
            State state = statesByCode.get(stateData.code());

            if (state == null) {
                state = stateRepository.save(new State(stateData.name(), stateData.code()));
                statesByCode.put(state.getCode(), state);
                newStates++;
            }

            for (LgaData lgaData : stateData.lgas()) {
                if (lgaCodes.add(lgaData.code())) {
                    lgaRepository.save(new Lga(
                            state.getId(),
                            lgaData.name(),
                            lgaData.code(),
                            lgaData.latitude(),
                            lgaData.longitude(),
                            lgaData.wikidataId()));
                    newLgas++;
                }
            }
        }

        log.info("Location seed complete: {} states and {} LGAs added", newStates, newLgas);
    }

    private List<StateData> readSeedFile() {
        try (InputStream in = new ClassPathResource(SEED_FILE).getInputStream()) {
            List<StateData> states = objectMapper.readValue(in, new TypeReference<>() {});

            if (states == null || states.isEmpty()) {
                throw new IllegalStateException("Location seed file is empty: " + SEED_FILE);
            }
            return states;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read location seed file: " + SEED_FILE, e);
        }
    }
}
