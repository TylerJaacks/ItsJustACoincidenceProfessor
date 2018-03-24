package com.tylerj.coincidenceprofessor.algorithm;

public class Algorithm {
    public static int getLevenshteinDistance(String sourceStr, String comparisonStr) {
        int sourceStrLength = sourceStr.length();
        int comparisonStrLength = comparisonStr.length();

        char[] sourceChar = stringToChar(sourceStr.toLowerCase());
        char[] comparisonChar = stringToChar(comparisonStr.toLowerCase());
        int[][] distanceChar = fillMatrix(sourceChar, comparisonChar);

        for (int row = 1; row < sourceStrLength + 1; row++) {
            for (int col = 1; col < comparisonStrLength + 1; col++) {
                if (sourceChar[row - 1] == comparisonChar[col - 1]) {
                    distanceChar[row][col] = distanceChar[row - 1][col - 1];
                } else {
                    int tmp = Math.min((distanceChar[row - 1][col] + 1), (distanceChar[row][col -  1] + 1));

                    distanceChar[row][col] = Math.min(tmp, (distanceChar[row - 1][col - 1] + 1));
                }
            }
        }

        return distanceChar[sourceStrLength][comparisonStrLength];
    }

    public static float getPercentageSimilarity(int distance, int len1, int len2) {
        float maxLength = (float) Math.max(len1, len2);
        float division = distance / maxLength;
        float decimal = 1.0f - division;
        float percentage = decimal * 100.0f;

        return Math.round(Math.abs(percentage));
    }

    private static char[] stringToChar(String string) {
        char[] charArr = new char[string.length()];

        for (int i = 0; i < string.length(); i++) {
            charArr[i] = string.charAt(i);
        }

        return charArr;
    }

    private static void printCharArray(char[] char1) {
        for (int i = 0; i < char1.length; i++) {
            System.out.printf(char1[i] + " ");
        }

        System.out.println("");
    }

    private static void printMatrix(int[][] arr, int n, int m) {
        for (int i = 0; i < n + 1; i++) {
            for (int j = 0; j < m + 1; j++) {
                System.out.print(arr[i][j] + " ");
            }

            System.out.println();
        }
    }

    private static int[][] fillMatrix(char[] s1, char[] s2) {
        int[][] int2DArr = new int[s1.length + 1][s2.length + 1];

        for (int i = 0; i < s1.length + 1; i++) {
            int2DArr[i][0] = i;
        }

        for (int j = 0; j < s2.length + 1; j++) {
            int2DArr[0][j] = j;
        }

        return int2DArr;
    }
}
