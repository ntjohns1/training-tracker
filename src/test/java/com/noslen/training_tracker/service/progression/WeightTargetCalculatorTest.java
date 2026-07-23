package com.noslen.training_tracker.service.progression;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Contract tests for {@link WeightTargetCalculator}. These assert the calculator's own
 * behaviour (rounding, band ordering, progression rules), not RP's exact proprietary band —
 * exact-value calibration is the job of the Phase F golden-master replay.
 */
class WeightTargetCalculatorTest {

    @Test
    @DisplayName("progressWeight steps the working weight up by the increment and holds reps")
    void progressWeightStepsUp() {
        // captured: ex4 100lb x 8 -> next weightTarget 102.5, repsTarget 8, wMin 87.5
        WeightTargetCalculator.Targets t = WeightTargetCalculator.progressWeight(100f, 8, 2.5f);

        assertEquals(102.5f, t.weightTarget());
        assertEquals(8, t.repsTarget());
        // light end of the band matches the captured value exactly
        assertEquals(87.5f, t.weightTargetMin());
        // band is ordered around the working weight
        assertTrue(t.weightTargetMin() < t.weightTarget());
        assertTrue(t.weightTargetMax() > t.weightTarget());
    }

    @Test
    @DisplayName("progressReps holds the working weight and increments the rep target")
    void progressRepsIncrementsReps() {
        // captured: ex535937 125lb x 15 -> next weightTarget 125, repsTarget 16
        WeightTargetCalculator.Targets t = WeightTargetCalculator.progressReps(125f, 15, 2.5f);

        assertEquals(125f, t.weightTarget());
        assertEquals(16, t.repsTarget());
        assertTrue(t.weightTargetMin() < t.weightTarget());
        assertTrue(t.weightTargetMax() > t.weightTarget());
    }

    @Test
    @DisplayName("deload reduces load ~10% and reps, with no surrounding band")
    void deloadReduces() {
        // captured: ex4 deload ~95lb (from week-3 working weight 105)
        WeightTargetCalculator.Targets t = WeightTargetCalculator.deload(105f, 8, 2.5f);

        assertEquals(95f, t.weightTarget());
        assertEquals(t.weightTarget(), t.weightTargetMin());
        assertEquals(t.weightTarget(), t.weightTargetMax());
        assertTrue(t.repsTarget() >= 1 && t.repsTarget() < 8);
    }

    @Test
    @DisplayName("rounds to the equipment increment")
    void roundsToIncrement() {
        assertEquals(102.5f, WeightTargetCalculator.roundToIncrement(101.3, 2.5f));
        assertEquals(100.0f, WeightTargetCalculator.roundToIncrement(101.0, 5f));
        assertEquals(45.0f, WeightTargetCalculator.roundToIncrement(44.2, 5f));
    }

    @Test
    @DisplayName("Epley 1RM and its inverse are consistent")
    void epleyRoundTrips() {
        double oneRepMax = WeightTargetCalculator.epleyOneRepMax(100f, 8);
        double backToWorking = WeightTargetCalculator.weightForReps(oneRepMax, 8);
        assertEquals(100.0, backToWorking, 0.001);
    }
}
