package com.noslen.training_tracker.service.progression;

public class ProgressionCalculator {

        /**
         * Calculates the next recommended set count for this muscle group based on the
         * last prescription (previous) and user feedback (jointPain, pump, soreness, workload).
         *
         * <p>Input scales:</p>
         * <ul>
         *   <li>workload, soreness, jointPain: 0..3
         *     <ul>
         *       <li>workload: 0=Easy, 1=Pretty good, 2=Pushed my limits, 3=Too much</li>
         *       <li>soreness: 0=never sore, 1=healed a while ago, 2=healed just in time, 3=still sore</li>
         *       <li>jointPain: 0=none, 1=low, 2=moderate, 3=high</li>
         *     </ul>
         *   </li>
         *   <li>pump: 0..2 (0=low, 1=medium, 2=high)</li>
         * </ul>
         *
         * <p>Decision policy (summary):</p>
         * <ul>
         *   <li>Safety first: if jointPain==3 or workload==3 → decrease (-1).</li>
         *   <li>If soreness==3 → hold only when jointPain==0; otherwise decrease.</li>
         *   <li>Compute riskSum = jointPain + soreness + workload.</li>
         *   <li>If riskSum ≤ 2 → generally increase (+1) unless pump is already high (2), then hold.</li>
         *   <li>If riskSum ≤ 4 → usually +1; but hold when pump==2 or jointPain==1.</li>
         *   <li>If riskSum ∈ [5,6] → hold; allow +1 only if pump==0 and (jointPain==0 && workload≤1 && soreness≤1).</li>
         *   <li>Always clamp the result to be at least 1 set.</li>
         * </ul>
         *
         * @param previous   the previous recommended set count (expected ≥ 1)
         * @param jointPain  perceived joint pain (0..3)
         * @param pump       perceived pump (0..2)
         * @param soreness   perceived muscle soreness (0..3)
         * @param workload   perceived workout difficulty (0..3)
         * @return the next recommended set count, never less than 1
         */

        public static int calculateRecommendedSets ( int previous, int jointPain, int pump, int soreness,
        int workload){
            // Clamp inputs defensively
            soreness = Math.max(0,
                                Math.min(3,
                                         soreness));
            workload = Math.max(0,
                                Math.min(3,
                                         workload));
            pump = Math.max(0,
                            Math.min(2,
                                     pump));

            // 1) Hard safety overrides
            // If any metric hits the max risk (3), act conservatively first and exit early.
            if (jointPain == 3) {
                return Math.max(0,
                                previous - 1);
            }

            if (workload == 3) {
                return Math.max(1,
                                previous - 1);
            }

            // 2) Soreness==3 rule: hold only if there is no joint pain; otherwise decrease.
            if (soreness == 3) {
                if (jointPain == 0) {
                    return Math.max(1,
                                    previous); // ok to hold here
                } else {
                    return Math.max(1,
                                    previous - 1);
                }
            }

            // 3) Aggregate strain (0..6 after the early exits above)
            int riskSum = jointPain + soreness + workload;

            // 4) Low risk → increase; pump=2 (high) caps the increase to a hold
            if (riskSum <= 2) {
                // Very easy: if pump is already high (2), volume is likely sufficient → hold
                return (pump == 2) ? Math.max(1,
                                              previous) : previous + 1;
            } else if (riskSum <= 4) {
                // Easy-ish: normally +1; but if pump is high (2) OR there is some pain (jointPain==1), be conservative
                if (pump == 2 || jointPain == 1) {
                    return Math.max(1,
                                    previous);
                }
                return previous + 1;
            } else {
                // 5) Cutoff zone (5–6): final else because after early exits riskSum ≤ 6.
                // Usually hold; allow a +1 bump only if everything looks great AND pump is LOW (0), which can indicate under-stimulus.
                if (pump == 0 && jointPain == 0 && workload <= 1 && soreness <= 1) {
                    return previous + 1;
                }
                return Math.max(1,
                                previous);
            }
        }
    }
