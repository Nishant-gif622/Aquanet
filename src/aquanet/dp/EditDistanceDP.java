package aquanet.dp;

import aquanet.core.CustomList;

public class EditDistanceDP {

    /**
     * Calculates the Levenshtein distance between two strings using Dynamic Programming.
     * Cost: O(N * M) time and space.
     */
    public static int calculate(String s1, String s2) {
        if (s1 == null || s2 == null) return -1;
        
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        // Base cases
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i; // Cost of deleting all characters from s1
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j; // Cost of inserting all characters into s1
        }

        // Fill the DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // Characters match, no edit cost
                } else {
                    int insert = dp[i][j - 1] + 1;
                    int delete = dp[i - 1][j] + 1;
                    int replace = dp[i - 1][j - 1] + 1;
                    
                    dp[i][j] = Math.min(insert, Math.min(delete, replace));
                }
            }
        }
        return dp[m][n];
    }

    /**
     * Finds the closest matching station name from a list of valid names.
     */
    public static String findClosestMatch(String query, CustomList<String> validNames) {
        if (validNames.isEmpty()) return null;

        String bestMatch = validNames.get(0);
        int minDistance = calculate(query, bestMatch);

        for (int i = 1; i < validNames.size(); i++) {
            String candidate = validNames.get(i);
            int dist = calculate(query, candidate);
            if (dist < minDistance) {
                minDistance = dist;
                bestMatch = candidate;
            }
        }
        return bestMatch;
    }
}