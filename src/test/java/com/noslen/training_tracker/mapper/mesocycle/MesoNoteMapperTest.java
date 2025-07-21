package com.noslen.training_tracker.mapper.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.MesoNotePayload;
import com.noslen.training_tracker.model.mesocycle.MesoNote;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class MesoNoteMapperTest {

    private MesoNoteMapper mapper;
    private MesoNotePayload samplePayload;
    private MesoNote sampleEntity;
    private Instant now;

    @BeforeEach
    void setUp() {
        mapper = new MesoNoteMapper();
        now = Instant.now();

        samplePayload = new MesoNotePayload(
                1L,
                10L,
                20L,
                now,
                now,
                "Test meso note content"
        );

        sampleEntity = MesoNote.builder()
                .id(1L)
                .mesocycle(Mesocycle.builder().id(10L).build())
                .noteId(20L)
                .createdAt(now)
                .updatedAt(now)
                .text("Test meso note content")
                .build();
    }

    @Test
    void toEntity_WithValidPayload_ShouldReturnEntity() {
        // When
        MesoNote result = mapper.toEntity(samplePayload);

        // Then
        assertNotNull(result);
        assertEquals(samplePayload.id(), result.getId());
        assertEquals(samplePayload.noteId(), result.getNoteId());
        assertEquals(samplePayload.text(), result.getText());
        assertEquals(samplePayload.createdAt(), result.getCreatedAt());
        assertEquals(samplePayload.updatedAt(), result.getUpdatedAt());

        // Verify mesocycle relationship is set
        assertNotNull(result.getMesocycle());
        assertEquals(samplePayload.mesoId(), result.getMesocycle().getId());
    }

    @Test
    void toEntity_WithNullPayload_ShouldReturnNull() {
        // When
        MesoNote result = mapper.toEntity(null);

        // Then
        assertNull(result);
    }

    @Test
    void toEntity_WithNullMesoId_ShouldHandleGracefully() {
        // Given
        MesoNotePayload payloadWithNullMesoId = new MesoNotePayload(
                1L, null, 20L, now, now, "Test note"
        );

        // When
        MesoNote result = mapper.toEntity(payloadWithNullMesoId);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(20L, result.getNoteId());
        assertEquals("Test note", result.getText());
        assertNull(result.getMesocycle());
    }

    @Test
    void toEntity_WithNullFields_ShouldHandleGracefully() {
        // Given
        MesoNotePayload payloadWithNulls = new MesoNotePayload(
                null, null, null, null, null, null
        );

        // When
        MesoNote result = mapper.toEntity(payloadWithNulls);

        // Then
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getNoteId());
        assertNull(result.getText());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
        assertNull(result.getMesocycle());
    }

    @Test
    void toPayload_WithValidEntity_ShouldReturnPayload() {
        // When
        MesoNotePayload result = mapper.toPayload(sampleEntity);

        // Then
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
        // When
        MesoNotePayload result = mapper.toPayload(null);

        // Then
        assertNull(result);
    }

    @Test
    void toPayload_WithNullMesocycle_ShouldHandleGracefully() {
        // Given
        MesoNote entityWithNullMesocycle = MesoNote.builder()
                .id(1L)
                .mesocycle(null)
                .noteId(20L)
                .text("Test note")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        MesoNotePayload result = mapper.toPayload(entityWithNullMesocycle);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertNull(result.mesoId());
        assertEquals(20L, result.noteId());
        assertEquals("Test note", result.text());
        assertEquals(now, result.createdAt());
        assertEquals(now, result.updatedAt());
    }

    @Test
    void updateEntity_ShouldBeNoOp() {
        // Given
        MesoNote existingEntity = MesoNote.builder()
                .id(1L)
                .text("Old text")
                .build();

        MesoNotePayload updatePayload = new MesoNotePayload(
                1L, 10L, 20L, now, now, "New text"
        );

        // When
        mapper.updateEntity(existingEntity, updatePayload);

        // Then
        // Since MesoNote is immutable, updateEntity should be a no-op
        // The entity should remain unchanged
        assertEquals("Old text", existingEntity.getText());
    }

    @Test
    void updateEntity_WithNullPayload_ShouldNotCrash() {
        // Given
        MesoNote existingEntity = MesoNote.builder().id(1L).build();

        // When & Then
        assertDoesNotThrow(() -> mapper.updateEntity(existingEntity, null));
    }

    @Test
    void updateEntity_WithNullEntity_ShouldNotCrash() {
        // When & Then
        assertDoesNotThrow(() -> mapper.updateEntity(null, samplePayload));
    }

    @Test
    void mergeEntity_WithValidData_ShouldCreateNewEntityWithUpdatedFields() {
        // Given
        MesoNote existingEntity = MesoNote.builder()
                .id(1L)
                .mesocycle(Mesocycle.builder().id(5L).build())
                .noteId(15L)
                .text("Old text")
                .createdAt(now.minusSeconds(3600))
                .updatedAt(now.minusSeconds(1800))
                .build();

        MesoNotePayload updatePayload = new MesoNotePayload(
                1L, 10L, 20L, now.minusSeconds(3600), now, "New text"
        );

        // When
        MesoNote result = mapper.mergeEntity(existingEntity, updatePayload);

        // Then
        assertNotNull(result);
        assertNotSame(existingEntity, result); // Should be a new instance
        assertEquals(1L, result.getId());
        assertEquals(20L, result.getNoteId());
        assertEquals("New text", result.getText());
        assertEquals(now.minusSeconds(3600), result.getCreatedAt()); // Preserved from existing
        assertNotNull(result.getUpdatedAt());
        assertTrue(result.getUpdatedAt().isAfter(now.minusSeconds(10))); // Should be current time

        // Verify mesocycle relationship is updated
        assertNotNull(result.getMesocycle());
        assertEquals(10L, result.getMesocycle().getId());
    }

    @Test
    void mergeEntity_WithNullPayload_ShouldReturnNull() {
        // Given
        MesoNote existingEntity = MesoNote.builder().id(1L).build();

        // When
        MesoNote result = mapper.mergeEntity(existingEntity, null);

        // Then
        assertNull(result);
    }

    @Test
    void mergeEntity_WithNullEntity_ShouldReturnNull() {
        // When
        MesoNote result = mapper.mergeEntity(null, samplePayload);

        // Then
        assertNull(result);
    }

    @Test
    void mergeEntity_WithNullMesoId_ShouldPreserveExistingMesocycle() {
        // Given
        Mesocycle existingMesocycle = Mesocycle.builder().id(5L).build();
        MesoNote existingEntity = MesoNote.builder()
                .id(1L)
                .mesocycle(existingMesocycle)
                .noteId(15L)
                .text("Old text")
                .createdAt(now.minusSeconds(3600))
                .build();

        MesoNotePayload updatePayload = new MesoNotePayload(
                1L, null, 20L, now.minusSeconds(3600), now, "New text"
        );

        // When
        MesoNote result = mapper.mergeEntity(existingEntity, updatePayload);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(20L, result.getNoteId());
        assertEquals("New text", result.getText());
        assertEquals(now.minusSeconds(3600), result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        // Verify existing mesocycle is preserved
        assertNotNull(result.getMesocycle());
        assertEquals(5L, result.getMesocycle().getId());
        assertSame(existingMesocycle, result.getMesocycle());
    }

    @Test
    void mergeEntity_WithNullFieldsInPayload_ShouldPreserveExistingValues() {
        // Given
        MesoNote existingEntity = MesoNote.builder()
                .id(1L)
                .mesocycle(Mesocycle.builder().id(5L).build())
                .noteId(15L)
                .text("Old text")
                .createdAt(now.minusSeconds(3600))
                .build();

        MesoNotePayload updatePayload = new MesoNotePayload(
                1L, 10L, null, now.minusSeconds(3600), now, null
        );

        // When
        MesoNote result = mapper.mergeEntity(existingEntity, updatePayload);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(15L, result.getNoteId()); // Preserved from existing
        assertEquals("Old text", result.getText()); // Preserved from existing
        assertEquals(now.minusSeconds(3600), result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        // Verify mesocycle is updated from payload
        assertNotNull(result.getMesocycle());
        assertEquals(10L, result.getMesocycle().getId());
    }
}
