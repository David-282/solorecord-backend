package com.solorecord.solorecord_backend.lab.data.model;

/**
 * Fixed list so outbreak detection can count results reliably. Stored by name, so never rename an entry
 * once data exists. OTHER carries a free-text name in TestOrder/TestResult.otherTestName.
 */
public enum TestType {
    MALARIA,
    TYPHOID,
    CHOLERA,
    LASSA_FEVER,
    COVID_19,
    TUBERCULOSIS,
    HIV,
    HEPATITIS_B,
    MENINGITIS,
    OTHER
}
