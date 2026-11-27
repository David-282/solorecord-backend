package com.solorecord.solorecord_backend.patient.utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NameMaskerTest {

    @Test
    void nullAndBlankBecomePlaceholder() {
        assertEquals("***", NameMasker.mask(null));
        assertEquals("***", NameMasker.mask("   "));
    }

    @Test
    void singleLetterRevealsNothing() {
        assertEquals("***", NameMasker.mask("A"));
    }

    @Test
    void shortPartsRevealOneLetter() {
        assertEquals("Jo***", NameMasker.mask("Joseph"));
        assertEquals("T***", NameMasker.mask("Tim"));
    }

    @Test
    void twoPartNameIsMaskedPerPart() {
        assertEquals("Da*** Af***", NameMasker.mask("David Afolabi"));
    }

    @Test
    void extraSpacesAreCollapsed() {
        assertEquals("Da*** Af***", NameMasker.mask("  David    Afolabi "));
    }
}
