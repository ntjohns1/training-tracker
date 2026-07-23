package com.noslen.training_tracker.mapper.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.response.MesoNoteResponse;
import com.noslen.training_tracker.model.mesocycle.MesoNote;
import org.springframework.stereotype.Component;

/**
 * POJO Mapper for converting a MesoNote entity to its response DTO.
 */
@Component
public class MesoNoteMapper {

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
}
