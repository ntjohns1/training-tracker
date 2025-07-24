package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.ExerciseSetPayload;
import com.noslen.training_tracker.enums.Status;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.model.day.ExerciseSet;
import com.noslen.training_tracker.model.day.types.SetType;
import com.noslen.training_tracker.util.EnumConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExerciseSetMapper {

    /**
     * Converts ExerciseSetPayload to ExerciseSet entity
     */
    public ExerciseSet toEntity(ExerciseSetPayload payload) {
        if (payload == null) {
            return null;
        }

        return ExerciseSet.builder()
                .id(payload.id())
                .position(payload.position())
                .setType(EnumConverter.stringToEnum(SetType.class, payload.setType()))
                .weight(payload.weight())
                .weightTarget(payload.weightTarget())
                .weightTargetMin(payload.weightTargetMin())
                .weightTargetMax(payload.weightTargetMax())
                .reps(payload.reps())
                .repsTarget(payload.repsTarget())
                .bodyweight(payload.bodyweight())
                .unit(payload.unit())
                .createdAt(payload.createdAt())
                .finishedAt(payload.finishedAt())
                .status(EnumConverter.stringToEnum(Status.class, payload.status()))
                .build();
    }

    /**
     * Converts ExerciseSetPayload to ExerciseSet entity with DayExercise reference
     */
    public ExerciseSet toEntity(ExerciseSetPayload payload, DayExercise dayExercise) {
        ExerciseSet entity = toEntity(payload);
        if (entity != null && dayExercise != null) {
            entity.setDayExercise(dayExercise);
        }
        return entity;
    }

    /**
     * Converts ExerciseSet entity to ExerciseSetPayload
     */
    public ExerciseSetPayload toPayload(ExerciseSet entity) {
        if (entity == null) {
            return null;
        }

        return new ExerciseSetPayload(
                entity.getId(),
                entity.getDayExerciseId(),
                entity.getPosition(),
                EnumConverter.enumToString(entity.getSetType()),
                entity.getWeight(),
                entity.getWeightTarget(),
                entity.getWeightTargetMin(),
                entity.getWeightTargetMax(),
                entity.getReps(),
                entity.getRepsTarget(),
                entity.getBodyweight(),
                entity.getUnit(),
                entity.getCreatedAt(),
                entity.getFinishedAt(),
                EnumConverter.enumToString(entity.getStatus())
        );
    }

    /**
     * Updates an existing ExerciseSet entity with data from ExerciseSetPayload
     * Note: Since ExerciseSet has limited mutable fields, this method only updates those with setters
     */
    public void updateEntity(ExerciseSet existing, ExerciseSetPayload payload) {
        if (existing == null || payload == null) {
            return;
        }

        // Update mutable fields that have setters
        if (payload.createdAt() != null) {
            existing.setCreatedAt(payload.createdAt());
        }
        if (payload.finishedAt() != null) {
            existing.setFinishedAt(payload.finishedAt());
        }
        if (payload.status() != null) {
            existing.setStatus(EnumConverter.stringToEnum(Status.class, payload.status()));
        }

        // Note: Other fields like position, setType, weight, etc. don't have setters
        // If these need to be updated, a new entity should be created using mergeEntity method
    }

    /**
     * Creates a new ExerciseSet entity by merging existing entity with payload data
     * This is useful when you need to update immutable fields
     */
    public ExerciseSet mergeEntity(ExerciseSet existing, ExerciseSetPayload payload) {
        if (existing == null) {
            return toEntity(payload);
        }
        if (payload == null) {
            return existing;
        }

        return ExerciseSet.builder()
                .id(existing.getId()) // Keep existing ID
                .dayExercise(existing.getDayExercise()) // Keep existing relationship
                .position(payload.position() != null ? payload.position() : existing.getPosition())
                .setType(payload.setType() != null ? EnumConverter.stringToEnum(SetType.class, payload.setType()) : existing.getSetType())
                .weight(payload.weight() != null ? payload.weight() : existing.getWeight())
                .weightTarget(payload.weightTarget() != null ? payload.weightTarget() : existing.getWeightTarget())
                .weightTargetMin(payload.weightTargetMin() != null ? payload.weightTargetMin() : existing.getWeightTargetMin())
                .weightTargetMax(payload.weightTargetMax() != null ? payload.weightTargetMax() : existing.getWeightTargetMax())
                .reps(payload.reps() != null ? payload.reps() : existing.getReps())
                .repsTarget(payload.repsTarget() != null ? payload.repsTarget() : existing.getRepsTarget())
                .bodyweight(payload.bodyweight() != null ? payload.bodyweight() : existing.getBodyweight())
                .unit(payload.unit() != null ? payload.unit() : existing.getUnit())
                .createdAt(payload.createdAt() != null ? payload.createdAt() : existing.getCreatedAt())
                .finishedAt(payload.finishedAt() != null ? payload.finishedAt() : existing.getFinishedAt())
                .status(EnumConverter.stringToEnum(Status.class, payload.status()))
                .build();
    }

    /**
     * Converts a list of ExerciseSet entities to ExerciseSetPayloads
     */
    public List<ExerciseSetPayload> toPayloadList(List<ExerciseSet> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toPayload)
                .collect(Collectors.toList());
    }
}
