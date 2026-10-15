package aquanet.random;

import aquanet.core.CustomList;

public class ReservoirSampler {
    private final String[] reservoir;
    private final int k;
    private int count;
    private final CustomRandom rng;

    /**
     * Initializes a sampler that keeps exactly k items in memory.
     */
    public ReservoirSampler(int k, long seed) {
        this.k = k;
        this.reservoir = new String[k];
        this.count = 0;
        this.rng = new CustomRandom(seed);
    }

    /**
     * Processes a single incoming sensor reading from the live stream.
     * Memory stays strictly at O(k) regardless of how many elements arrive.
     */
    public void processStreamElement(String dataPoint) {
        // Step 1: Fill the reservoir until it has k elements
        if (count < k) {
            reservoir[count] = dataPoint;
        } 
        // Step 2: For the i-th element (count >= k), replace a random element 
        // in the reservoir with probability k/i.
        else {
            int j = rng.nextInt(count + 1); 
            if (j < k) {
                reservoir[j] = dataPoint;
            }
        }
        count++;
    }

    /**
     * Retrieves the current uniform sample.
     */
    public CustomList<String> getSample() {
        CustomList<String> sample = new CustomList<>();
        int limit = Math.min(count, k);
        for (int i = 0; i < limit; i++) {
            sample.add(reservoir[i]);
        }
        return sample;
    }
    
    public int getTotalProcessed() {
        return count;
    }
}