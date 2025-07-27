package com.noslen.training_tracker.mapper.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.MesoNoteResponse;
import com.noslen.training_tracker.model.mesocycle.MesoNote;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * POJO Mapper for converting between MesoNote entity and MesoNoteResponse DTO
 */
@Component
public class MesoNoteMapper {

    /**
     * Converts MesoNoteResponse to MesoNote entity
     */
    public MesoNote toEntity(MesoNoteResponse payload) {
        if (payload == null) {
            return null;
        }

        MesoNote mesoNote = new MesoNote();
        mesoNote.setId(payload.id());
        mesoNote.setNoteId(payload.noteId());
        mesoNote.setText(payload.text());
        mesoNote.setCreatedAt(payload.createdAt());
        mesoNote.setUpdatedAt(payload.updatedAt());

        // Set mesocycle relationship if mesoId is provided
        if (payload.mesoId() != null) {
            mesoNote.setMesocycle(Mesocycle.builder().id(payload.mesoId()).build());
        }

        return mesoNote;
    }

    /**
     * Converts MesoNote entity to MesoNoteResponse
     */
    public MesoNoteResponse toPayload(MesoNote entity) {
        if (entity == null) {
            return null;
        }

        Long mesoId = null;
        if (entity.getMesocycle() != null) {
            mesoId = entity.getMesocycle().getId();
        }

        return new MesoNoteResponse(
                entity.getId(),
                mesoId,
                entity.getNoteId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getText()
        );
    }

    /**
     * Updates an existing MesoNote entity with data from MesoNoteResponse
     * Note: MesoNote is immutable, so this method is a no-op.
     * Use mergeEntity() instead to create a new entity with updated values.
     */
    public void updateEntity(MesoNote existing, MesoNoteResponse payload) {
        // MesoNote is immutable - no mutable fields to update
        // Use mergeEntity() to create a new entity with updated values
    }

    /**
     * Creates a new MesoNote entity by merging existing entity with payload data
     * This method handles immutable fields by creating a new entity instance
     */
    public MesoNote mergeEntity(MesoNote existing, MesoNoteResponse payload) {
        if (existing == null || payload == null) {
            return null;
        }

        MesoNote mesoNote = new MesoNote();
        mesoNote.setId(existing.getId());
        mesoNote.setNoteId(payload.noteId() != null ? payload.noteId() : existing.getNoteId());
        mesoNote.setText(payload.text() != null ? payload.text() : existing.getText());
        mesoNote.setCreatedAt(existing.getCreatedAt()); // Preserve original creation time
        mesoNote.setUpdatedAt(Instant.now()); // Set current time for update

        // Set mesocycle relationship
        if (payload.mesoId() != null) {
            mesoNote.setMesocycle(Mesocycle.builder().id(payload.mesoId()).build());
        } else if (existing.getMesocycle() != null) {
            mesoNote.setMesocycle(existing.getMesocycle());
        }

        return mesoNote;
    }
}
