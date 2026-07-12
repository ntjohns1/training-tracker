package com.noslen.training_tracker.service.progression;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link ProgressionCalculator#calculateRecommendedSets(int, int, int, int, int)}.
 *
 * Parameter order: (previous, jointPain, pump, soreness, workload)
 * Scales: jointPain/soreness/workload 0..3, pump 0..2
 */
public class ProgressionCalculatorTest {

    @Nested
    @DisplayName("Safety overrides")
    class SafetyOverrides {

        @Test
        @DisplayName("jointPain==3 decreases by 1 regardless of other feedback")
        void jointPainMaxDecreases() {
            assertEquals(2, ProgressionCalculator.calculateRecommendedSets(3, 3, 0, 0, 0));
            assertEquals(2, ProgressionCalculator.calculateRecommendedSets(3, 3, 2, 3, 3));
        }

        @Test
        @DisplayName("jointPain==3 floors at 0 (asymmetric with all other paths which floor at 1)")
        void jointPainMaxFloorsAtZero() {
            // NOTE: documents a possible bug - every other decrease path floors at 1,
            // but the jointPain==3 path can return 0 sets.
            assertEquals(0, ProgressionCalculator.calculateRecommendedSets(1, 3, 0, 0, 0));
            assertEquals(0, ProgressionCalculator.calculateRecommendedSets(0, 3, 0, 0, 0));
        }

        @Test
        @DisplayName("workload==3 decreases by 1, floored at 1")
        void workloadMaxDecreases() {
            assertEquals(2, ProgressionCalculator.calculateRecommendedSets(3, 0, 0, 0, 3));
            assertEquals(1, ProgressionCalculator.calculateRecommendedSets(1, 0, 0, 0, 3));
        }

        @Test
        @DisplayName("jointPain==3 takes precedence over workload==3")
        void jointPainPrecedesWorkload() {
            // jointPain path uses Math.max(0, ...) - result 0 proves jointPain branch ran
            assertEquals(0, ProgressionCalculator.calculateRecommendedSets(1, 3, 0, 0, 3));
        }
    }

    @Nested
    @DisplayName("Soreness==3 rule")
    class SorenessMax {

        @Test
        @DisplayName("soreness==3 with no joint pain holds")
        void holdsWhenNoJointPain() {
            assertEquals(3, ProgressionCalculator.calculateRecommendedSets(3, 0, 0, 3, 0));
            assertEquals(1, ProgressionCalculator.calculateRecommendedSets(1, 0, 0, 3, 2));
        }

        @Test
        @DisplayName("soreness==3 with any joint pain decreases, floored at 1")
        void decreasesWithJointPain() {
            assertEquals(2, ProgressionCalculator.calculateRecommendedSets(3, 1, 0, 3, 0));
            assertEquals(2, ProgressionCalculator.calculateRecommendedSets(3, 2, 0, 3, 0));
            assertEquals(1, ProgressionCalculator.calculateRecommendedSets(1, 1, 0, 3, 0));
        }
    }

    @Nested
    @DisplayName("riskSum <= 2 (low risk)")
    class LowRisk {

        @ParameterizedTest(name = "previous={0} jp={1} pump={2} sore={3} work={4} -> {5}")
        @CsvSource({
                // increase by 1 when pump is not high
                "3, 0, 0, 0, 0, 4",
                "3, 0, 1, 0, 0, 4",
                "3, 0, 0, 1, 1, 4", // riskSum=2
                "3, 0, 1, 2, 0, 4", // riskSum=2
                "1, 0, 0, 0, 0, 2",
                // pump==2 caps to hold
                "3, 0, 2, 0, 0, 3",
                "3, 0, 2, 1, 1, 3",
                "1, 0, 2, 0, 0, 1",
        })
        void lowRisk(int previous, int jointPain, int pump, int soreness, int workload, int expected) {
            assertEquals(expected,
                    ProgressionCalculator.calculateRecommendedSets(previous, jointPain, pump, soreness, workload));
        }
    }

    @Nested
    @DisplayName("riskSum 3-4 (moderate risk)")
    class ModerateRisk {

        @ParameterizedTest(name = "previous={0} jp={1} pump={2} sore={3} work={4} -> {5}")
        @CsvSource({
                // increase by 1 when pump < 2 and jointPain != 1
                "3, 0, 0, 2, 2, 4", // riskSum=4
                "3, 0, 1, 1, 2, 4", // riskSum=3
                "3, 2, 0, 1, 0, 4", // riskSum=3, jointPain==2 does not trigger hold
                // hold when pump==2
                "3, 0, 2, 2, 2, 3",
                "3, 0, 2, 1, 2, 3",
                // hold when jointPain==1
                "3, 1, 0, 1, 1, 3",
                "3, 1, 0, 2, 0, 3",
                "1, 1, 0, 1, 1, 1",
        })
        void moderateRisk(int previous, int jointPain, int pump, int soreness, int workload, int expected) {
            assertEquals(expected,
                    ProgressionCalculator.calculateRecommendedSets(previous, jointPain, pump, soreness, workload));
        }
    }

    @Nested
    @DisplayName("riskSum 5-6 (cutoff zone)")
    class CutoffZone {

        @ParameterizedTest(name = "previous={0} jp={1} pump={2} sore={3} work={4} -> {5}")
        @CsvSource({
                // hold in the cutoff zone
                "3, 2, 0, 2, 2, 3", // riskSum=6
                "3, 1, 0, 2, 2, 3", // riskSum=5
                "3, 2, 1, 1, 2, 3", // riskSum=5
                "3, 2, 2, 2, 2, 3", // riskSum=6, high pump
                "1, 2, 0, 2, 2, 1", // floors at 1
        })
        void cutoffZoneHolds(int previous, int jointPain, int pump, int soreness, int workload, int expected) {
            assertEquals(expected,
                    ProgressionCalculator.calculateRecommendedSets(previous, jointPain, pump, soreness, workload));
        }

        @Test
        @DisplayName("the +1 escape hatch (pump==0, jp==0, work<=1, sore<=1) is unreachable at riskSum>=5")
        void escapeHatchUnreachable() {
            // NOTE: documents dead code - jp==0 && work<=1 && sore<=1 implies riskSum <= 2,
            // which is handled by the low-risk branch. The +1 bump inside the 5-6 zone
            // can never execute. Max achievable sum under those constraints:
            assertEquals(4, ProgressionCalculator.calculateRecommendedSets(3, 0, 0, 1, 1)); // handled by riskSum<=2
        }
    }

    @Nested
    @DisplayName("Input clamping")
    class InputClamping {

        @Test
        @DisplayName("negative soreness is clamped to 0")
        void negativeSorenessClamped() {
            assertEquals(4, ProgressionCalculator.calculateRecommendedSets(3, 0, 0, -1, 0));
        }

        @Test
        @DisplayName("soreness above 3 is clamped to 3 (triggers soreness rule)")
        void highSorenessClamped() {
            assertEquals(3, ProgressionCalculator.calculateRecommendedSets(3, 0, 0, 99, 0));
        }

        @Test
        @DisplayName("workload above 3 is clamped to 3 (triggers safety decrease)")
        void highWorkloadClamped() {
            assertEquals(2, ProgressionCalculator.calculateRecommendedSets(3, 0, 0, 0, 10));
        }

        @Test
        @DisplayName("pump above 2 is clamped to 2 (caps increase to hold)")
        void highPumpClamped() {
            assertEquals(3, ProgressionCalculator.calculateRecommendedSets(3, 0, 5, 0, 0));
        }

        @Test
        @DisplayName("jointPain is NOT clamped (possible bug)")
        void jointPainNotClamped() {
            // NOTE: documents a possible bug - jointPain has no defensive clamp.
            // jp=5 skips the jp==3 safety override and lands in the cutoff zone (riskSum=5).
            assertEquals(3, ProgressionCalculator.calculateRecommendedSets(3, 5, 0, 0, 0));
        }
    }
}
