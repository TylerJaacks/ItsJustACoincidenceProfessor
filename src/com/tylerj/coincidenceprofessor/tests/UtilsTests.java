package com.tylerj.coincidenceprofessor.tests;

import com.tylerj.coincidenceprofessor.codesearch.Languages;
import com.tylerj.coincidenceprofessor.codesearch.Locations;
import com.tylerj.coincidenceprofessor.codesearch.Utils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the Utils class.
 */
public class UtilsTests {
    /**
     * Tests the languageId for Java.
     */
    @Test
    public void javaLanguageId() {
        assertEquals(Utils.languageEnumToId(Languages.JAVA), 23);
    }

    /**
     * Tests the locationId for GitHub.
     */
    @Test
    public void gitHubLocationId() {
        assertEquals(Utils.locationEnumToId(Locations.GITHUB), 2);
    }
}
