package com.noslen.training_tracker.factory;

import com.noslen.training_tracker.dto.mesocycle.request.CreateMesocycleRequest;
import com.noslen.training_tracker.dto.mesocycle.response.MesocycleResponse;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.model.day.DayMuscleGroup;
import com.noslen.training_tracker.model.exercise.Exercise;
import com.noslen.training_tracker.model.mesocycle.MesoTemplate;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import com.noslen.training_tracker.model.progression.MuscleGroup;
import com.noslen.training_tracker.model.progression.Progression;
import com.noslen.training_tracker.repository.exercise.ExerciseRepo;
import com.noslen.training_tracker.repository.day.DayMuscleGroupRepo;
import com.noslen.training_tracker.repository.progression.MuscleGroupRepo;
import com.noslen.training_tracker.util.EnumConverter;
import com.noslen.training_tracker.enums.Unit;
import com.noslen.training_tracker.enums.Status;
import com.noslen.training_tracker.enums.MgProgressionType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.UUID;

/**
 * Factory for creating Mesocycle entities with complex orchestration logic.
 * Handles nested entity creation, referential integrity, and progression setup.
 */
@Component
public class MesocycleFactory {
    
    private final ExerciseRepo exerciseRepo;
    private final DayMuscleGroupRepo dayMuscleGroupRepo;
    private final MuscleGroupRepo muscleGroupRepo;
    private final DayFactory dayFactory;
    
    public MesocycleFactory(ExerciseRepo exerciseRepo, DayMuscleGroupRepo dayMuscleGroupRepo, MuscleGroupRepo muscleGroupRepo, DayFactory dayFactory) {
        this.exerciseRepo = exerciseRepo;
        this.dayMuscleGroupRepo = dayMuscleGroupRepo;
        this.muscleGroupRepo = muscleGroupRepo;
        this.dayFactory = dayFactory;
    }
    
    /**
     * Creates a new Mesocycle entity from CreateMesocycleRequest with full orchestration.
     * This method handles the complex creation of nested Day, DayExercise, DayMuscleGroup, and Progression entities.
     */
    public Mesocycle createFromRequest(CreateMesocycleRequest request, Long userId) {
        if (request == null) {
            throw new IllegalArgumentException("CreateMesocycleRequest cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        Instant now = Instant.now();
        
        // Create the base Mesocycle entity using builder
        Mesocycle mesocycle = Mesocycle.builder()
                .mesocycleKey(generateMesocycleKey())
                .userId(userId)
                .name(request.name())
                .days(request.days() != null ? request.days().size() * request.weeks() : 0) // Handle null days safely
                .unit(stringToUnit(request.unit()))
                .sourceTemplate(request.sourceTemplateId() != null ? 
                    MesoTemplate.builder().id(request.sourceTemplateId()).build() : null)
                .sourceMeso(request.sourceMesoId() != null ? 
                    Mesocycle.builder().id(request.sourceMesoId()).build() : null)
                .microRirs(0L) // Default value since not in DTO
                .createdAt(now)
                .updatedAt(now)
                .finishedAt(null)
                .deletedAt(null)
                .firstMicroCompletedAt(null)
                .firstWorkoutCompletedAt(null)
                .firstExerciseCompletedAt(null)
                .firstSetCompletedAt(null)
                .lastMicroFinishedAt(null)
                .lastSetCompletedAt(null)
                .lastSetSkippedAt(null)
                .lastWorkoutCompletedAt(null)
                .lastWorkoutFinishedAt(null)
                .lastWorkoutSkippedAt(null)
                .lastWorkoutPartialedAt(null)
                .weeks(new ArrayList<>()) // Will be populated below
                .notes(new ArrayList<>())
                .status(null)
                .generatedFrom(null)
                .progressions(new HashMap<>()) // Will be populated below
                .build();
        
        // Create Days using DayFactory
        List<Day> daysList = createDaysFromRequest(request, mesocycle, now);
        
        // Create Progressions
        Map<Long, Progression> progressionsMap = createProgressionsFromRequest(request, mesocycle, now);
        
        // Since Mesocycle doesn't have setters, we need to rebuild it with the populated collections
        return Mesocycle.builder()
                .id(mesocycle.getId())
                .mesocycleKey(mesocycle.getMesocycleKey())
                .userId(mesocycle.getUserId())
                .name(mesocycle.getName())
                .days(mesocycle.getDays())
                .unit(mesocycle.getUnit())
                .sourceTemplate(mesocycle.getSourceTemplate())
                .sourceMeso(mesocycle.getSourceMeso())
                .microRirs(mesocycle.getMicroRirs())
                .createdAt(mesocycle.getCreatedAt())
                .updatedAt(mesocycle.getUpdatedAt())
                .finishedAt(mesocycle.getFinishedAt())
                .deletedAt(mesocycle.getDeletedAt())
                .firstMicroCompletedAt(mesocycle.getFirstMicroCompletedAt())
                .firstWorkoutCompletedAt(mesocycle.getFirstWorkoutCompletedAt())
                .firstExerciseCompletedAt(mesocycle.getFirstExerciseCompletedAt())
                .firstSetCompletedAt(mesocycle.getFirstSetCompletedAt())
                .lastMicroFinishedAt(mesocycle.getLastMicroFinishedAt())
                .lastSetCompletedAt(mesocycle.getLastSetCompletedAt())
                .lastSetSkippedAt(mesocycle.getLastSetSkippedAt())
                .lastWorkoutCompletedAt(mesocycle.getLastWorkoutCompletedAt())
                .lastWorkoutFinishedAt(mesocycle.getLastWorkoutFinishedAt())
                .lastWorkoutSkippedAt(mesocycle.getLastWorkoutSkippedAt())
                .lastWorkoutPartialedAt(mesocycle.getLastWorkoutPartialedAt())
                .weeks(daysList)
                .notes(mesocycle.getNotes())
                .status(mesocycle.getStatus())
                .generatedFrom(mesocycle.getGeneratedFrom())
                .progressions(progressionsMap)
                .build();
    }
    
    /**
     * Creates all Day entities with nested DayExercises and DayMuscleGroups.
     * Implements the pattern: weeks * day_patterns = total days
     */
    private List<Day> createDaysFromRequest(CreateMesocycleRequest request, Mesocycle mesocycle, Instant now) {
        List<Day> allDays = new ArrayList<>();
        List<CreateMesocycleRequest.DayRequest> dayPatterns = request.days();
        
        // Handle null days gracefully
        if (dayPatterns == null || dayPatterns.isEmpty()) {
            return allDays; // Return empty list
        }
        
        // Create days for each week
        for (int week = 1; week <= request.weeks(); week++) {
            for (int dayIndex = 0; dayIndex < dayPatterns.size(); dayIndex++) {
                CreateMesocycleRequest.DayRequest dayRequest = dayPatterns.get(dayIndex);
                
                // Create Day entity using builder
                Day day = Day.builder()
                        .mesocycle(mesocycle)
                        .week(week)
                        .position(dayIndex + 1) // 1-indexed position
                        .createdAt(now)
                        .updatedAt(now)
                        .build();
                
                // Create the nested entities
                List<DayExercise> dayExercises = createDayExercisesForDay(day, dayRequest, now);
                Set<DayMuscleGroup> dayMuscleGroups = createDayMuscleGroupsForDay(day, dayExercises, now);
                
                // Rebuild Day with populated collections
                Day dayWithCollections = Day.builder()
                        .id(day.getId())
                        .mesocycle(mesocycle)
                        .week(week)
                        .position(dayIndex + 1)
                        .createdAt(now)
                        .updatedAt(now)
                        .exercises(new ArrayList<>(dayExercises))
                        .muscleGroups(new ArrayList<>(dayMuscleGroups))
                        .build();
                
                allDays.add(dayWithCollections);
            }
        }
        
        return allDays;
    }
    
    /**
     * Creates Progression entities for muscle groups specified in the request.
     */
    private Map<Long, Progression> createProgressionsFromRequest(CreateMesocycleRequest request, Mesocycle mesocycle, Instant now) {
        Map<Long, Progression> progressions = new HashMap<>();
        
        if (request.progressions() != null) {
            for (Map.Entry<String, CreateMesocycleRequest.ProgressionRequest> entry : request.progressions().entrySet()) {
                CreateMesocycleRequest.ProgressionRequest progressionRequest = entry.getValue();
                
                // Fetch the MuscleGroup entity
                MuscleGroup muscleGroup = muscleGroupRepo.findById(progressionRequest.muscleGroupId())
                        .orElseThrow(() -> new IllegalArgumentException("MuscleGroup not found with ID: " + progressionRequest.muscleGroupId()));
                
                // Create Progression using AllArgsConstructor (id, muscleGroup, mgProgressionType, mesocycle)
                Progression progression = new Progression(null, muscleGroup, stringToMgProgressionType(progressionRequest.mgProgressionType()), mesocycle);
                
                progressions.put(progressionRequest.muscleGroupId(), progression);
            }
        }
        
        return progressions;
    }
    
    private List<DayExercise> createDayExercisesForDay(Day day, CreateMesocycleRequest.DayRequest dayRequest, Instant now) {
        List<DayExercise> dayExercises = new ArrayList<>();
        for (int i = 0; i < dayRequest.exercises().size(); i++) {
            CreateMesocycleRequest.DayExerciseRequest exerciseRequest = dayRequest.exercises().get(i);
            Exercise exercise = exerciseRepo.findById(exerciseRequest.exerciseId())
                    .orElseThrow(() -> new IllegalArgumentException("Exercise not found with ID: " + exerciseRequest.exerciseId()));
            
            DayExercise dayExercise = DayExercise.builder()
                    .day(day)
                    .exercise(exercise)
                    .position(i + 1) // 1-indexed position
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            
            // Use setter for createdAt since it has @Setter annotation
            dayExercise.setCreatedAt(now);
            dayExercise.setDay(day);
            dayExercise.setExercise(exercise);
            
            dayExercises.add(dayExercise);
        }
        return dayExercises;
    }
    
    private Set<DayMuscleGroup> createDayMuscleGroupsForDay(Day day, List<DayExercise> dayExercises, Instant now) {
        Set<DayMuscleGroup> dayMuscleGroups = new HashSet<>();
        Set<Long> processedMuscleGroups = new HashSet<>();
        
        for (DayExercise dayExercise : dayExercises) {
            // Note: Need to check if Exercise has getMuscleGroup() method
            // For now, we'll create a placeholder implementation
            Long muscleGroupId = 1L; // This should come from the exercise or request
            
            if (!processedMuscleGroups.contains(muscleGroupId)) {
                MuscleGroup muscleGroup = muscleGroupRepo.findById(muscleGroupId)
                        .orElseThrow(() -> new IllegalArgumentException("MuscleGroup not found with ID: " + muscleGroupId));
                
                DayMuscleGroup dayMuscleGroup = DayMuscleGroup.builder()
                        .day(day)
                        .muscleGroup(muscleGroup)
                        .createdAt(now)
                        .updatedAt(now)
                        .build();
                
                // Use setter for status since it has @Setter annotation
                dayMuscleGroup.setStatus(Status.UNPROGRAMMED);
                
                dayMuscleGroups.add(dayMuscleGroup);
                processedMuscleGroups.add(muscleGroupId);
            }
        }
        return dayMuscleGroups;
    }
    
    /**
     * Creates a new Mesocycle entity from response DTO for creation operations.
     * Sets appropriate timestamps and default values for new mesocycles.
     * (Kept for backward compatibility)
     */
    public Mesocycle createFromResponse(MesocycleResponse response) {
        if (response == null) {
            return null;
        }
        
        Instant now = Instant.now();
        
        return Mesocycle.builder()
                .id(response.id())
                .mesocycleKey(response.key())
                .userId(response.userId())
                .name(response.name())
                .days(response.days())
                .unit(stringToUnit(response.unit()))
                .sourceTemplate(response.sourceTemplateId() != null ? 
                    MesoTemplate.builder().id(response.sourceTemplateId()).build() : null)
                .sourceMeso(response.sourceMesoId() != null ? 
                    Mesocycle.builder().id(response.sourceMesoId()).build() : null)
                .microRirs(response.microRirs())
                .createdAt(now)
                .updatedAt(now)
                .finishedAt(null) // New mesocycles are not finished
                .deletedAt(null) // New mesocycles are not deleted
                .firstMicroCompletedAt(null)
                .firstWorkoutCompletedAt(null)
                .firstExerciseCompletedAt(null)
                .firstSetCompletedAt(null)
                .lastMicroFinishedAt(null)
                .lastSetCompletedAt(null)
                .lastSetSkippedAt(null)
                .lastWorkoutCompletedAt(null)
                .lastWorkoutFinishedAt(null)
                .lastWorkoutSkippedAt(null)
                .lastWorkoutPartialedAt(null)
                .weeks(Collections.emptyList()) // Will be populated separately if needed
                .notes(Collections.emptyList()) // Will be populated separately if needed
                .status(null) // Will be set based on business logic
                .generatedFrom(null) // Will be set based on business logic
                .progressions(Collections.emptyMap()) // Will be populated separately if needed
                .build();
    }
    
    /**
     * Creates a Mesocycle entity for soft delete operation.
     * Preserves all existing data but sets deletion timestamp.
     */
    public Mesocycle createForSoftDelete(Mesocycle existing) {
        if (existing == null) {
            return null;
        }
        
        return Mesocycle.builder()
                .id(existing.getId())
                .mesocycleKey(existing.getMesocycleKey())
                .userId(existing.getUserId())
                .name(existing.getName())
                .days(existing.getDays())
                .unit(existing.getUnit())
                .sourceTemplate(existing.getSourceTemplate())
                .sourceMeso(existing.getSourceMeso())
                .microRirs(existing.getMicroRirs())
                .createdAt(existing.getCreatedAt())
                .updatedAt(Instant.now())
                .finishedAt(existing.getFinishedAt())
                .deletedAt(Instant.now()) // Set deletion timestamp
                .firstMicroCompletedAt(existing.getFirstMicroCompletedAt())
                .firstWorkoutCompletedAt(existing.getFirstWorkoutCompletedAt())
                .firstExerciseCompletedAt(existing.getFirstExerciseCompletedAt())
                .firstSetCompletedAt(existing.getFirstSetCompletedAt())
                .lastMicroFinishedAt(existing.getLastMicroFinishedAt())
                .lastSetCompletedAt(existing.getLastSetCompletedAt())
                .lastSetSkippedAt(existing.getLastSetSkippedAt())
                .lastWorkoutCompletedAt(existing.getLastWorkoutCompletedAt())
                .lastWorkoutFinishedAt(existing.getLastWorkoutFinishedAt())
                .lastWorkoutSkippedAt(existing.getLastWorkoutSkippedAt())
                .lastWorkoutPartialedAt(existing.getLastWorkoutPartialedAt())
                .weeks(existing.getWeeks())
                .notes(existing.getNotes())
                .status(existing.getStatus())
                .generatedFrom(existing.getGeneratedFrom())
                .progressions(existing.getProgressions())
                .build();
    }
    
    /**
     * Creates a Mesocycle entity for finish operation.
     * Sets finishedAt timestamp while preserving other data.
     */
    public Mesocycle createForFinish(Mesocycle existing) {
        if (existing == null) {
            return null;
        }
        
        return Mesocycle.builder()
                .id(existing.getId())
                .mesocycleKey(existing.getMesocycleKey())
                .userId(existing.getUserId())
                .name(existing.getName())
                .days(existing.getDays())
                .unit(existing.getUnit())
                .sourceTemplate(existing.getSourceTemplate())
                .sourceMeso(existing.getSourceMeso())
                .microRirs(existing.getMicroRirs())
                .createdAt(existing.getCreatedAt())
                .updatedAt(Instant.now())
                .finishedAt(Instant.now()) // Set finished timestamp
                .deletedAt(existing.getDeletedAt())
                .firstMicroCompletedAt(existing.getFirstMicroCompletedAt())
                .firstWorkoutCompletedAt(existing.getFirstWorkoutCompletedAt())
                .firstExerciseCompletedAt(existing.getFirstExerciseCompletedAt())
                .firstSetCompletedAt(existing.getFirstSetCompletedAt())
                .lastMicroFinishedAt(existing.getLastMicroFinishedAt())
                .lastSetCompletedAt(existing.getLastSetCompletedAt())
                .lastSetSkippedAt(existing.getLastSetSkippedAt())
                .lastWorkoutCompletedAt(existing.getLastWorkoutCompletedAt())
                .lastWorkoutFinishedAt(existing.getLastWorkoutFinishedAt())
                .lastWorkoutSkippedAt(existing.getLastWorkoutSkippedAt())
                .lastWorkoutPartialedAt(existing.getLastWorkoutPartialedAt())
                .weeks(existing.getWeeks())
                .notes(existing.getNotes())
                .status(existing.getStatus())
                .generatedFrom(existing.getGeneratedFrom())
                .progressions(existing.getProgressions())
                .build();
    }
    
    private Unit stringToUnit(String unitString) {
        if (unitString == null) {
            return null;
        }
        try {
            return EnumConverter.stringToEnum(Unit.class, unitString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid unit string: " + unitString);
        }
    }
    
    private MgProgressionType stringToMgProgressionType(String typeString) {
        if (typeString == null) {
            return null;
        }
        try {
            return EnumConverter.stringToEnum(MgProgressionType.class, typeString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid progression type string: " + typeString);
        }
    }
    
    private String generateMesocycleKey() {
        return UUID.randomUUID().toString();
    }
}
