package aquanet.strings;

import aquanet.core.CustomList;

public class KMPAlgorithm {

    /**
     * Searches for a pattern within a text and returns a list of starting indices where matches occur.
     */
    public static CustomList<Integer> search(String text, String pattern) {
        CustomList<Integer> matchIndices = new CustomList<>();
        
        if (pattern == null || pattern.length() == 0 || text == null || text.length() < pattern.length()) {
            return matchIndices;
        }

        int[] lps = computeLPSArray(pattern);
        int i = 0; // index for text
        int j = 0; // index for pattern

        while (i < text.length()) {
            if (pattern.charAt(j) == text.charAt(i)) {
                j++;
                i++;
            }
            if (j == pattern.length()) {
                // Match found!
                matchIndices.add(i - j);
                j = lps[j - 1]; // Reset j using LPS to find overlapping matches
            } else if (i < text.length() && pattern.charAt(j) != text.charAt(i)) {
                // Mismatch after j matches
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i = i + 1;
                }
            }
        }
        return matchIndices;
    }

    /**
     * Pre-processes the pattern to compute the Longest Prefix Suffix (LPS) array.
     */
    private static int[] computeLPSArray(String pattern) {
        int length = 0;
        int i = 1;
        int[] lps = new int[pattern.length()];
        lps[0] = 0; // LPS of first character is always 0

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else {
                if (length != 0) {
                    length = lps[length - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }
}