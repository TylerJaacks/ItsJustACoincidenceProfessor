package com.tylerj.coincidenceprofessor.tests;

import com.tylerj.coincidenceprofessor.algorithm.Algorithm;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class AlgorithmTests {
    @Test
    public void TwoIdenticalStrings() {
        String s1 = "Hello";
        String s2 = "Hello";

        assertEquals(Algorithm.getPercentageSimilarity(Algorithm.getLevenshteinDistance(s1, s2), s1.length(), s2.length()), 100.0f);
    }

    @Test
    public void TwoDifferentStrings() {
        String s1 = "Hello";
        String s2 = "World";

        assertEquals(Algorithm.getPercentageSimilarity(Algorithm.getLevenshteinDistance(s1, s2), s1.length(), s2.length()), 20.0f);
    }

    @Test
    public void TwoIdenticalHelloWorlds() {
        String s1 = "package com.tylerj.coincidenceprofessor.app; public class Main { public static void main(String[] args) { System.out.println(\"Hello, world!\"); } }";
        String s2 = "package com.tylerj.coincidenceprofessor.app; public class Main { public static void main(String[] args) { System.out.println(\"Hello, world!\"); } }";

        assertEquals(Algorithm.getPercentageSimilarity(Algorithm.getLevenshteinDistance(s1, s2), s1.length(), s2.length()), 100.0f);
    }

    @Test
    public void TwoSlightlyChangedHelloWorlds() {
        String s1 = "package com.tylerj.helloworld.app; public class Main { public static void main(String[] args) { System.out.println(\"Hello, world!\"); } }";
        String s2 = "package com.tylerj.coincidenceprofessor.app; public class Main { public static void main(String[] args) { System.out.println(\"Hello, world!\"); } }";

        assertEquals(Algorithm.getPercentageSimilarity(Algorithm.getLevenshteinDistance(s1, s2), s1.length(), s2.length()), 88.0f);
    }
}
