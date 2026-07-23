package com.noslen.training_tracker.mapper.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.response.MesoNoteResponse;
import com.noslen.training_tracker.model.mesocycle.MesoNote;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class MesoNoteMapperTest {

    private MesoNoteMapper mapper;
    private MesoNote sampleEntity;
    private Instant now;

    @BeforeEach
    void setUp() {
        mapper = new MesoNoteMapper();
        now = Instant.now();

        sampleEntity = new MesoNote();
        sampleEntity.setId(1L);
        sampleEntity.setMesocycle(Mesocycle.builder().id(10L).build());
        sampleEntity.setNoteId(20L);
        sampleEntity.setCreatedAt(now);
        sampleEntity.setUpdatedAt(now);
        sampleEntity.setText("Test meso note content");
    }

    @Test
    void toPayload_WithValidEntity_ShouldReturnPayload() {
        MesoNoteResponse result = mapper.toPayload(sampleEntity);

        assertNotNull(result);
        assertEquals(sampleEntity.getId(), result.id());
        assertEquals(sampleEntity.getMesocycle().getId(), result.mesoId());
        assertEquals(sampleEntity.getNoteId(), result.noteId());
        assertEquals(sampleEntity.getCreatedAt(), result.createdAt());
        assertEquals(sampleEntity.getUpdatedAt(), result.updatedAt());
        assertEquals(sampleEntity.getText(), result.text());
    }

    @Test
    void toPayload_WithNullEntity_ShouldReturnNull() {
        assertNull(mapper.toPayload(null));
    }

    @Test
    void toPayload_WithNullMesocycle_ShouldHandleGracefully() {
        MesoNote entityWithNullMesocycle = new MesoNote();
        entityWithNullMesocycle.setId(1L);
        entityWithNullMesocycle.setMesocycle(null);
        entityWithNullMesocycle.setNoteId(20L);
        entityWithNullMesocycle.setText("Test note");
        entityWithNullMesocycle.setCreatedAt(now);
        entityWithNullMesocycle.setUpdatedAt(now);

        MesoNoteResponse result = mapper.toPayload(entityWithNullMesocycle);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertNull(result.mesoId());
        assertEquals(20L, result.noteId());
        assertEquals("Test note", result.text());
    }
}
