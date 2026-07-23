package com.noslen.training_tracker.mapper.progression;

import com.noslen.training_tracker.dto.progression.response.ProgressionResponse;
import com.noslen.training_tracker.model.progression.Progression;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProgressionMapper {

    /**
     * Converts Progression entity to ProgressionResponse
     */
    public ProgressionResponse toPayload(Progression entity) {
        if (entity == null) {
            return null;
        }

        return new ProgressionResponse(
                entity.getId(),
                entity.getMuscleGroupId(),
                entity.getMgProgressionType()
        );
    }

    /**
     * Converts a list of Progression entities to ProgressionPayloads
     */
    public List<ProgressionResponse> toPayloadList(List<Progression> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toPayload)
                .collect(Collectors.toList());
    }
}
