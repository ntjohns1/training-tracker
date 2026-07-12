package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.response.DayNoteResponse;
import com.noslen.training_tracker.model.day.DayNote;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * POJO Mapper for converting between DayNote entity and DayNoteResponse DTO.
 * Handles manual mapping logic and supports immutable model fields.
 */
@Component
public class DayNoteMapper {

    /**
     * Converts DayNote entity to DayNoteResponse DTO.
     *
     * @param entity the DayNote entity to convert
     * @return the converted DayNoteResponse DTO, or null if entity is null
     */
    public DayNoteResponse toPayload(DayNote entity) {
        if (entity == null) {
            return null;
        }

        return new DayNoteResponse(
                entity.getId(),
                entity.getDayId(), // Uses the derived property from entity
                entity.getNoteId(),
                entity.getPinned(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getText()
        );
    }

    /**
     * Converts a list of DayNote entities to a list of DayNoteResponse DTOs.
     *
     * @param entities the list of DayNote entities to convert
     * @return the converted list of DayNoteResponse DTOs, or null if input is null
     */
    public List<DayNoteResponse> toPayloadList(List<DayNote> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toPayload)
                .collect(Collectors.toList());
    }
}
