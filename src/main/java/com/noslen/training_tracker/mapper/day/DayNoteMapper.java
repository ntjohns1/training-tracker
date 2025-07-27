package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.DayNoteResponse;
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
     * Converts DayNoteResponse DTO to DayNote entity.
     * Note: Day relationship is not set by this mapper and should be handled by the service layer.
     *
     * @param payload the DayNoteResponse to convert
     * @return the converted DayNote entity, or null if payload is null
     */
    public DayNote toEntity(DayNoteResponse payload) {
        if (payload == null) {
            return null;
        }

        DayNote dayNote = new DayNote();
        dayNote.setId(payload.id());
        dayNote.setNoteId(payload.noteId());
        dayNote.setPinned(payload.pinned());
        dayNote.setCreatedAt(payload.createdAt());
        dayNote.setUpdatedAt(payload.updatedAt());
        dayNote.setText(payload.text());
        return dayNote;
    }

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
     * Updates mutable fields of an existing DayNote entity from DayNoteResponse.
     * Only updates fields that can be modified after creation.
     *
     * @param entity  the existing DayNote entity to update
     * @param payload the DayNoteResponse containing update data
     */
    public void updateEntity(DayNote entity, DayNoteResponse payload) {
        if (entity == null || payload == null) {
            return;
        }

        // Update mutable fields
        if (payload.text() != null) {
            entity.setText(payload.text());
        }
        if (payload.updatedAt() != null) {
            entity.setUpdatedAt(payload.updatedAt());
        }
        if (payload.createdAt() != null) {
            entity.setCreatedAt(payload.createdAt());
        }
    }

    /**
     * Creates a new DayNote entity by merging existing entity with payload updates.
     * Used when immutable fields need to be changed.
     *
     * @param existing the existing DayNote entity
     * @param payload  the DayNoteResponse containing update data
     * @return a new DayNote entity with merged data
     */
    public DayNote mergeEntity(DayNote existing, DayNoteResponse payload) {
        if (existing == null) {
            return toEntity(payload);
        }
        if (payload == null) {
            return existing;
        }

        DayNote dayNote = new DayNote();
        dayNote.setId(existing.getId()); // Always preserve existing ID
        dayNote.setDay(existing.getDay()); // Preserve relationship
        dayNote.setNoteId(payload.noteId() != null && payload.noteId() != 0 ? payload.noteId() : existing.getNoteId());
        dayNote.setPinned(payload.pinned() != null ? payload.pinned() : existing.getPinned());
        dayNote.setCreatedAt(existing.getCreatedAt()); // Preserve creation timestamp
        dayNote.setUpdatedAt(payload.updatedAt() != null ? payload.updatedAt() : existing.getUpdatedAt());
        dayNote.setText(payload.text() != null ? payload.text() : existing.getText());
        return dayNote;
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

    /**
     * Converts a list of DayNoteResponse DTOs to a list of DayNote entities.
     *
     * @param payloads the list of DayNoteResponse DTOs to convert
     * @return the converted list of DayNote entities, or null if input is null
     */
    public List<DayNote> toEntityList(List<DayNoteResponse> payloads) {
        if (payloads == null) {
            return null;
        }

        return payloads.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
