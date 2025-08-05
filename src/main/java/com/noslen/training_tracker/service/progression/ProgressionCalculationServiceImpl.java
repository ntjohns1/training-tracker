package com.noslen.training_tracker.service.progression;

import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.model.day.DayMuscleGroup;
import com.noslen.training_tracker.model.day.ExerciseSet;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import com.noslen.training_tracker.model.progression.MuscleGroup;
import com.noslen.training_tracker.repository.day.DayRepo;
import com.noslen.training_tracker.repository.day.ExerciseSetRepo;
import com.noslen.training_tracker.enums.SetType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProgressionCalculationServiceImpl implements ProgressionCalculationService {
    
    private final DayRepo dayRepository;
    private final ExerciseSetRepo exerciseSetRepository;
    
    @Override
    public boolean calculateProgression(Day completedDay) {
        log.info("Calculating progression for completed day: {} (Week {}, Position {})", 
                completedDay.getId(), completedDay.getWeek(), completedDay.getPosition());
        
        // Find the corresponding day in next week
        Day nextWeekDay = findNextWeekDay(completedDay);
        if (nextWeekDay == null) {
            log.warn("No next week day found for day: {}", completedDay.getId());
            return false;
        }
        
        // Get current week RIR from mesocycle
        Mesocycle mesocycle = completedDay.getMesocycle();
        int currentWeekRir = getCurrentWeekRir(mesocycle, nextWeekDay.getWeek());
        
        log.info("Processing progression for next week day: {} with RIR: {}", 
                nextWeekDay.getId(), currentWeekRir);
        
        // Update recommended sets for each muscle group
        for (DayMuscleGroup currentDmg : completedDay.getMuscleGroups()) {
            DayMuscleGroup nextWeekDmg = findCorrespondingDayMuscleGroup(nextWeekDay, currentDmg.getMuscleGroup());
            if (nextWeekDmg != null) {
                updateRecommendedSets(currentDmg, nextWeekDmg);
            }
        }
        
        // Calculate exercise set progressions
        for (DayExercise currentDayExercise : completedDay.getExercises()) {
            DayExercise nextWeekDayExercise = findCorrespondingDayExercise(nextWeekDay, currentDayExercise);
            if (nextWeekDayExercise != null) {
                calculateExerciseProgression(currentDayExercise, nextWeekDayExercise, currentWeekRir);
            }
        }
        
        log.info("Progression calculation completed for day: {}", completedDay.getId());
        return true;
    }
    
    @Override
    public void updateRecommendedSets(DayMuscleGroup currentDayMuscleGroup, DayMuscleGroup nextWeekDayMuscleGroup) {
        // Calculate feedback score (pump + soreness + workload + jointPain)
        int feedbackScore = calculateFeedbackScore(currentDayMuscleGroup);
        
        // Get muscle group specific threshold
        int threshold = getMuscleGroupThreshold(currentDayMuscleGroup.getMuscleGroup());
        
        // Determine set adjustment
        int currentSets = currentDayMuscleGroup.getRecommendedSets() != null ? 
                         currentDayMuscleGroup.getRecommendedSets() : 0;
        int newSets = calculateNewSetCount(currentSets, feedbackScore, threshold);
        
        // Update next week's recommended sets
        nextWeekDayMuscleGroup.setRecommendedSets(newSets);
        
        log.debug("Updated sets for muscle group {}: {} -> {} (feedback: {}, threshold: {})",
                currentDayMuscleGroup.getMuscleGroup().getName(), currentSets, newSets, feedbackScore, threshold);
    }
    
    @Override
    public ExerciseSet calculateSetProgression(ExerciseSet previousWeekSet, int currentWeekRir) {
        if (currentWeekRir == -1) {
            // Deload week
            return calculateDeloadTargets(previousWeekSet, true); // Assuming first half for now
        }
        
        Float currentWeight = previousWeekSet.getWeight();
        Integer currentReps = previousWeekSet.getReps();
        
        if (currentWeight == null || currentReps == null) {
            log.warn("Previous week set missing weight or reps data: {}", previousWeekSet.getId());
            return previousWeekSet; // Return unchanged if data is missing
        }
        
        // Create new set with progression
        ExerciseSet newSet = new ExerciseSet();
        newSet.setSetType(previousWeekSet.getSetType());
        newSet.setPosition(previousWeekSet.getPosition());
        
        // Determine progression strategy
        if (isBodyweightExercise(currentWeight) || isHighRepRange(currentReps)) {
            // Progress by adding 1 rep
            newSet.setRepsTarget(currentReps + 1);
            newSet.setWeightTarget(currentWeight);
            calculateRepBasedTargets(newSet, currentWeight, currentReps + 1);
        } else {
            // Progress by weight
            float newWeight = calculateWeightProgression(currentWeight);
            newSet.setWeightTarget(newWeight);
            newSet.setRepsTarget(currentReps);
            calculateWeightBasedTargets(newSet, newWeight, currentReps);
        }
        
        return newSet;
    }
    
    @Override
    public ExerciseSet calculateDeloadTargets(ExerciseSet previousWeekSet, boolean isFirstHalfOfWeek) {
        ExerciseSet deloadSet = new ExerciseSet();
        deloadSet.setSetType(previousWeekSet.getSetType());
        deloadSet.setPosition(previousWeekSet.getPosition());
        
        Float peakWeight = previousWeekSet.getWeight();
        Integer peakReps = previousWeekSet.getReps();
        
        if (peakWeight == null || peakReps == null) {
            return previousWeekSet;
        }
        
        // Deload to ~60-70% of peak weight with moderate reps
        float deloadWeight = peakWeight * 0.65f;
        int deloadReps = Math.max(6, Math.min(12, peakReps + 2));
        
        deloadSet.setWeightTarget(deloadWeight);
        deloadSet.setRepsTarget(deloadReps);
        
        // Set deload ranges (wider tolerance)
        deloadSet.setWeightTargetMin(deloadWeight * 0.85f);
        deloadSet.setWeightTargetMax(deloadWeight * 1.15f);
        
        return deloadSet;
    }
    
    @Override
    public Day findNextWeekDay(Day currentDay) {
        return dayRepository.findByMesocycleAndWeekAndPosition(
                currentDay.getMesocycle(),
                currentDay.getWeek() + 1,
                currentDay.getPosition()
        ).orElse(null);
    }
    
    @Override
    public int getCurrentWeekRir(Mesocycle mesocycle, int weekNumber) {
        Long microRirs = mesocycle.getMicroRirs();
        if (microRirs == null) {
            return -1; // Default to deload if invalid
        }
        
        String microRirsStr = microRirs.toString();
        if (weekNumber < 1 || weekNumber > microRirsStr.length()) {
            return -1; // Default to deload if invalid
        }
        
        char rirChar = microRirsStr.charAt(weekNumber - 1);
        if (Character.isDigit(rirChar)) {
            return Character.getNumericValue(rirChar);
        }
        
        return -1; // Deload week
    }
    
    // Helper methods
    
    private int calculateFeedbackScore(DayMuscleGroup dmg) {
        int score = 0;
        score += dmg.getPump() != null ? dmg.getPump() : 0;
        score += dmg.getSoreness() != null ? dmg.getSoreness() : 0;
        score += dmg.getWorkload() != null ? dmg.getWorkload() : 0;
        
        // Aggregate joint pain from all DayExercises in this muscle group
        int jointPainSum = dmg.getDay().getExercises().stream()
                .filter(de -> de.getMuscleGroup().getId().equals(dmg.getId()))
                .mapToInt(de -> de.getJointPain() != null ? de.getJointPain() : 0)
                .sum();
        
        score += jointPainSum;
        return score;
    }
    
    private int getMuscleGroupThreshold(MuscleGroup muscleGroup) {
        // Muscle group specific thresholds based on recovery characteristics
        // These values can be fine-tuned based on training data
        return switch (muscleGroup.getName().getValue().toLowerCase()) {
            case "calves", "biceps", "triceps" -> 12; // Fast recovery
            case "chest", "shoulders", "back" -> 14; // Moderate recovery
            case "quads", "hamstrings", "glutes" -> 16; // Slower recovery
            default -> 14; // Default moderate threshold
        };
    }
    
    private int calculateNewSetCount(int currentSets, int feedbackScore, int threshold) {
        if (feedbackScore < threshold - 2) {
            return currentSets + 1; // Add a set if recovery is excellent
        } else if (feedbackScore > threshold + 2) {
            return Math.max(1, currentSets - 1); // Remove a set if recovery is poor
        }
        return currentSets; // Keep same if recovery is moderate
    }
    
    private boolean isBodyweightExercise(Float weight) {
        return weight == null || weight == 0.0f;
    }
    
    private boolean isHighRepRange(Integer reps) {
        return reps != null && reps >= 11 && reps <= 15;
    }
    
    private float calculateWeightProgression(float currentWeight) {
        if (currentWeight < 21f) {
            return currentWeight + 0.5f;
        } else if (currentWeight < 71f) {
            return currentWeight + 1.0f;
        } else if (currentWeight < 141f) {
            return currentWeight + 2.5f;
        } else {
            return currentWeight + 5.0f;
        }
    }
    
    private void calculateRepBasedTargets(ExerciseSet set, float weight, int targetReps) {
        // Use simple rep range for bodyweight/high rep exercises
        set.setWeightTargetMin(weight);
        set.setWeightTargetMax(weight);
    }
    
    private void calculateWeightBasedTargets(ExerciseSet set, float targetWeight, int reps) {
        // Calculate min/max using approximate 1RM formulas
        // This is a simplified version - can be enhanced with proper Epley formula
        float minWeight = targetWeight * 0.85f; // ~12 reps equivalent
        float maxWeight = targetWeight * 1.15f; // ~5 reps equivalent
        
        set.setWeightTargetMin(minWeight);
        set.setWeightTargetMax(maxWeight);
    }
    
    private DayMuscleGroup findCorrespondingDayMuscleGroup(Day nextWeekDay, MuscleGroup muscleGroup) {
        return nextWeekDay.getMuscleGroups().stream()
                .filter(dmg -> dmg.getMuscleGroup().getId().equals(muscleGroup.getId()))
                .findFirst()
                .orElse(null);
    }
    
    private DayExercise findCorrespondingDayExercise(Day nextWeekDay, DayExercise currentDayExercise) {
        return nextWeekDay.getExercises().stream()
                .filter(de -> de.getExercise().getId().equals(currentDayExercise.getExercise().getId()) &&
                             de.getMuscleGroup().getMuscleGroup().getId().equals(
                                     currentDayExercise.getMuscleGroup().getMuscleGroup().getId()))
                .findFirst()
                .orElse(null);
    }
    
    private void calculateExerciseProgression(DayExercise currentDayExercise, DayExercise nextWeekDayExercise, int currentWeekRir) {
        List<ExerciseSet> currentSets = currentDayExercise.getSets();
        if (currentSets.isEmpty()) {
            return;
        }
        
        // Use the last set as reference for progression (typically the heaviest/most challenging)
        ExerciseSet referenceSet = currentSets.get(currentSets.size() - 1);
        
        // Calculate progression for each set in next week
        List<ExerciseSet> nextWeekSets = nextWeekDayExercise.getSets();
        for (int i = 0; i < nextWeekSets.size() && i < currentSets.size(); i++) {
            ExerciseSet currentSet = currentSets.get(i);
            ExerciseSet nextWeekSet = nextWeekSets.get(i);
            
            // Calculate progression based on current set
            ExerciseSet progressedSet = calculateSetProgression(currentSet, currentWeekRir);
            
            // Apply progression to next week's set
            nextWeekSet.setWeightTarget(progressedSet.getWeightTarget());
            nextWeekSet.setWeightTargetMin(progressedSet.getWeightTargetMin());
            nextWeekSet.setWeightTargetMax(progressedSet.getWeightTargetMax());
            nextWeekSet.setRepsTarget(progressedSet.getRepsTarget());
            
            // Save the updated set
            exerciseSetRepository.save(nextWeekSet);
        }
    }
}
