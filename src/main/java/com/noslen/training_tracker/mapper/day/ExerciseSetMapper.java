package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.response.ExerciseSetResponse;
import com.noslen.training_tracker.enums.Status;
import com.noslen.training_tracker.enums.Unit;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.model.day.ExerciseSet;
import com.noslen.training_tracker.enums.SetType;
import com.noslen.training_tracker.util.EnumConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExerciseSetMapper {

    /**
     * Converts ExerciseSetResponse to ExerciseSet entity
     */
    public ExerciseSet toEntity(ExerciseSetResponse payload) {
        if (payload == null) {
            return null;
        }

        ExerciseSet exerciseSet = new ExerciseSet();
        exerciseSet.setId(payload.id());
        exerciseSet.setPosition(payload.position());
        exerciseSet.setSetType(EnumConverter.stringToEnum(SetType.class, payload.setType()));
        exerciseSet.setWeight(payload.weight());
        exerciseSet.setWeightTarget(payload.weightTarget());
        exerciseSet.setWeightTargetMin(payload.weightTargetMin());
        exerciseSet.setWeightTargetMax(payload.weightTargetMax());
        exerciseSet.setReps(payload.reps());
        exerciseSet.setRepsTarget(payload.repsTarget());
        exerciseSet.setBodyweight(payload.bodyweight());
        exerciseSet.setUnit(stringToUnit(payload.unit()));
        exerciseSet.setCreatedAt(payload.createdAt());
        exerciseSet.setFinishedAt(payload.finishedAt());
        exerciseSet.setStatus(EnumConverter.stringToEnum(Status.class, payload.status()));
        return exerciseSet;
    }

    /**
     * Converts ExerciseSetResponse to ExerciseSet entity with DayExercise reference
     */
    public ExerciseSet toEntity(ExerciseSetResponse payload, DayExercise dayExercise) {
        ExerciseSet entity = toEntity(payload);
        if (entity != null && dayExercise != null) {
            entity.setDayExercise(dayExercise);
        }
        return entity;
    }

    /**
     * Converts ExerciseSet entity to ExerciseSetResponse
     */
    public ExerciseSetResponse toPayload(ExerciseSet entity) {
        if (entity == null) {
            return null;
        }

        return new ExerciseSetResponse(
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
                unitToString(entity.getUnit()),
                entity.getCreatedAt(),
                entity.getFinishedAt(),
                EnumConverter.enumToString(entity.getStatus())
        );
    }

    /**
     * Updates an existing ExerciseSet entity with data from ExerciseSetResponse
     * Note: Since ExerciseSet has limited mutable fields, this method only updates those with setters
     */
    public void updateEntity(ExerciseSet existing, ExerciseSetResponse payload) {
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
    public ExerciseSet mergeEntity(ExerciseSet existing, ExerciseSetResponse payload) {
        if (existing == null) {
            return toEntity(payload);
        }
        if (payload == null) {
            return existing;
        }

        ExerciseSet exerciseSet = new ExerciseSet();
        exerciseSet.setId(existing.getId()); // Keep existing ID
        exerciseSet.setDayExercise(existing.getDayExercise()); // Keep existing relationship
        exerciseSet.setPosition(payload.position() != null ? payload.position() : existing.getPosition());
        exerciseSet.setSetType(payload.setType() != null ? EnumConverter.stringToEnum(SetType.class, payload.setType()) : existing.getSetType());
        exerciseSet.setWeight(payload.weight() != null ? payload.weight() : existing.getWeight());
        exerciseSet.setWeightTarget(payload.weightTarget() != null ? payload.weightTarget() : existing.getWeightTarget());
        exerciseSet.setWeightTargetMin(payload.weightTargetMin() != null ? payload.weightTargetMin() : existing.getWeightTargetMin());
        exerciseSet.setWeightTargetMax(payload.weightTargetMax() != null ? payload.weightTargetMax() : existing.getWeightTargetMax());
        exerciseSet.setReps(payload.reps() != null ? payload.reps() : existing.getReps());
        exerciseSet.setRepsTarget(payload.repsTarget() != null ? payload.repsTarget() : existing.getRepsTarget());
        exerciseSet.setBodyweight(payload.bodyweight() != null ? payload.bodyweight() : existing.getBodyweight());
        exerciseSet.setUnit(stringToUnit(payload.unit()) != null ? stringToUnit(payload.unit()) : existing.getUnit());
        exerciseSet.setCreatedAt(payload.createdAt() != null ? payload.createdAt() : existing.getCreatedAt());
        exerciseSet.setFinishedAt(payload.finishedAt() != null ? payload.finishedAt() : existing.getFinishedAt());
        exerciseSet.setStatus(EnumConverter.stringToEnum(Status.class, payload.status()));
        return exerciseSet;
    }

    /**
     * Converts a list of ExerciseSet entities to ExerciseSetPayloads
     */
    public List<ExerciseSetResponse> toPayloadList(List<ExerciseSet> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toPayload)
                .collect(Collectors.toList());
    }

    public Unit stringToUnit(String unit) {
        if (unit == null) {
            return null;
        }
        try {
            return EnumConverter.stringToEnum(Unit.class, unit);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid unit string: " + unit);
        }
    }

    public String unitToString(Unit unit) {
        if (unit == null) {
            return null;
        }
        return EnumConverter.enumToSerializedValue(unit);
    }   
}
