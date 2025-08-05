package com.noslen.training_tracker.service.progression;

import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.day.DayMuscleGroup;
import com.noslen.training_tracker.model.day.ExerciseSet;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;

/**
 * Service responsible for calculating workout progressions based on user feedback
 * and RIR (Reps in Reserve) progression system.
 * Implements the progression algorithm as specified in progression_algorithm.md:
 * - Analyzes feedback from completed workouts (pump, soreness, workload, joint pain)
 * - Calculates recommended sets for next week's corresponding day
 * - Generates progressive weight/rep targets for exercise sets
 * - Handles deload week calculations
 */
public interface ProgressionCalculationService {
    
    /**
     * Calculates progression for the next week based on completed day feedback.
     * This is the main entry point triggered when a day is marked as complete.
     * 
     * @param completedDay The day that was just completed with user feedback
     * @return true if progression was successfully calculated and applied
     */
    boolean calculateProgression(Day completedDay);
    
    /**
     * Updates recommended sets for a specific DayMuscleGroup based on feedback.
     * Uses muscle group-specific thresholds and recovery characteristics.
     * 
     * @param currentDayMuscleGroup The completed DayMuscleGroup with feedback
     * @param nextWeekDayMuscleGroup The corresponding DayMuscleGroup for next week
     */
    void updateRecommendedSets(DayMuscleGroup currentDayMuscleGroup, DayMuscleGroup nextWeekDayMuscleGroup);
    
    /**
     * Calculates progressive weight and rep targets for exercise sets.
     * Implements different progression strategies based on:
     * - Current RIR (from mesocycle.microRirs)
     * - Exercise type (bodyweight vs weighted)
     * - Rep range (high reps 11-15 vs lower reps)
     * - Current weight (determines increment size)
     * 
     * @param previousWeekSet The completed set from previous week
     * @param currentWeekRir The RIR target for current week
     * @return New ExerciseSet with calculated targets
     */
    ExerciseSet calculateSetProgression(ExerciseSet previousWeekSet, int currentWeekRir);
    
    /**
     * Calculates deload targets for exercise sets.
     * Uses special deload rules with reduced volume and intensity.
     * 
     * @param previousWeekSet The set from the peak week (week 4)
     * @param isFirstHalfOfWeek Whether this is first or second half of deload week
     * @return ExerciseSet with deload targets
     */
    ExerciseSet calculateDeloadTargets(ExerciseSet previousWeekSet, boolean isFirstHalfOfWeek);
    
    /**
     * Finds the corresponding day in the next week.
     * 
     * @param currentDay The completed day
     * @return The corresponding day in next week (same position, week + 1)
     */
    Day findNextWeekDay(Day currentDay);
    
    /**
     * Gets the current week's RIR from mesocycle microRirs string.
     * 
     * @param mesocycle The mesocycle containing microRirs
     * @param weekNumber The week number (1-based)
     * @return RIR value for the specified week, or -1 for deload
     */
    int getCurrentWeekRir(Mesocycle mesocycle, int weekNumber);
}
