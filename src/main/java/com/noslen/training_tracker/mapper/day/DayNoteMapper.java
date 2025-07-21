package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.DayNotePayload;
import com.noslen.training_tracker.model.day.DayNote;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * POJO Mapper for converting between DayNote entity and DayNotePayload DTO.
 * Handles manual mapping logic and supports immutable model fields.
 */
@Component
public class DayNoteMapper {

    /**
     * Converts DayNotePayload DTO to DayNote entity.
     * Note: Day relationship is not set by this mapper and should be handled by the service layer.
     *
     * @param payload the DayNotePayload to convert
     * @return the converted DayNote entity, or null if payload is null
     */
    public DayNote toEntity(DayNotePayload payload) {
        if (payload == null) {
            return null;
        }

        return DayNote.builder()
                .id(payload.id())
                .noteId(payload.noteId())
                .pinned(payload.pinned())
                .createdAt(payload.createdAt())
                .updatedAt(payload.updatedAt())
                .text(payload.text())
                .build();
    }

    /**
     * Converts DayNote entity to DayNotePayload DTO.
     *
     * @param entity the DayNote entity to convert
     * @return the converted DayNotePayload DTO, or null if entity is null
     */
    public DayNotePayload toPayload(DayNote entity) {
        if (entity == null) {
            return null;
        }

        return new DayNotePayload(
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
     * Updates mutable fields of an existing DayNote entity from DayNotePayload.
     * Only updates fields that can be modified after creation.
     *
     * @param entity  the existing DayNote entity to update
     * @param payload the DayNotePayload containing update data
     */
    public void updateEntity(DayNote entity, DayNotePayload payload) {
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
     * @param payload  the DayNotePayload containing update data
     * @return a new DayNote entity with merged data
     */
    public DayNote mergeEntity(DayNote existing, DayNotePayload payload) {
        if (existing == null) {
            return toEntity(payload);
        }
        if (payload == null) {
            return existing;
        }

        return DayNote.builder()
                .id(existing.getId()) // Always preserve existing ID
                .day(existing.getDay()) // Preserve relationship
                .noteId(payload.noteId() != null && payload.noteId() != 0 ? payload.noteId() : existing.getNoteId())
                .pinned(payload.pinned() != null ? payload.pinned() : existing.getPinned())
                .createdAt(existing.getCreatedAt()) // Preserve creation timestamp
                .updatedAt(payload.updatedAt() != null ? payload.updatedAt() : existing.getUpdatedAt())
                .text(payload.text() != null ? payload.text() : existing.getText())
                .build();
    }

    /**
     * Converts a list of DayNote entities to a list of DayNotePayload DTOs.
     *
     * @param entities the list of DayNote entities to convert
     * @return the converted list of DayNotePayload DTOs, or null if input is null
     */
    public List<DayNotePayload> toPayloadList(List<DayNote> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toPayload)
                .collect(Collectors.toList());
    }

    /**
     * Converts a list of DayNotePayload DTOs to a list of DayNote entities.
     *
     * @param payloads the list of DayNotePayload DTOs to convert
     * @return the converted list of DayNote entities, or null if input is null
     */
    public List<DayNote> toEntityList(List<DayNotePayload> payloads) {
        if (payloads == null) {
            return null;
        }

        return payloads.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
