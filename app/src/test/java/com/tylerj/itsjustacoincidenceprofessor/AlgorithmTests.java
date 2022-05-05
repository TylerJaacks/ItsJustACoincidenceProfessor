package com.tylerj.itsjustacoincidenceprofessor;

import com.tylerj.itsjustacoincidenceprofessor.algorithm.Algorithm;

import junit.framework.Assert;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests for Algorithm class.
 */
public class AlgorithmTests {
    /**
     * Tests two identical strings for similarity percentages.
     */
    @Test
    public void TwoIdenticalStrings() {
        String s1 = "Hello";
        String s2 = "Hello";

        Assert.assertEquals(Algorithm.getPercentageSimilarity(Algorithm.getLevenshteinDistance(s1, s2), s1.length(), s2.length()), 100.0f);
    }

    /**
     * Tests two different strings for similarity percentages.
     */
    @Test
    public void TwoDifferentStrings() {
        String s1 = "Hello";
        String s2 = "World";

        Assert.assertEquals(Algorithm.getPercentageSimilarity(Algorithm.getLevenshteinDistance(s1, s2), s1.length(), s2.length()), 20.0f);
    }

    /**
     * Tests two identical Hello, world programs for similarity percentages.
     */
    @Test
    public void TwoIdenticalHelloWorlds() {
        String s1 = "package com.tylerj.itsjustacoincidenceprofessor.app; public class Main { public static void main(String[] args) { System.out.println(\"Hello, world!\"); } }";
        String s2 = "package com.tylerj.itsjustacoincidenceprofessor.app; public class Main { public static void main(String[] args) { System.out.println(\"Hello, world!\"); } }";

        Assert.assertEquals(Algorithm.getPercentageSimilarity(Algorithm.getLevenshteinDistance(s1, s2), s1.length(), s2.length()), 100.0f);
    }

    /**
     * Tests two slightly changed Hello, world programs for similarity percentages.
     */
    @Test
    public void TwoSlightlyChangedHelloWorlds() {
        String s1 = "package com.tylerj.helloworld.app; public class Main { public static void main(String[] args) { System.out.println(\"Hello, world!\"); } }";
        String s2 = "package com.tylerj.itsjustacoincidenceprofessor.app; public class Main { public static void main(String[] args) { System.out.println(\"Hello, world!\"); } }";

        Assert.assertEquals(Algorithm.getPercentageSimilarity(Algorithm.getLevenshteinDistance(s1, s2), s1.length(), s2.length()), 88.0f);
    }
}
