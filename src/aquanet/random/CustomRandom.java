package aquanet.random;

/**
 * A custom Linear Congruential Generator (LCG) to generate pseudorandom numbers.
 * Keeps us compliant with the "no java.util.*" engine constraint.
 */
public class CustomRandom {
    private long state;

    public CustomRandom(long seed) {
        this.state = seed;
    }

    /**
     * Returns a pseudorandom integer between 0 (inclusive) and bound (exclusive).
     */
    public int nextInt(int bound) {
        if (bound <= 0) throw new IllegalArgumentException("Bound must be positive");
        
        // Classic LCG parameters (used in Numerical Recipes)
        state = (state * 1664525L + 1013904223L) & 0xFFFFFFFFL;
        
        // Map the generated state to the requested bound
        return (int) (state % bound);
    }
}