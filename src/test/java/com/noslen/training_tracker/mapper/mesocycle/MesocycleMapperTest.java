package com.noslen.training_tracker.mapper.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.response.MesoNoteResponse;
import com.noslen.training_tracker.dto.mesocycle.response.MesocycleResponse;
import com.noslen.training_tracker.enums.Status;
import com.noslen.training_tracker.enums.Unit;
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

    private MesocycleResponse samplePayload;
    private Mesocycle sampleEntity;
    private Instant now;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        now = Instant.now();

        // Sample nested note payload
        MesoNoteResponse notePayload = new MesoNoteResponse(
                1L, 10L, 20L, now, now, "Test note content"
        );

        samplePayload = new MesocycleResponse(
                1L,
                "test-key",
                100L,
                "Test Mesocycle",
                28,
                "lb",
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
                .unit(Unit.LB)
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
                .notes(Collections.singletonList(createMesoNote(1L)))
                .status(Status.READY)
                .generatedFrom("template")
                .progressions(Collections.emptyMap())
                .build();
    }

    @Test
    void toPayload_WithValidEntity_ShouldReturnPayload() {
        // Given
        MesoNoteResponse mockNotePayload = new MesoNoteResponse(
                1L, 10L, 20L, now, now, "Test note"
        );
        when(mesoNoteMapper.toPayload(any(MesoNote.class))).thenReturn(mockNotePayload);

        // When
        MesocycleResponse result = mapper.toPayload(sampleEntity);

        // Then
        assertNotNull(result);
        assertEquals(sampleEntity.getId(), result.id());
        assertEquals(sampleEntity.getMesocycleKey(), result.key());
        assertEquals(sampleEntity.getUserId(), result.userId());
        assertEquals(sampleEntity.getName(), result.name());
        assertEquals(sampleEntity.getDays(), result.days());
        assertEquals(mapper.unitToString(sampleEntity.getUnit()), result.unit());
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
        MesocycleResponse result = mapper.toPayload(null);

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
                .unit(Unit.LB)
                .sourceTemplate(null)
                .sourceMeso(null)
                .weeks(null)
                .notes(null)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        MesocycleResponse result = mapper.toPayload(entityWithNullRelationships);

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
    void toPayload_WithWeeksCount_ShouldReturnCorrectCount() {
        // Given
        Mesocycle entityWithWeeks = Mesocycle.builder()
                .id(1L)
                .mesocycleKey("test-key")
                .weeks(Arrays.asList(null, null, null)) // 3 weeks
                .notes(Collections.emptyList())
                .build();

        // When
        MesocycleResponse result = mapper.toPayload(entityWithWeeks);

        // Then
        assertNotNull(result);
        assertEquals(3, result.weeks());
        verifyNoInteractions(mesoNoteMapper);
    }

    private MesoNote createMesoNote(Long id) {
        MesoNote note = new MesoNote();
        note.setId(id);
        return note;
    }
}
