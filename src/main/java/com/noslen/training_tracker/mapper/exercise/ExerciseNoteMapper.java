package com.noslen.training_tracker.mapper.exercise;

import com.noslen.training_tracker.dto.exercise.response.ExerciseNoteResponse;
import com.noslen.training_tracker.model.exercise.ExerciseNote;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExerciseNoteMapper {

    /**
     * Convert ExerciseNoteResponse to ExerciseNote entity
     */
    public ExerciseNote toEntity(ExerciseNoteResponse payload) {
        if (payload == null) {
            return null;
        }

        ExerciseNote exerciseNote = new ExerciseNote();
        exerciseNote.setId(payload.id());
        exerciseNote.setUserId(payload.userId());
        exerciseNote.setNoteId(payload.noteId());
        exerciseNote.setCreatedAt(payload.createdAt());
        exerciseNote.setUpdatedAt(payload.updatedAt());
        exerciseNote.setText(payload.text());
        // Note: exercise and dayExercise relationships are handled separately
        
        return exerciseNote;
    }

    /**
     * Convert ExerciseNote entity to ExerciseNoteResponse
     */
    public ExerciseNoteResponse toPayload(ExerciseNote entity) {
        if (entity == null) {
            return null;
        }

        return new ExerciseNoteResponse(
                entity.getId(),
                entity.getExerciseId(), // Uses the @JsonProperty method
                entity.getUserId(),
                entity.getNoteId(),
                entity.getDayExerciseId(), // Uses the @JsonProperty method
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getText()
        );
    }

    /**
     * Update existing ExerciseNote entity with data from ExerciseNoteResponse
     * Only updates mutable fields that have setters
     */
    public void updateEntity(ExerciseNote existing, ExerciseNoteResponse payload) {
        if (existing == null || payload == null) {
            return;
        }

        // Only update fields that have setters (mutable fields)
        if (payload.text() != null) {
            existing.setText(payload.text());
        }
        if (payload.updatedAt() != null) {
            existing.setUpdatedAt(payload.updatedAt());
        }
    }

    /**
     * Create a new ExerciseNote entity by merging existing entity with payload data
     * This handles immutable fields by creating a new entity
     */
    public ExerciseNote mergeEntity(ExerciseNote existing, ExerciseNoteResponse payload) {
        if (payload == null) {
            return existing;
        }
        if (existing == null) {
            return toEntity(payload);
        }

        ExerciseNote exerciseNote = new ExerciseNote();
        exerciseNote.setId(existing.getId()); // Keep existing ID
        exerciseNote.setUserId(payload.userId() != null && !payload.userId().equals(0L) ? payload.userId() : existing.getUserId());
        exerciseNote.setNoteId(payload.noteId() != null && !payload.noteId().equals(0L) ? payload.noteId() : existing.getNoteId());
        exerciseNote.setExercise(existing.getExercise()); // Keep existing relationship
        exerciseNote.setDayExercise(existing.getDayExercise()); // Keep existing relationship
        exerciseNote.setCreatedAt(existing.getCreatedAt()); // Keep existing creation time
        exerciseNote.setUpdatedAt(payload.updatedAt() != null ? payload.updatedAt() : existing.getUpdatedAt());
        exerciseNote.setText(payload.text() != null ? payload.text() : existing.getText());
        
        return exerciseNote;
    }

    /**
     * Convert list of ExerciseNote entities to list of ExerciseNotePayloads
     */
    public List<ExerciseNoteResponse> toPayloadList(List<ExerciseNote> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toPayload)
                .collect(Collectors.toList());
    }
}
