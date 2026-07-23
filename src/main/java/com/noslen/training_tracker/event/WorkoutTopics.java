package com.noslen.training_tracker.event;

/**
 * Kafka topic names for workout lifecycle events.
 */
public final class WorkoutTopics {

    /** Published when a workout day is completed; drives next-week progression. */
    public static final String DAY_COMPLETED = "workout.day-completed";

    private WorkoutTopics() {
        // Constants holder - prevent instantiation
    }
}
