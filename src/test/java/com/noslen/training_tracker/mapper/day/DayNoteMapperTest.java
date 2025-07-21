package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.DayNotePayload;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.day.DayNote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class DayNoteMapperTest {

    private DayNoteMapper mapper;
    private DayNotePayload samplePayload;
    private DayNote sampleEntity;
    private Instant now;
    private Day day = Day.builder().id(1L).build();
    


    @BeforeEach
    void setUp() {
        mapper = new DayNoteMapper();
        now = Instant.now();

        samplePayload = new DayNotePayload(
                1L,
                10L,
                20L,
                true,
                now,
                now,
                "Test note content"
        );

        sampleEntity = DayNote.builder()
                .id(1L)
                .day(day)
                .noteId(20L)
                .text("Test note content")
                .pinned(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    void toEntity_WithValidPayload_ShouldReturnEntity() {
        // When
        DayNote result = mapper.toEntity(samplePayload);

        // Then
        assertNotNull(result);
        assertEquals(samplePayload.id(), result.getId());
        assertEquals(samplePayload.dayId(), result.getDay().getId());
        assertEquals(samplePayload.noteId(), result.getNoteId());
        assertEquals(samplePayload.text(), result.getText());
        assertEquals(samplePayload.pinned(), result.getPinned());
        assertEquals(samplePayload.createdAt(), result.getCreatedAt());
        assertEquals(samplePayload.updatedAt(), result.getUpdatedAt());

        // Verify relationships are set
        assertNotNull(result.getDay());
        assertEquals(samplePayload.dayId(), result.getDay().getId());
        assertEquals(samplePayload.noteId(), result.getNoteId());
    }

    @Test
    void toEntity_WithNullPayload_ShouldReturnNull() {
        // When
        DayNote result = mapper.toEntity(null);

        // Then
        assertNull(result);
    }

    @Test
    void toEntity_WithNullFields_ShouldHandleGracefully() {
        // Given
        DayNotePayload payloadWithNulls = new DayNotePayload(
                null, null, null, null, null, null, null
        );

        // When
        DayNote result = mapper.toEntity(payloadWithNulls);

        // Then
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getDay());
        assertNull(result.getNoteId());
        assertNull(result.getText());
        assertNull(result.getPinned());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
    }

    @Test
    void toEntity_WithFalsePinned_ShouldHandleCorrectly() {
        // Given
        DayNotePayload payloadWithFalsePinned = new DayNotePayload(
                1L, 10L, 20L, false, now, now, "Test note"
        );

        // When
        DayNote result = mapper.toEntity(payloadWithFalsePinned);

        // Then
        assertNotNull(result);
        assertEquals(false, result.getPinned());
    }

    @Test
    void toPayload_WithValidEntity_ShouldReturnPayload() {
        // When
        DayNotePayload result = mapper.toPayload(sampleEntity);

        // Then
        assertNotNull(result);
        assertEquals(sampleEntity.getId(), result.id());
        assertEquals(sampleEntity.getDay().getId(), result.dayId());
        assertEquals(sampleEntity.getNoteId(), result.noteId());
        assertEquals(sampleEntity.getText(), result.text());
        assertEquals(sampleEntity.getPinned(), result.pinned());
        assertEquals(sampleEntity.getCreatedAt(), result.createdAt());
        assertEquals(sampleEntity.getUpdatedAt(), result.updatedAt());
    }

    @Test
    void toPayload_WithNullEntity_ShouldReturnNull() {
        // When
        DayNotePayload result = mapper.toPayload(null);

        // Then
        assertNull(result);
    }

    @Test
    void toPayload_WithEntityHavingRelationships_ShouldExtractIds() {
        // Given
        Day day = Day.builder().id(100L).build();
        DayNote entityWithRelationships = DayNote.builder()
                .day(day)
                .noteId(200L)
                .text("Test note")
                .pinned(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        DayNotePayload result = mapper.toPayload(entityWithRelationships);

        // Then
        assertNotNull(result);
        assertEquals(100L, result.dayId());
        assertEquals(200L, result.noteId());
        assertEquals("Test note", result.text());
        assertEquals(true, result.pinned());
        assertEquals(now, result.createdAt());
        assertEquals(now, result.updatedAt());
    }

    @Test
    void toPayload_WithNullRelationships_ShouldHandleGracefully() {
        // Given
        DayNote entityWithNullRelationships = DayNote.builder()
                .text("Test note")
                .pinned(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        DayNotePayload result = mapper.toPayload(entityWithNullRelationships);

        // Then
        assertNotNull(result);
        assertNull(result.dayId());
        assertNull(result.noteId());
        assertEquals("Test note", result.text());
        assertEquals(false, result.pinned());
        assertEquals(now, result.createdAt());
        assertEquals(now, result.updatedAt());
    }

    @Test
    void updateEntity_WithValidData_ShouldUpdateMutableFields() {
        // Given
        DayNote existingEntity = DayNote.builder()
                .day(Day.builder().id(10L).build())
                .noteId(20L)
                .text("Old text")
                .pinned(false)
                .createdAt(now.minusSeconds(3600))
                .updatedAt(now.minusSeconds(1800))
                .build();

        DayNotePayload updatePayload = new DayNotePayload(
                1L, 15L, 25L, true, now.minusSeconds(3600), now, "Updated note content"
        );

        // When
        mapper.updateEntity(existingEntity, updatePayload);

        // Then
        assertEquals("Updated note content", existingEntity.getText());
        assertEquals(true, existingEntity.getPinned());
        
        // Verify updatedAt is set to current time (not from payload)
        assertNotNull(existingEntity.getUpdatedAt());
        assertTrue(existingEntity.getUpdatedAt().isAfter(now.minusSeconds(10)));

        // Verify relationships are updated
        assertNotNull(existingEntity.getDay());
        assertEquals(15L, existingEntity.getDay().getId());
    }

    @Test
    void updateEntity_WithNullPayload_ShouldNotCrash() {
        // Given
        DayNote existingEntity = DayNote.builder().id(1L).build();

        // When & Then
        assertDoesNotThrow(() -> mapper.updateEntity(existingEntity, null));
    }

    @Test
    void updateEntity_WithNullEntity_ShouldNotCrash() {
        // When & Then
        assertDoesNotThrow(() -> mapper.updateEntity(null, samplePayload));
    }

    @Test
    void updateEntity_WithNullRelationshipIds_ShouldHandleGracefully() {
        // Given
        DayNote existingEntity = DayNote.builder()
                .day(Day.builder().id(10L).build())
                .noteId(20L)
                .text("Old text")
                .pinned(false)
                .build();

        DayNotePayload payloadWithNullIds = new DayNotePayload(
                1L, null, null, true, now, now, "New text"
        );

        // When
        mapper.updateEntity(existingEntity, payloadWithNullIds);

        // Then
        assertEquals("New text", existingEntity.getText());
        assertEquals(true, existingEntity.getPinned());
        assertNotNull(existingEntity.getUpdatedAt());
        
        // Relationships should not be updated when IDs are null
        assertNotNull(existingEntity.getDay());
        assertEquals(10L, existingEntity.getDay().getId());
    }

    @Test
    void mergeEntity_WithValidData_ShouldCreateNewEntityWithUpdatedFields() {
        // Given
        DayNote existingEntity = DayNote.builder()
                .day(Day.builder().id(10L).build())
                .noteId(20L)
                .text("Old text")
                .pinned(false)
                .createdAt(now.minusSeconds(3600))
                .updatedAt(now.minusSeconds(1800))
                .build();

        DayNotePayload updatePayload = new DayNotePayload(
                1L, 15L, 25L, true, now.minusSeconds(3600), now, "Updated note content"
        );

        // When
        DayNote result = mapper.mergeEntity(existingEntity, updatePayload);

        // Then
        assertNotNull(result);
        assertNotSame(existingEntity, result); // Should be a new instance
        assertEquals(1L, result.getId());
        assertEquals(15L, result.getDay().getId());
        assertEquals(25L, result.getNoteId());
        assertEquals("Updated note content", result.getText());
        assertEquals(true, result.getPinned());
        assertEquals(now.minusSeconds(3600), result.getCreatedAt()); // Preserved from existing
        assertNotNull(result.getUpdatedAt());

        // Verify relationships are set
        assertNotNull(result.getDay());
        assertEquals(15L, result.getDay().getId());
    }

    @Test
    void mergeEntity_WithNullPayload_ShouldReturnNull() {
        // Given
        DayNote existingEntity = DayNote.builder().id(1L).build();

        // When
        DayNote result = mapper.mergeEntity(existingEntity, null);

        // Then
        assertNull(result);
    }

    @Test
    void mergeEntity_WithNullEntity_ShouldReturnNull() {
        // When
        DayNote result = mapper.mergeEntity(null, samplePayload);

        // Then
        assertNull(result);
    }

    @Test
    void mergeEntity_WithNullRelationshipIds_ShouldHandleGracefully() {
        // Given
        DayNote existingEntity = DayNote.builder()
                .day(Day.builder().id(10L).build())
                .noteId(20L)
                .text("Old text")
                .pinned(false)
                .createdAt(now.minusSeconds(3600))
                .build();

        DayNotePayload payloadWithNullIds = new DayNotePayload(
                1L, null, null, true, now.minusSeconds(3600), now, "New text"
        );

        // When
        DayNote result = mapper.mergeEntity(existingEntity, payloadWithNullIds);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNull(result.getDay());
        assertNull(result.getNoteId());
        assertEquals("New text", result.getText());
        assertEquals(true, result.getPinned());
        assertEquals(now.minusSeconds(3600), result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void updateEntity_WithEmptyText_ShouldHandleCorrectly() {
        // Given
        DayNote existingEntity = DayNote.builder()
                .text("Old text")
                .build();

        DayNotePayload payloadWithEmptyText = new DayNotePayload(
                1L, 10L, 20L, false, now, now, ""
        );

        // When
        mapper.updateEntity(existingEntity, payloadWithEmptyText);

        // Then
        assertEquals("", existingEntity.getText());
        assertEquals(false, existingEntity.getPinned());
    }
}
