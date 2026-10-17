package com.solorecord.solorecord_backend.patient.utility;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class NameMasker {

    private static final String PLACEHOLDER = "***";

    private NameMasker() {}

    public static String mask(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return PLACEHOLDER;
        }

        return Arrays.stream(fullName.strip().split("\\s+"))
                .map(NameMasker::maskPart)
                .collect(Collectors.joining(" "));
    }

    private static String maskPart(String part) {
        int reveal = part.length() <= 1 ? 0
                : part.length() <= 4 ? 1
                : 2;

        return part.substring(0, reveal) + PLACEHOLDER;
    }
}
