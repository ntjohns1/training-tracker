package com.noslen.training_tracker.mapper.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.MesoNotePayload;
import com.noslen.training_tracker.model.mesocycle.MesoNote;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * POJO Mapper for converting between MesoNote entity and MesoNotePayload DTO
 */
@Component
public class MesoNoteMapper {

    /**
     * Converts MesoNotePayload to MesoNote entity
     */
    public MesoNote toEntity(MesoNotePayload payload) {
        if (payload == null) {
            return null;
        }

        MesoNote.MesoNoteBuilder builder = MesoNote.builder()
                .id(payload.id())
                .noteId(payload.noteId())
                .text(payload.text())
                .createdAt(payload.createdAt())
                .updatedAt(payload.updatedAt());

        // Set mesocycle relationship if mesoId is provided
        if (payload.mesoId() != null) {
            builder.mesocycle(Mesocycle.builder().id(payload.mesoId()).build());
        }

        return builder.build();
    }

    /**
     * Converts MesoNote entity to MesoNotePayload
     */
    public MesoNotePayload toPayload(MesoNote entity) {
        if (entity == null) {
            return null;
        }

        Long mesoId = null;
        if (entity.getMesocycle() != null) {
            mesoId = entity.getMesocycle().getId();
        }

        return new MesoNotePayload(
                entity.getId(),
                mesoId,
                entity.getNoteId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getText()
        );
    }

    /**
     * Updates an existing MesoNote entity with data from MesoNotePayload
     * Note: MesoNote is immutable, so this method is a no-op.
     * Use mergeEntity() instead to create a new entity with updated values.
     */
    public void updateEntity(MesoNote existing, MesoNotePayload payload) {
        // MesoNote is immutable - no mutable fields to update
        // Use mergeEntity() to create a new entity with updated values
    }

    /**
     * Creates a new MesoNote entity by merging existing entity with payload data
     * This method handles immutable fields by creating a new entity instance
     */
    public MesoNote mergeEntity(MesoNote existing, MesoNotePayload payload) {
        if (existing == null || payload == null) {
            return null;
        }

        MesoNote.MesoNoteBuilder builder = MesoNote.builder()
                .id(existing.getId())
                .noteId(payload.noteId() != null ? payload.noteId() : existing.getNoteId())
                .text(payload.text() != null ? payload.text() : existing.getText())
                .createdAt(existing.getCreatedAt()) // Preserve original creation time
                .updatedAt(Instant.now()); // Set current time for update

        // Set mesocycle relationship
        if (payload.mesoId() != null) {
            builder.mesocycle(Mesocycle.builder().id(payload.mesoId()).build());
        } else if (existing.getMesocycle() != null) {
            builder.mesocycle(existing.getMesocycle());
        }

        return builder.build();
    }
}
