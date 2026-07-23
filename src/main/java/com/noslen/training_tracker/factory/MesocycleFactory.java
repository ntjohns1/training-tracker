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
    
//    TODO: use services or EntityManager instead of repos for all instance
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
                .microRirs(microRirsFor(request.weeks())) // Per-week RIR digits; last digit 8 = deload
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
        
        // Build Days and Progressions referencing THIS Mesocycle instance, then populate its
        // (mutable) collections in place. Returning this same instance keeps the whole graph
        // pointing at one Mesocycle, so a cascading save doesn't hit a transient copy.
        List<Day> daysList = createDaysFromRequest(request, mesocycle, now);
        Map<Long, Progression> progressionsMap = createProgressionsFromRequest(request, mesocycle, now);

        mesocycle.getWeeks().addAll(daysList);
        mesocycle.getProgressions().putAll(progressionsMap);
        return mesocycle;
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

                // Build the nested entities against THIS day and populate its collections in place,
                // so the persisted graph shares one Day instance (mappedBy back-references resolve).
                List<DayExercise> dayExercises = createDayExercisesForDay(day, dayRequest, now);
                Set<DayMuscleGroup> dayMuscleGroups = createDayMuscleGroupsForDay(day, dayExercises, now);

                day.getExercises().addAll(dayExercises);
                day.getMuscleGroups().addAll(dayMuscleGroups);

                allDays.add(day);
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

            // The DayExercise carries its muscle group (from the exercise) so progression queries
            // that filter exercises/sets by muscle group can resolve it.
            MuscleGroup muscleGroup = exercise.getMuscleGroupId() != null
                    ? muscleGroupRepo.findById(exercise.getMuscleGroupId())
                        .orElseThrow(() -> new IllegalArgumentException("MuscleGroup not found with ID: " + exercise.getMuscleGroupId()))
                    : null;

            DayExercise dayExercise = DayExercise.builder()
                    .day(day)
                    .exercise(exercise)
                    .muscleGroup(muscleGroup)
                    .position(i + 1) // 1-indexed position
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            dayExercises.add(dayExercise);
        }
        return dayExercises;
    }
    
    private Set<DayMuscleGroup> createDayMuscleGroupsForDay(Day day, List<DayExercise> dayExercises, Instant now) {
        Set<DayMuscleGroup> dayMuscleGroups = new HashSet<>();
        Set<Long> processedMuscleGroups = new HashSet<>();

        for (DayExercise dayExercise : dayExercises) {
            // Derive the muscle group from the exercise's catalog muscleGroupId; one
            // DayMuscleGroup per distinct muscle group trained on this day.
            Exercise exercise = dayExercise.getExercise();
            Long muscleGroupId = exercise != null ? exercise.getMuscleGroupId() : null;

            if (muscleGroupId == null || !processedMuscleGroups.add(muscleGroupId)) {
                continue;
            }

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
    
    /**
     * The per-microcycle RIR scheme, packed as decimal digits (read left→right, one per week;
     * final {@code 8} = deload). RP derives this from the total week count (4–8). E.g. a 5-week
     * meso is {@code 32108} = RIR 3,2,1,0 then deload.
     */
    private long microRirsFor(int weeks) {
        return switch (weeks) {
            case 4 -> 2108L;
            case 5 -> 32108L;
            case 6 -> 332108L;
            case 7 -> 3322108L;
            case 8 -> 33221108L;
            default -> throw new IllegalArgumentException(
                    "Mesocycle weeks must be between 4 and 8, was: " + weeks);
        };
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
