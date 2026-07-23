package com.noslen.training_tracker.service.progression;

/**
 * Computes weight and rep targets for a prescribed set based on the achieved
 * performance of the previous week.
 *
 * <p><b>Approximation note:</b> RP's real weight-target band appears to be driven by a
 * proprietary reps→%1RM table. This implementation uses an Epley one-rep-max estimate and a
 * rep window ({@code repsTarget - 3} for the heavy end, {@code repsTarget + 6} for the light
 * end), rounded to the equipment increment. Calibrated against the captured full_meso data it
 * matches the light end ({@code weightTargetMin}) and is within one increment on the heavy end
 * ({@code weightTargetMax}). The Phase F golden-master replay is the mechanism for tightening
 * these constants — see docs/progression_algorithm.md.</p>
 */
public final class WeightTargetCalculator {

    /** Default load step for barbell/most machine work. */
    public static final float DEFAULT_INCREMENT = 2.5f;

    /** Heavy end of the target band, in reps below the working target. */
    private static final int HEAVY_END_REP_OFFSET = -3;
    /** Light end of the target band, in reps above the working target. */
    private static final int LIGHT_END_REP_OFFSET = 6;

    /** Deload load factor applied to the last working weight (~90%). */
    private static final float DELOAD_WEIGHT_FACTOR = 0.90f;
    /** Deload rep factor applied to the last working reps (~60%). */
    private static final float DELOAD_REP_FACTOR = 0.60f;

    private WeightTargetCalculator() {
        // Utility class - prevent instantiation
    }

    /**
     * Result of a target computation: the working target plus the surrounding band and rep target.
     */
    public record Targets(float weightTarget, float weightTargetMin, float weightTargetMax,
                          int repsTarget) {}

    /**
     * Computes next week's working targets when progressing load: the working weight steps up by
     * {@code increment} while the rep target holds.
     *
     * @param achievedWeight last week's achieved weight
     * @param achievedReps   last week's achieved reps
     * @param increment      equipment load step (e.g. 2.5 or 5)
     * @return the target band and rep target
     */
    public static Targets progressWeight(float achievedWeight, int achievedReps, float increment) {
        float workingWeight = roundToIncrement(achievedWeight + increment, increment);
        return band(workingWeight, achievedReps, increment);
    }

    /**
     * Computes next week's working targets when progressing reps: the working weight holds while
     * the rep target increases by one.
     *
     * @param achievedWeight last week's achieved weight
     * @param achievedReps   last week's achieved reps
     * @param increment      equipment load step used for band rounding
     * @return the target band and rep target
     */
    public static Targets progressReps(float achievedWeight, int achievedReps, float increment) {
        float workingWeight = roundToIncrement(achievedWeight, increment);
        return band(workingWeight, achievedReps + 1, increment);
    }

    /**
     * Computes deload targets: reduced load and reps, no band (deload is a light, fixed prescription).
     *
     * @param referenceWeight the working weight to deload from (typically the first week's weight)
     * @param referenceReps   the reps to deload from
     * @param increment       equipment load step used for rounding
     * @return deload targets ({@code weightTargetMin}/{@code Max} equal {@code weightTarget})
     */
    public static Targets deload(float referenceWeight, int referenceReps, float increment) {
        float weight = roundToIncrement(referenceWeight * DELOAD_WEIGHT_FACTOR, increment);
        int reps = Math.max(1, Math.round(referenceReps * DELOAD_REP_FACTOR));
        return new Targets(weight, weight, weight, reps);
    }

    /**
     * Builds the target band around a working weight for a given rep target using an Epley 1RM
     * estimate and the heavy/light rep offsets.
     */
    private static Targets band(float workingWeight, int repsTarget, float increment) {
        double oneRepMax = epleyOneRepMax(workingWeight, repsTarget);
        float max = roundToIncrement(
                weightForReps(oneRepMax, Math.max(1, repsTarget + HEAVY_END_REP_OFFSET)), increment);
        float min = roundToIncrement(
                weightForReps(oneRepMax, repsTarget + LIGHT_END_REP_OFFSET), increment);
        return new Targets(workingWeight, min, max, repsTarget);
    }

    /** Epley one-rep-max estimate: {@code w * (1 + reps/30)}. */
    static double epleyOneRepMax(float weight, int reps) {
        return weight * (1.0 + reps / 30.0);
    }

    /** Inverse Epley: the weight liftable for {@code reps} at the given 1RM. */
    static double weightForReps(double oneRepMax, int reps) {
        return oneRepMax / (1.0 + reps / 30.0);
    }

    /** Rounds a weight to the nearest multiple of {@code increment}. */
    static float roundToIncrement(double weight, float increment) {
        if (increment <= 0) {
            return (float) weight;
        }
        return (float) (Math.round(weight / increment) * increment);
    }
}
