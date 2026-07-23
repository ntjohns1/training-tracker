package com.noslen.training_tracker.mapper.exercise;

import com.noslen.training_tracker.dto.exercise.response.ExerciseNoteResponse;
import com.noslen.training_tracker.model.exercise.ExerciseNote;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExerciseNoteMapper {

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
