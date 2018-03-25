package com.tylerj.coincidenceprofessor.algorithm;

/**
 * Represents the Wagner–Fischer algorithm.
 */
public class Algorithm {
    /**
     * Get the Levenshtein Distance for two given strings.
     * @param s1 The first string.
     * @param s2 The second string.
     * @return Returns the Levenshtein Distance for two strings.
     */
    public static int getLevenshteinDistance(String s1, String s2) {
        int s1Length = s1.length();
        int s2Length = s2.length();

        char[] s1CharArr = stringToChar(s1.toLowerCase());
        char[] s2CharArr = stringToChar(s2.toLowerCase());
        int[][] distanceMatrix = fillLevenshteinMatrix(s1CharArr, s2CharArr);

        for (int row = 1; row < s1Length + 1; row++) {
            for (int col = 1; col < s2Length + 1; col++) {
                if (s1CharArr[row - 1] == s2CharArr[col - 1]) {
                    distanceMatrix[row][col] = distanceMatrix[row - 1][col - 1];
                } else {
                    int tmp = Math.min((distanceMatrix[row - 1][col] + 1), (distanceMatrix[row][col -  1] + 1));
                    distanceMatrix[row][col] = Math.min(tmp, (distanceMatrix[row - 1][col - 1] + 1));
                }
            }
        }

        return distanceMatrix[s1Length][s2Length];
    }

    /**
     * Gets the percentage of similarity between to strings.
     * @param distance The Levenshtein Distance.
     * @param len1 The length of the first string.
     * @param len2 The length of the second string.
     * @return The percentage of similarity between two strings.
     */
    public static float getPercentageSimilarity(int distance, int len1, int len2) {
        return Math.round(Math.abs((1.0f - (distance / ((float) Math.max(len1, len2)))) * 100.0f));
    }

    /**
     * Runs the algorithm for computing the similarity of two strings.
     * @param s1 The first string for comparison.
     * @param s2 The second string for comparison.
     * @return The percentage of similarity between to strings.
     */
    public static float runAlgorithm(String s1, String s2) {
        return (getPercentageSimilarity(getLevenshteinDistance(s1, s2), s1.length(), s2.length()));
    }

    /**
     * Converts Strings to char arrays.
     * @param s1 String to convert.
     * @return The character array representing the s1.
     */
    private static char[] stringToChar(String s1) {
        char[] charArr = new char[s1.length()];

        for (int i = 0; i < s1.length(); i++) {
            charArr[i] = s1.charAt(i);
        }

        return charArr;
    }

    /**
     * Prints a character array.
     * @param charArr The character array to print.
     */
    private static void printCharArray(char[] charArr) {
        for (int i = 0; i < charArr.length; i++) {
            System.out.print(charArr[i] + " ");
        }

        System.out.println("");
    }

    /**
     * Prints a the Levenshtein matrix.
     * @param arr The Levenshtein matrix.
     * @param n Rows in the matrix.
     * @param m Columns in the matrix.
     */
    private static void printLevenshteinMatrix(int[][] arr, int n, int m) {
        for (int row = 0; row < n + 1; row++) {
            for (int col = 0; col < m + 1; col++) {
                System.out.print(arr[row][col] + " ");
            }

            System.out.println();
        }
    }

    /**
     * Fills the Levenshtein matrix.
     * @param s1 The first character array.
     * @param s2 The second character array.
     * @return Levenshtein matrix.
     */
    private static int[][] fillLevenshteinMatrix(char[] s1, char[] s2) {
        int[][] matrix = new int[s1.length + 1][s2.length + 1];

        for (int i = 0; i < s1.length + 1; i++) {
            matrix[i][0] = i;
        }

        for (int j = 0; j < s2.length + 1; j++) {
            matrix[0][j] = j;
        }

        return matrix;
    }
}
