package com.noslen.training_tracker.mapper.progression;

import com.noslen.training_tracker.dto.progression.response.MuscleGroupResponse;
import com.noslen.training_tracker.model.progression.MuscleGroup;
import com.noslen.training_tracker.enums.MgName;
import com.noslen.training_tracker.util.EnumConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MuscleGroupMapper {

    /**
     * Converts MuscleGroup entity to MuscleGroupResponse
     */
    public MuscleGroupResponse toPayload(MuscleGroup entity) {
        if (entity == null) {
            return null;
        }

        return new MuscleGroupResponse(
                entity.getId(),
                mgNameToString(entity.getName()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Converts a list of MuscleGroup entities to MuscleGroupPayloads
     */
    public List<MuscleGroupResponse> toPayloadList(List<MuscleGroup> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toPayload)
                .collect(Collectors.toList());
    }

    /**
     * Converts MgName enum to String using getValue() method
     * Returns capitalized format (e.g., "Chest", "Quads")
     */
    private String mgNameToString(MgName mgName) {
        if (mgName == null) {
            return null;
        }
        return EnumConverter.enumToSerializedValue(mgName);
    }
}
