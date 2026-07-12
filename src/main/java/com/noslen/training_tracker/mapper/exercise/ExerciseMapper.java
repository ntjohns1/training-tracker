package com.noslen.training_tracker.mapper.exercise;

import com.noslen.training_tracker.dto.exercise.response.ExerciseResponse;
import com.noslen.training_tracker.enums.ExerciseType;
import com.noslen.training_tracker.enums.MgSubType;
import com.noslen.training_tracker.model.exercise.Exercise;
import com.noslen.training_tracker.util.EnumConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExerciseMapper {

    private final ExerciseNoteMapper exerciseNoteMapper;

    public ExerciseMapper(ExerciseNoteMapper exerciseNoteMapper) {
        this.exerciseNoteMapper = exerciseNoteMapper;
    }

    /**
     * Convert ExerciseResponse to Exercise entity
     */
    public Exercise toEntity(ExerciseResponse payload) {
        if (payload == null) {
            return null;
        }

        Exercise exercise = new Exercise();
        exercise.setId(payload.id());
        exercise.setName(payload.name());
        exercise.setMuscleGroupId(payload.muscleGroupId());
        exercise.setYoutubeId(payload.youtubeId());
        exercise.setExerciseType(stringToExerciseType(payload.exerciseType()));
        exercise.setUserId(payload.userId());
        exercise.setCreatedAt(payload.createdAt());
        exercise.setUpdatedAt(payload.updatedAt());
        exercise.setDeletedAt(payload.deletedAt());
        exercise.setMgSubType(stringToMgSubType(payload.mgSubType()));
        
        // Convert nested ExerciseNotePayloads to ExerciseNote entities
        if (payload.notes() != null) {
            exercise.setNotes(payload.notes()
                    .stream()
                    .map(exerciseNoteMapper::toEntity)
                    .collect(Collectors.toList()));
        }
        
        return exercise;
    }

    /**
     * Convert Exercise entity to ExerciseResponse
     */
    public ExerciseResponse toPayload(Exercise entity) {
        if (entity == null) {
            return null;
        }

        return new ExerciseResponse(
                entity.getId(),
                entity.getName(),
                entity.getMuscleGroupId(),
                entity.getYoutubeId(),
                exerciseTypeToString(entity.getExerciseType()),
                entity.getUserId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt(),
                mgSubTypeToString(entity.getMgSubType()),
                // Convert nested ExerciseNote entities to ExerciseNotePayloads
                exerciseNoteMapper.toPayloadList(entity.getNotes())
        );
    }

    /**
     * Update existing Exercise entity with data from ExerciseResponse
     * Only updates mutable fields that have setters
     */
    public void updateEntity(Exercise existing, ExerciseResponse payload) {
        if (existing == null || payload == null) {
            return;
        }

        // Update fields that have setters (mutable fields)
        if (payload.name() != null) {
            existing.setName(payload.name());
        }
        if (payload.muscleGroupId() != null && !payload.muscleGroupId()
                .equals(0L)) {
            existing.setMuscleGroupId(payload.muscleGroupId());
        }
        if (payload.youtubeId() != null) {
            existing.setYoutubeId(payload.youtubeId());
        }
        if (payload.exerciseType() != null) {
            existing.setExerciseType(stringToExerciseType(payload.exerciseType()));
        }
        if (payload.userId() != null && !payload.userId()
                .equals(0L)) {
            existing.setUserId(payload.userId());
        }
        if (payload.mgSubType() != null) {
            existing.setMgSubType(stringToMgSubType(payload.mgSubType()));
        }
        if (payload.createdAt() != null) {
            existing.setCreatedAt(payload.createdAt());
        }
        if (payload.updatedAt() != null) {
            existing.setUpdatedAt(payload.updatedAt());
        }
        if (payload.deletedAt() != null) {
            existing.setDeletedAt(payload.deletedAt());
        }
        if (payload.notes() != null) {
            existing.setNotes(payload.notes()
                                      .stream()
                                      .map(exerciseNoteMapper::toEntity)
                                      .collect(Collectors.toList()));
        }
    }

    /**
     * Create a new Exercise entity by merging existing entity with payload data
     * This handles immutable fields by creating a new entity
     */
    public Exercise mergeEntity(Exercise existing, ExerciseResponse payload) {
        if (payload == null) {
            return existing;
        }
        if (existing == null) {
            return toEntity(payload);
        }

        Exercise exercise = new Exercise();
        // Keep existing ID - need to use reflection or constructor since there's no setId
        exercise.setId(existing.getId());
        exercise.setName(payload.name() != null ? payload.name() : existing.getName());
        exercise.setMuscleGroupId(payload.muscleGroupId() != null && !payload.muscleGroupId().equals(0L) ?
                payload.muscleGroupId() : existing.getMuscleGroupId());
        exercise.setYoutubeId(payload.youtubeId() != null ? payload.youtubeId() : existing.getYoutubeId());
        exercise.setExerciseType(payload.exerciseType() != null ? stringToExerciseType(payload.exerciseType()) :
                existing.getExerciseType());
        exercise.setUserId(payload.userId() != null && !payload.userId().equals(0L) ?
                payload.userId() : existing.getUserId());
        exercise.setCreatedAt(existing.getCreatedAt()); // Keep existing creation time
        exercise.setUpdatedAt(payload.updatedAt() != null ? payload.updatedAt() : existing.getUpdatedAt());
        exercise.setDeletedAt(payload.deletedAt() != null ? payload.deletedAt() : existing.getDeletedAt());
        exercise.setMgSubType(stringToMgSubType(payload.mgSubType()) != null ? stringToMgSubType(payload.mgSubType()) : existing.getMgSubType());
        
        if (payload.notes() != null) {
            exercise.setNotes(payload.notes()
                    .stream()
                    .map(exerciseNoteMapper::toEntity)
                    .collect(Collectors.toList()));
        } else {
            exercise.setNotes(existing.getNotes());
        }
        
        return exercise;
    }

    /**
     * Convert list of Exercise entities to list of ExercisePayloads
     */
    public List<ExerciseResponse> toPayloadList(List<Exercise> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toPayload)
                .collect(Collectors.toList());
    }

    private ExerciseType stringToExerciseType(String exerciseType) {
        if (exerciseType == null) {
            return null;
        }
        try {
            return EnumConverter.stringToEnum(ExerciseType.class,
                                              exerciseType);
        } catch (IllegalArgumentException e) {
            // If that fails, try to find by the enum's getValue() method
            for (ExerciseType et : ExerciseType.values()) {
                if (et.getValue()
                        .equals(exerciseType)) {
                    return et;
                }
            }
            throw new IllegalArgumentException(e);
        }
    }

    private String exerciseTypeToString(ExerciseType exerciseType) {
        if (exerciseType == null) {
            return null;
        }
        return EnumConverter.enumToSerializedValue(exerciseType);
    }

    private MgSubType stringToMgSubType(String mgSubType) {
        if (mgSubType == null) {
            return null;
        }
        try {
            return EnumConverter.stringToEnum(MgSubType.class,
                                              mgSubType);
        } catch (IllegalArgumentException e) {
            // If that fails, try to find by the enum's getValue() method
            for (MgSubType mt : MgSubType.values()) {
                if (mt.getValue()
                        .equals(mgSubType)) {
                    return mt;
                }
            }
            throw new IllegalArgumentException(e);
        }
    }

    private String mgSubTypeToString(MgSubType mgSubType) {
        if (mgSubType == null) {
            return null;
        }
        return EnumConverter.enumToSerializedValue(mgSubType);
    }   
}
