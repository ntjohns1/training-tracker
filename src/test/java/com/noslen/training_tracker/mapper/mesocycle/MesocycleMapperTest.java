package com.noslen.training_tracker.mapper.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.MesoNotePayload;
import com.noslen.training_tracker.dto.mesocycle.MesocyclePayload;
import com.noslen.training_tracker.model.mesocycle.MesoNote;
import com.noslen.training_tracker.model.mesocycle.MesoTemplate;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MesocycleMapperTest {

    @Mock
    private MesoNoteMapper mesoNoteMapper;

    @InjectMocks
    private MesocycleMapper mapper;

    private MesocyclePayload samplePayload;
    private Mesocycle sampleEntity;
    private Instant now;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        now = Instant.now();

        // Sample nested note payload
        MesoNotePayload notePayload = new MesoNotePayload(
                1L, 10L, 20L, now, now, "Test note content"
        );

        samplePayload = new MesocyclePayload(
                1L,
                "test-key",
                100L,
                "Test Mesocycle",
                28,
                "days",
                2L,
                3L,
                5L,
                now,
                now,
                null,
                null,
                now.minusSeconds(3600),
                now.minusSeconds(1800),
                now.minusSeconds(900),
                now.minusSeconds(600),
                now.minusSeconds(300),
                now.minusSeconds(150),
                now.minusSeconds(100),
                now.minusSeconds(50),
                now.minusSeconds(25),
                now.minusSeconds(10),
                now.minusSeconds(5),
                4,
                Arrays.asList(notePayload)
        );

        sampleEntity = Mesocycle.builder()
                .id(1L)
                .mesocycleKey("test-key")
                .userId(100L)
                .name("Test Mesocycle")
                .days(28)
                .unit("days")
                .sourceTemplate(MesoTemplate.builder().id(2L).build())
                .sourceMeso(Mesocycle.builder().id(3L).build())
                .microRirs(5L)
                .createdAt(now)
                .updatedAt(now)
                .finishedAt(null)
                .deletedAt(null)
                .firstMicroCompletedAt(now.minusSeconds(3600))
                .firstWorkoutCompletedAt(now.minusSeconds(1800))
                .firstExerciseCompletedAt(now.minusSeconds(900))
                .firstSetCompletedAt(now.minusSeconds(600))
                .lastMicroFinishedAt(now.minusSeconds(300))
                .lastSetCompletedAt(now.minusSeconds(150))
                .lastSetSkippedAt(now.minusSeconds(100))
                .lastWorkoutCompletedAt(now.minusSeconds(50))
                .lastWorkoutFinishedAt(now.minusSeconds(25))
                .lastWorkoutSkippedAt(now.minusSeconds(10))
                .lastWorkoutPartialedAt(now.minusSeconds(5))
                .weeks(Collections.emptyList())
                .notes(Arrays.asList(MesoNote.builder().id(1L).build()))
                .status("active")
                .generatedFrom("template")
                .progressions(Collections.emptyMap())
                .build();
    }

    @Test
    void toEntity_WithValidPayload_ShouldReturnEntity() {
        // Given
        MesoNote mockNote = MesoNote.builder().id(1L).build();
        when(mesoNoteMapper.toEntity(any(MesoNotePayload.class))).thenReturn(mockNote);

        // When
        Mesocycle result = mapper.toEntity(samplePayload);

        // Then
        assertNotNull(result);
        assertEquals(samplePayload.id(), result.getId());
        assertEquals(samplePayload.key(), result.getMesocycleKey());
        assertEquals(samplePayload.userId(), result.getUserId());
        assertEquals(samplePayload.name(), result.getName());
        assertEquals(samplePayload.days(), result.getDays());
        assertEquals(samplePayload.unit(), result.getUnit());
        assertEquals(samplePayload.microRirs(), result.getMicroRirs());
        assertEquals(samplePayload.createdAt(), result.getCreatedAt());
        assertEquals(samplePayload.updatedAt(), result.getUpdatedAt());
        assertEquals(samplePayload.finishedAt(), result.getFinishedAt());
        assertEquals(samplePayload.deletedAt(), result.getDeletedAt());

        // Verify relationships are set
        assertNotNull(result.getSourceTemplate());
        assertEquals(samplePayload.sourceTemplateId(), result.getSourceTemplate().getId());
        assertNotNull(result.getSourceMeso());
        assertEquals(samplePayload.sourceMesoId(), result.getSourceMeso().getId());

        // Verify notes mapping
        assertNotNull(result.getNotes());
        assertEquals(1, result.getNotes().size());
        verify(mesoNoteMapper).toEntity(any(MesoNotePayload.class));
    }

    @Test
    void toEntity_WithNullPayload_ShouldReturnNull() {
        // When
        Mesocycle result = mapper.toEntity(null);

        // Then
        assertNull(result);
        verifyNoInteractions(mesoNoteMapper);
    }

    @Test
    void toEntity_WithNullRelationshipIds_ShouldHandleGracefully() {
        // Given
        MesocyclePayload payloadWithNullIds = new MesocyclePayload(
                1L, "test-key", 100L, "Test Mesocycle", 28, "days",
                null, null, 5L, now, now, null, null,
                null, null, null, null, null, null, null, null, null, null, null,
                4, Collections.emptyList()
        );

        // When
        Mesocycle result = mapper.toEntity(payloadWithNullIds);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test-key", result.getMesocycleKey());
        assertEquals("Test Mesocycle", result.getName());
        assertNull(result.getSourceTemplate());
        assertNull(result.getSourceMeso());
        verifyNoInteractions(mesoNoteMapper);
    }

    @Test
    void toEntity_WithNullFields_ShouldHandleGracefully() {
        // Given
        MesocyclePayload payloadWithNulls = new MesocyclePayload(
                null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null
        );

        // When
        Mesocycle result = mapper.toEntity(payloadWithNulls);

        // Then
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getMesocycleKey());
        assertNull(result.getName());
        assertNull(result.getUserId());
        assertNull(result.getDays());
        assertNull(result.getUnit());
        assertNull(result.getSourceTemplate());
        assertNull(result.getSourceMeso());
        verifyNoInteractions(mesoNoteMapper);
    }

    @Test
    void toPayload_WithValidEntity_ShouldReturnPayload() {
        // Given
        MesoNotePayload mockNotePayload = new MesoNotePayload(
                1L, 10L, 20L, now, now, "Test note"
        );
        when(mesoNoteMapper.toPayload(any(MesoNote.class))).thenReturn(mockNotePayload);

        // When
        MesocyclePayload result = mapper.toPayload(sampleEntity);

        // Then
        assertNotNull(result);
        assertEquals(sampleEntity.getId(), result.id());
        assertEquals(sampleEntity.getMesocycleKey(), result.key());
        assertEquals(sampleEntity.getUserId(), result.userId());
        assertEquals(sampleEntity.getName(), result.name());
        assertEquals(sampleEntity.getDays(), result.days());
        assertEquals(sampleEntity.getUnit(), result.unit());
        assertEquals(sampleEntity.getMicroRirs(), result.microRirs());
        assertEquals(sampleEntity.getCreatedAt(), result.createdAt());
        assertEquals(sampleEntity.getUpdatedAt(), result.updatedAt());
        assertEquals(sampleEntity.getFinishedAt(), result.finishedAt());
        assertEquals(sampleEntity.getDeletedAt(), result.deletedAt());

        // Verify relationship IDs are extracted
        assertEquals(2L, result.sourceTemplateId());
        assertEquals(3L, result.sourceMesoId());

        // Verify weeks count
        assertEquals(0, result.weeks()); // Empty list should return 0

        // Verify notes mapping
        assertNotNull(result.notes());
        assertEquals(1, result.notes().size());
        verify(mesoNoteMapper).toPayload(any(MesoNote.class));
    }

    @Test
    void toPayload_WithNullEntity_ShouldReturnNull() {
        // When
        MesocyclePayload result = mapper.toPayload(null);

        // Then
        assertNull(result);
        verifyNoInteractions(mesoNoteMapper);
    }

    @Test
    void toPayload_WithNullRelationships_ShouldHandleGracefully() {
        // Given
        Mesocycle entityWithNullRelationships = Mesocycle.builder()
                .id(1L)
                .mesocycleKey("test-key")
                .name("Test Mesocycle")
                .userId(100L)
                .days(28)
                .unit("days")
                .sourceTemplate(null)
                .sourceMeso(null)
                .weeks(null)
                .notes(null)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        MesocyclePayload result = mapper.toPayload(entityWithNullRelationships);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("test-key", result.key());
        assertEquals("Test Mesocycle", result.name());
        assertNull(result.sourceTemplateId());
        assertNull(result.sourceMesoId());
        assertNull(result.weeks());
        assertNotNull(result.notes());
        assertTrue(result.notes().isEmpty());
        verifyNoInteractions(mesoNoteMapper);
    }

    @Test
    void updateEntity_ShouldBeNoOp() {
        // Given
        Mesocycle existingEntity = Mesocycle.builder()
                .id(1L)
                .mesocycleKey("old-key")
                .name("Old Name")
                .build();

        MesocyclePayload updatePayload = new MesocyclePayload(
                1L, "new-key", 100L, "New Name", 28, "days",
                null, null, null, now, now, null, null,
                null, null, null, null, null, null, null, null, null, null, null,
                4, Collections.emptyList()
        );

        // When
        mapper.updateEntity(existingEntity, updatePayload);

        // Then
        // Since Mesocycle is immutable, updateEntity should be a no-op
        // The entity should remain unchanged
        assertEquals("old-key", existingEntity.getMesocycleKey());
        assertEquals("Old Name", existingEntity.getName());
        verifyNoInteractions(mesoNoteMapper);
    }

    @Test
    void updateEntity_WithNullPayload_ShouldNotCrash() {
        // Given
        Mesocycle existingEntity = Mesocycle.builder().id(1L).build();

        // When & Then
        assertDoesNotThrow(() -> mapper.updateEntity(existingEntity, null));
        verifyNoInteractions(mesoNoteMapper);
    }

    @Test
    void updateEntity_WithNullEntity_ShouldNotCrash() {
        // When & Then
        assertDoesNotThrow(() -> mapper.updateEntity(null, samplePayload));
        verifyNoInteractions(mesoNoteMapper);
    }

    @Test
    void mergeEntity_WithValidData_ShouldCreateNewEntityWithUpdatedFields() {
        // Given
        Mesocycle existingEntity = Mesocycle.builder()
                .id(1L)
                .mesocycleKey("old-key")
                .name("Old Name")
                .userId(50L)
                .days(21)
                .unit("weeks")
                .sourceTemplate(MesoTemplate.builder().id(10L).build())
                .sourceMeso(Mesocycle.builder().id(20L).build())
                .microRirs(3L)
                .createdAt(now.minusSeconds(3600))
                .updatedAt(now.minusSeconds(1800))
                .finishedAt(null)
                .deletedAt(null)
                .weeks(Collections.emptyList())
                .notes(Collections.emptyList())
                .status("draft")
                .generatedFrom("manual")
                .progressions(Collections.emptyMap())
                .build();

        MesoNotePayload notePayload = new MesoNotePayload(
                2L, 1L, 30L, now, now, "Updated note"
        );
        MesocyclePayload updatePayload = new MesocyclePayload(
                1L, "new-key", 100L, "New Name", 28, "days",
                2L, 3L, 5L, now.minusSeconds(3600), now, null, null,
                null, null, null, null, null, null, null, null, null, null, null,
                4, Arrays.asList(notePayload)
        );

        MesoNote mockNote = MesoNote.builder().id(2L).build();
        when(mesoNoteMapper.toEntity(notePayload)).thenReturn(mockNote);

        // When
        Mesocycle result = mapper.mergeEntity(existingEntity, updatePayload);

        // Then
        assertNotNull(result);
        assertNotSame(existingEntity, result); // Should be a new instance
        assertEquals(1L, result.getId());
        assertEquals("new-key", result.getMesocycleKey());
        assertEquals("New Name", result.getName());
        assertEquals(100L, result.getUserId());
        assertEquals(28, result.getDays());
        assertEquals("days", result.getUnit());
        assertEquals(5L, result.getMicroRirs());
        assertEquals(now.minusSeconds(3600), result.getCreatedAt()); // Preserved from existing
        assertNotNull(result.getUpdatedAt());
        assertTrue(result.getUpdatedAt().isAfter(now.minusSeconds(10))); // Should be current time

        // Verify relationships are updated
        assertNotNull(result.getSourceTemplate());
        assertEquals(2L, result.getSourceTemplate().getId());
        assertNotNull(result.getSourceMeso());
        assertEquals(3L, result.getSourceMeso().getId());

        // Verify preserved fields
        assertEquals("draft", result.getStatus());
        assertEquals("manual", result.getGeneratedFrom());
        assertSame(existingEntity.getWeeks(), result.getWeeks());
        assertSame(existingEntity.getProgressions(), result.getProgressions());

        // Verify notes are updated
        assertNotNull(result.getNotes());
        assertEquals(1, result.getNotes().size());
        verify(mesoNoteMapper).toEntity(notePayload);
    }

    @Test
    void mergeEntity_WithNullPayload_ShouldReturnNull() {
        // Given
        Mesocycle existingEntity = Mesocycle.builder().id(1L).build();

        // When
        Mesocycle result = mapper.mergeEntity(existingEntity, null);

        // Then
        assertNull(result);
        verifyNoInteractions(mesoNoteMapper);
    }

    @Test
    void mergeEntity_WithNullEntity_ShouldReturnNull() {
        // When
        Mesocycle result = mapper.mergeEntity(null, samplePayload);

        // Then
        assertNull(result);
        verifyNoInteractions(mesoNoteMapper);
    }

    @Test
    void mergeEntity_WithNullFieldsInPayload_ShouldPreserveExistingValues() {
        // Given
        MesoTemplate existingSourceTemplate = MesoTemplate.builder().id(10L).build();
        Mesocycle existingSourceMeso = Mesocycle.builder().id(20L).build();
        List<MesoNote> existingNotes = Arrays.asList(MesoNote.builder().id(5L).build());

        Mesocycle existingEntity = Mesocycle.builder()
                .id(1L)
                .mesocycleKey("old-key")
                .name("Old Name")
                .userId(50L)
                .days(21)
                .unit("weeks")
                .sourceTemplate(existingSourceTemplate)
                .sourceMeso(existingSourceMeso)
                .microRirs(3L)
                .createdAt(now.minusSeconds(3600))
                .notes(existingNotes)
                .status("draft")
                .generatedFrom("manual")
                .build();

        MesocyclePayload updatePayload = new MesocyclePayload(
                1L, null, null, "New Name", null, null,
                null, null, null, now.minusSeconds(3600), now, null, null,
                null, null, null, null, null, null, null, null, null, null, null,
                null, null
        );

        // When
        Mesocycle result = mapper.mergeEntity(existingEntity, updatePayload);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("old-key", result.getMesocycleKey()); // Preserved from existing
        assertEquals("New Name", result.getName()); // Updated from payload
        assertEquals(50L, result.getUserId()); // Preserved from existing
        assertEquals(21, result.getDays()); // Preserved from existing
        assertEquals("weeks", result.getUnit()); // Preserved from existing
        assertEquals(3L, result.getMicroRirs()); // Preserved from existing
        assertEquals(now.minusSeconds(3600), result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        // Verify relationships are preserved from existing
        assertNotNull(result.getSourceTemplate());
        assertEquals(10L, result.getSourceTemplate().getId());
        assertSame(existingSourceTemplate, result.getSourceTemplate());
        assertNotNull(result.getSourceMeso());
        assertEquals(20L, result.getSourceMeso().getId());
        assertSame(existingSourceMeso, result.getSourceMeso());
        assertSame(existingNotes, result.getNotes());

        verifyNoInteractions(mesoNoteMapper);
    }

    @Test
    void toPayload_WithWeeksCount_ShouldReturnCorrectCount() {
        // Given
        Mesocycle entityWithWeeks = Mesocycle.builder()
                .id(1L)
                .mesocycleKey("test-key")
                .weeks(Arrays.asList(null, null, null)) // 3 weeks
                .notes(Collections.emptyList())
                .build();

        // When
        MesocyclePayload result = mapper.toPayload(entityWithWeeks);

        // Then
        assertNotNull(result);
        assertEquals(3, result.weeks());
        verifyNoInteractions(mesoNoteMapper);
    }
}
