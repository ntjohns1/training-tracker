package com.noslen.training_tracker.mapper.exercise;

import com.noslen.training_tracker.dto.exercise.ExerciseNotePayload;
import com.noslen.training_tracker.model.exercise.ExerciseNote;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExerciseNoteMapper {

    /**
     * Convert ExerciseNotePayload to ExerciseNote entity
     */
    public ExerciseNote toEntity(ExerciseNotePayload payload) {
        if (payload == null) {
            return null;
        }

        return ExerciseNote.builder()
                .id(payload.id())
                .userId(payload.userId())
                .noteId(payload.noteId())
                .createdAt(payload.createdAt())
                .updatedAt(payload.updatedAt())
                .text(payload.text())
                // Note: exercise and dayExercise relationships are handled separately
                .build();
    }

    /**
     * Convert ExerciseNote entity to ExerciseNotePayload
     */
    public ExerciseNotePayload toPayload(ExerciseNote entity) {
        if (entity == null) {
            return null;
        }

        return new ExerciseNotePayload(
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
     * Update existing ExerciseNote entity with data from ExerciseNotePayload
     * Only updates mutable fields that have setters
     */
    public void updateEntity(ExerciseNote existing, ExerciseNotePayload payload) {
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
    public ExerciseNote mergeEntity(ExerciseNote existing, ExerciseNotePayload payload) {
        if (payload == null) {
            return existing;
        }
        if (existing == null) {
            return toEntity(payload);
        }

        return ExerciseNote.builder()
                .id(existing.getId()) // Keep existing ID
                .userId(payload.userId() != null && !payload.userId().equals(0L) ? payload.userId() : existing.getUserId())
                .noteId(payload.noteId() != null && !payload.noteId().equals(0L) ? payload.noteId() : existing.getNoteId())
                .exercise(existing.getExercise()) // Keep existing relationship
                .dayExercise(existing.getDayExercise()) // Keep existing relationship
                .createdAt(existing.getCreatedAt()) // Keep existing creation time
                .updatedAt(payload.updatedAt() != null ? payload.updatedAt() : existing.getUpdatedAt())
                .text(payload.text() != null ? payload.text() : existing.getText())
                .build();
    }

    /**
     * Convert list of ExerciseNote entities to list of ExerciseNotePayloads
     */
    public List<ExerciseNotePayload> toPayloadList(List<ExerciseNote> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toPayload)
                .collect(Collectors.toList());
    }
}
