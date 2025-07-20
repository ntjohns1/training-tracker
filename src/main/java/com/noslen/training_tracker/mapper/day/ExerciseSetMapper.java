package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.ExerciseSetPayload;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.model.day.ExerciseSet;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    builder = @Builder(disableBuilder = true)
)
public interface ExerciseSetMapper {

    /**
     * Converts ExerciseSetPayload to ExerciseSet entity
     */
    @Mapping(target = "dayExercise", ignore = true)
    ExerciseSet toEntity(ExerciseSetPayload payload);

    /**
     * Converts ExerciseSetPayload to ExerciseSet entity with DayExercise reference
     */
    ExerciseSet toEntity(ExerciseSetPayload payload, @Context DayExercise dayExercise);

    /**
     * Custom mapping method to handle DayExercise context
     */
    @AfterMapping
    default void setDayExercise(@MappingTarget ExerciseSet target, ExerciseSetPayload payload, @Context DayExercise dayExercise) {
        if (dayExercise != null) {
            target.setDayExercise(dayExercise);
        }
    }

    /**
     * Converts ExerciseSet entity to ExerciseSetPayload
     */
    @Mapping(target = "dayExerciseId", source = "dayExercise.id")
    ExerciseSetPayload toPayload(ExerciseSet entity);

    /**
     * Updates an existing ExerciseSet entity with data from ExerciseSetPayload
     */
    @Mapping(target = "id", ignore = true) // Keep existing ID
    @Mapping(target = "dayExercise", ignore = true) // Keep existing relationship
    @Mapping(target = "createdAt", ignore = true) // Keep original creation time
    void updateEntity(@MappingTarget ExerciseSet existing, ExerciseSetPayload payload);

    /**
     * Converts a list of ExerciseSet entities to ExerciseSetPayloads
     */
    List<ExerciseSetPayload> toPayloadList(List<ExerciseSet> entities);
}
