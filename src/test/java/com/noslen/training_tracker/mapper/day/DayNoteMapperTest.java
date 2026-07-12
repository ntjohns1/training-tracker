package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.response.DayNoteResponse;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.day.DayNote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DayNoteMapperTest {

    private DayNoteMapper mapper;
    private DayNote sampleEntity;
    private Instant now;
    private Day day = Day.builder().id(1L).build();

    @BeforeEach
    void setUp() {
        mapper = new DayNoteMapper();
        now = Instant.now();

        sampleEntity = new DayNote();
        sampleEntity.setId(1L);
        sampleEntity.setDay(day);
        sampleEntity.setNoteId(20L);
        sampleEntity.setText("Test note content");
        sampleEntity.setPinned(true);
        sampleEntity.setCreatedAt(now);
        sampleEntity.setUpdatedAt(now);
    }

    @Test
    void toPayload_WithValidEntity_ShouldReturnPayload() {
        // When
        DayNoteResponse result = mapper.toPayload(sampleEntity);

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
        DayNoteResponse result = mapper.toPayload(null);

        // Then
        assertNull(result);
    }

    @Test
    void toPayload_WithEntityHavingRelationships_ShouldExtractIds() {
        // Given
        Day day = Day.builder().id(100L).build();
        DayNote entityWithRelationships = new DayNote();
        entityWithRelationships.setDay(day);
        entityWithRelationships.setNoteId(200L);
        entityWithRelationships.setText("Test note");
        entityWithRelationships.setPinned(true);
        entityWithRelationships.setCreatedAt(now);
        entityWithRelationships.setUpdatedAt(now);

        // When
        DayNoteResponse result = mapper.toPayload(entityWithRelationships);

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
        DayNote entityWithNullRelationships = new DayNote();
        entityWithNullRelationships.setText("Test note");
        entityWithNullRelationships.setPinned(false);
        entityWithNullRelationships.setCreatedAt(now);
        entityWithNullRelationships.setUpdatedAt(now);

        // When
        DayNoteResponse result = mapper.toPayload(entityWithNullRelationships);

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
    void toPayloadList_WithValidList_ShouldMapAll() {
        // When
        List<DayNoteResponse> result = mapper.toPayloadList(List.of(sampleEntity));

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleEntity.getId(), result.get(0).id());
    }

    @Test
    void toPayloadList_WithNullList_ShouldReturnNull() {
        // When
        List<DayNoteResponse> result = mapper.toPayloadList(null);

        // Then
        assertNull(result);
    }
}
