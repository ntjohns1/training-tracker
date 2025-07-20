package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.DayExercisePayload;
import com.noslen.training_tracker.model.day.DayExercise;
import org.mapstruct.*;

import java.time.Instant;
import java.util.List;

@Mapper(
    componentModel = "spring",
    uses = {ExerciseSetMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    builder = @Builder(disableBuilder = true)
)
public interface DayExerciseMapper {

    /**
     * Converts DayExercisePayload to DayExercise entity
     */
    @Mapping(target = "day", source = "dayId", qualifiedByName = "dayIdToDay")
    @Mapping(target = "exercise", source = "exerciseId", qualifiedByName = "exerciseIdToExercise")
    @Mapping(target = "muscleGroup", source = "muscleGroupId", qualifiedByName = "muscleGroupIdToMuscleGroup")
    @Mapping(target = "createdAt", expression = "java(payload.createdAt() != null ? payload.createdAt() : java.time.Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(payload.updatedAt() != null ? payload.updatedAt() : java.time.Instant.now())")
    DayExercise toEntity(DayExercisePayload payload);

    /**
     * Converts DayExercise entity to DayExercisePayload
     */
    @Mapping(target = "dayId", source = "day.id")
    @Mapping(target = "exerciseId", source = "exercise.id")
    @Mapping(target = "muscleGroupId", source = "muscleGroup.id")
    DayExercisePayload toPayload(DayExercise entity);

    /**
     * Updates an existing DayExercise entity with data from DayExercisePayload
     */
    @Mapping(target = "id", ignore = true) // Keep existing ID
    @Mapping(target = "day", source = "dayId", qualifiedByName = "dayIdToDay")
    @Mapping(target = "exercise", source = "exerciseId", qualifiedByName = "exerciseIdToExercise")
    @Mapping(target = "muscleGroup", source = "muscleGroupId", qualifiedByName = "muscleGroupIdToMuscleGroup")
    @Mapping(target = "createdAt", ignore = true) // Keep original creation time
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "sets", ignore = true) // Keep existing sets - should be managed separately
    void updateEntity(@MappingTarget DayExercise existing, DayExercisePayload payload);

    /**
     * Converts a list of DayExercise entities to DayExercisePayloads
     */
    List<DayExercisePayload> toPayloadList(List<DayExercise> entities);

    // Custom mapping methods for entity relationships
    @Named("dayIdToDay")
    default com.noslen.training_tracker.model.day.Day dayIdToDay(Long dayId) {
        if (dayId == null) return null;
        return com.noslen.training_tracker.model.day.Day.builder().id(dayId).build();
    }

    @Named("exerciseIdToExercise")
    default com.noslen.training_tracker.model.exercise.Exercise exerciseIdToExercise(Long exerciseId) {
        if (exerciseId == null) return null;
        return com.noslen.training_tracker.model.exercise.Exercise.builder().id(exerciseId).build();
    }

    @Named("muscleGroupIdToMuscleGroup")
    default com.noslen.training_tracker.model.day.DayMuscleGroup muscleGroupIdToMuscleGroup(Long muscleGroupId) {
        if (muscleGroupId == null) return null;
        return com.noslen.training_tracker.model.day.DayMuscleGroup.builder().id(muscleGroupId).build();
    }
}
