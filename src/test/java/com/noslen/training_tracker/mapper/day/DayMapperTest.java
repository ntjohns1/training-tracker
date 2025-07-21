package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.DayExercisePayload;
import com.noslen.training_tracker.dto.day.DayMuscleGroupPayload;
import com.noslen.training_tracker.dto.day.DayNotePayload;
import com.noslen.training_tracker.dto.day.DayPayload;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.model.day.DayMuscleGroup;
import com.noslen.training_tracker.model.day.DayNote;
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

class DayMapperTest {

    @Mock
    private DayNoteMapper dayNoteMapper;

    @Mock
    private DayExerciseMapper dayExerciseMapper;

    @Mock
    private DayMuscleGroupMapper dayMuscleGroupMapper;

    @InjectMocks
    private DayMapper dayMapper;

    private DayPayload samplePayload;
    private Day sampleEntity;
    private Instant now;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        now = Instant.now();

        // Sample nested payloads
        DayNotePayload notePayload = new DayNotePayload(1L, 1L, 1L, true, now, now, "Test note");
        DayExercisePayload exercisePayload = new DayExercisePayload(1L, 1L, 1L, 1, 0, now, now, null, 1L, Collections.emptyList(), "active");
        DayMuscleGroupPayload muscleGroupPayload = new DayMuscleGroupPayload( );

        samplePayload = new DayPayload(
                1L,
                1L,
                "Test Day",
                1,
                1,
                70.5,
                "kg",
                now,
                now,
                now,
                Arrays.asList(notePayload),
                Arrays.asList(exercisePayload),
                Arrays.asList(muscleGroupPayload)
        );

        // Sample nested entities
        DayNote noteEntity = DayNote.builder()
                .id(1L)
                .dayId(1L)
                .noteId(1L)
                .text("Test note")
                .pinned(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        DayExercise exerciseEntity = DayExercise.builder()
                .id(1L)
                .dayId(1L)
                .exerciseId(1L)
                .position(1)
                .jointPain(0)
                .createdAt(now)
                .updatedAt(now)
                .muscleGroupId(1L)
                .status("active")
                .build();

        DayMuscleGroup muscleGroupEntity = DayMuscleGroup.builder()
                .id(1L)
                .dayId(1L)
                .muscleGroupId(1L)
                .createdAt(now)
                .updatedAt(now)
                .build();

        sampleEntity = Day.builder()
                .id(1L)
                .mesoId(1L)
                .label("Test Day")
                .position(1)
                .week(1)
                .bodyweight(70.5)
                .unit("kg")
                .bodyweightAt(now)
                .createdAt(now)
                .updatedAt(now)
                .notes(Arrays.asList(noteEntity))
                .exercises(Arrays.asList(exerciseEntity))
                .muscleGroups(Arrays.asList(muscleGroupEntity))
                .build();
    }

    @Test
    void toEntity_WithValidPayload_ShouldReturnEntity() {
        // Given
        DayNote noteEntity = DayNote.builder().id(1L).build();
        DayExercise exerciseEntity = DayExercise.builder().id(1L).build();
        DayMuscleGroup muscleGroupEntity = DayMuscleGroup.builder().id(1L).build();

        when(dayNoteMapper.toEntity(any(DayNotePayload.class))).thenReturn(noteEntity);
        when(dayExerciseMapper.toEntity(any(DayExercisePayload.class))).thenReturn(exerciseEntity);
        when(dayMuscleGroupMapper.toEntity(any(DayMuscleGroupPayload.class))).thenReturn(muscleGroupEntity);

        // When
        Day result = dayMapper.toEntity(samplePayload);

        // Then
        assertNotNull(result);
        assertEquals(samplePayload.id(), result.getId());
        assertEquals(samplePayload.mesoId(), result.getMesoId());
        assertEquals(samplePayload.label(), result.getLabel());
        assertEquals(samplePayload.position(), result.getPosition());
        assertEquals(samplePayload.week(), result.getWeek());
        assertEquals(samplePayload.bodyweight(), result.getBodyweight());
        assertEquals(samplePayload.unit(), result.getUnit());
        assertEquals(samplePayload.bodyweightAt(), result.getBodyweightAt());
        assertEquals(samplePayload.createdAt(), result.getCreatedAt());
        assertEquals(samplePayload.updatedAt(), result.getUpdatedAt());

        // Verify nested collections
        assertEquals(1, result.getNotes().size());
        assertEquals(1, result.getExercises().size());
        assertEquals(1, result.getMuscleGroups().size());

        // Verify mapper calls
        verify(dayNoteMapper).toEntity(any(DayNotePayload.class));
        verify(dayExerciseMapper).toEntity(any(DayExercisePayload.class));
        verify(dayMuscleGroupMapper).toEntity(any(DayMuscleGroupPayload.class));
    }

    @Test
    void toEntity_WithNullPayload_ShouldReturnNull() {
        // When
        Day result = dayMapper.toEntity(null);

        // Then
        assertNull(result);
    }

    @Test
    void toEntity_WithEmptyCollections_ShouldHandleGracefully() {
        // Given
        DayPayload payloadWithEmptyCollections = new DayPayload(
                1L, 1L, "Test", 1, 1, 70.0, "kg", now, now, now,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        // When
        Day result = dayMapper.toEntity(payloadWithEmptyCollections);

        // Then
        assertNotNull(result);
        assertTrue(result.getNotes().isEmpty());
        assertTrue(result.getExercises().isEmpty());
        assertTrue(result.getMuscleGroups().isEmpty());
    }

    @Test
    void toEntity_WithNullCollections_ShouldHandleGracefully() {
        // Given
        DayPayload payloadWithNullCollections = new DayPayload(
                1L, 1L, "Test", 1, 1, 70.0, "kg", now, now, now,
                null, null, null
        );

        // When
        Day result = dayMapper.toEntity(payloadWithNullCollections);

        // Then
        assertNotNull(result);
        assertTrue(result.getNotes().isEmpty());
        assertTrue(result.getExercises().isEmpty());
        assertTrue(result.getMuscleGroups().isEmpty());
    }

    @Test
    void toPayload_WithValidEntity_ShouldReturnPayload() {
        // Given
        DayNotePayload notePayload = new DayNotePayload(1L, 1L, 1L, "Test", true, now, now);
        DayExercisePayload exercisePayload = new DayExercisePayload(1L, 1L, 1L, 1, 0, now, now, null, 1L, Collections.emptyList(), "active");
        DayMuscleGroupPayload muscleGroupPayload = new DayMuscleGroupPayload(1L, 1L, 1L, now, now);

        when(dayNoteMapper.toPayload(any(DayNote.class))).thenReturn(notePayload);
        when(dayExerciseMapper.toPayload(any(DayExercise.class))).thenReturn(exercisePayload);
        when(dayMuscleGroupMapper.toPayload(any(DayMuscleGroup.class))).thenReturn(muscleGroupPayload);

        // When
        DayPayload result = dayMapper.toPayload(sampleEntity);

        // Then
        assertNotNull(result);
        assertEquals(sampleEntity.getId(), result.id());
        assertEquals(sampleEntity.getMesoId(), result.mesoId());
        assertEquals(sampleEntity.getLabel(), result.label());
        assertEquals(sampleEntity.getPosition(), result.position());
        assertEquals(sampleEntity.getWeek(), result.week());
        assertEquals(sampleEntity.getBodyweight(), result.bodyweight());
        assertEquals(sampleEntity.getUnit(), result.unit());
        assertEquals(sampleEntity.getBodyweightAt(), result.bodyweightAt());
        assertEquals(sampleEntity.getCreatedAt(), result.createdAt());
        assertEquals(sampleEntity.getUpdatedAt(), result.updatedAt());

        // Verify nested collections
        assertEquals(1, result.notes().size());
        assertEquals(1, result.exercises().size());
        assertEquals(1, result.muscleGroups().size());

        // Verify mapper calls
        verify(dayNoteMapper).toPayload(any(DayNote.class));
        verify(dayExerciseMapper).toPayload(any(DayExercise.class));
        verify(dayMuscleGroupMapper).toPayload(any(DayMuscleGroup.class));
    }

    @Test
    void toPayload_WithNullEntity_ShouldReturnNull() {
        // When
        DayPayload result = dayMapper.toPayload(null);

        // Then
        assertNull(result);
    }

    @Test
    void toPayload_WithEmptyCollections_ShouldHandleGracefully() {
        // Given
        Day entityWithEmptyCollections = Day.builder()
                .id(1L)
                .mesoId(1L)
                .label("Test")
                .position(1)
                .week(1)
                .bodyweight(70.0)
                .unit("kg")
                .bodyweightAt(now)
                .createdAt(now)
                .updatedAt(now)
                .notes(Collections.emptyList())
                .exercises(Collections.emptyList())
                .muscleGroups(Collections.emptyList())
                .build();

        // When
        DayPayload result = dayMapper.toPayload(entityWithEmptyCollections);

        // Then
        assertNotNull(result);
        assertTrue(result.notes().isEmpty());
        assertTrue(result.exercises().isEmpty());
        assertTrue(result.muscleGroups().isEmpty());
    }

    @Test
    void toPayload_WithNullCollections_ShouldHandleGracefully() {
        // Given
        Day entityWithNullCollections = Day.builder()
                .id(1L)
                .mesoId(1L)
                .label("Test")
                .position(1)
                .week(1)
                .bodyweight(70.0)
                .unit("kg")
                .bodyweightAt(now)
                .createdAt(now)
                .updatedAt(now)
                .notes(null)
                .exercises(null)
                .muscleGroups(null)
                .build();

        // When
        DayPayload result = dayMapper.toPayload(entityWithNullCollections);

        // Then
        assertNotNull(result);
        assertNull(result.notes());
        assertNull(result.exercises());
        assertNull(result.muscleGroups());
    }

    @Test
    void updateEntity_WithValidData_ShouldUpdateMutableFields() {
        // Given
        Day existingEntity = Day.builder()
                .id(1L)
                .mesoId(1L)
                .label("Old Label")
                .position(1)
                .week(1)
                .bodyweight(65.0)
                .unit("lb")
                .bodyweightAt(now.minusSeconds(3600))
                .createdAt(now.minusSeconds(7200))
                .updatedAt(now.minusSeconds(3600))
                .build();

        DayPayload updatePayload = new DayPayload(
                1L, 2L, "New Label", 2, 2, 70.0, "kg", now, now, now,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList()
        );

        // When
        dayMapper.updateEntity(existingEntity, updatePayload);

        // Then
        assertEquals("New Label", existingEntity.getLabel());
        assertEquals(70.0, existingEntity.getBodyweight());
        assertEquals("kg", existingEntity.getUnit());
        assertEquals(now, existingEntity.getBodyweightAt());
        assertEquals(now, existingEntity.getFinishedAt());
        assertNotNull(existingEntity.getUpdatedAt());

        // Verify mesocycle relationship is set
        assertNotNull(existingEntity.getMesocycle());
        assertEquals(2L, existingEntity.getMesocycle().getId());
    }

    @Test
    void updateEntity_WithNullPayload_ShouldNotCrash() {
        // Given
        Day existingEntity = Day.builder().id(1L).build();

        // When & Then
        assertDoesNotThrow(() -> dayMapper.updateEntity(existingEntity, null));
    }

    @Test
    void updateEntity_WithNullEntity_ShouldNotCrash() {
        // When & Then
        assertDoesNotThrow(() -> dayMapper.updateEntity(null, samplePayload));
    }

    @Test
    void mergeEntity_WithValidData_ShouldCreateNewEntityWithUpdatedFields() {
        // Given
        Day existingEntity = Day.builder()
                .id(1L)
                .mesoId(1L)
                .label("Old Label")
                .position(1)
                .week(1)
                .bodyweight(65.0)
                .unit("lb")
                .createdAt(now.minusSeconds(7200))
                .build();

        DayPayload updatePayload = new DayPayload(
                1L, 2L, "New Label", 2, 2, 70.0, "kg", now, now, now,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList()
        );

        // When
        Day result = dayMapper.mergeEntity(existingEntity, updatePayload);

        // Then
        assertNotNull(result);
        assertNotSame(existingEntity, result); // Should be a new instance
        assertEquals(1L, result.getId());
        assertEquals(2L, result.getMesoId());
        assertEquals("New Label", result.getLabel());
        assertEquals(2, result.getPosition());
        assertEquals(2, result.getWeek());
        assertEquals(70.0, result.getBodyweight());
        assertEquals("kg", result.getUnit());
        assertEquals(now, result.getBodyweightAt());
        assertEquals(now, result.getFinishedAt());
        assertEquals(now.minusSeconds(7200), result.getCreatedAt()); // Preserved from existing
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void mergeEntity_WithNullPayload_ShouldReturnNull() {
        // Given
        Day existingEntity = Day.builder().id(1L).build();

        // When
        Day result = dayMapper.mergeEntity(existingEntity, null);

        // Then
        assertNull(result);
    }

    @Test
    void mergeEntity_WithNullEntity_ShouldReturnNull() {
        // When
        Day result = dayMapper.mergeEntity(null, samplePayload);

        // Then
        assertNull(result);
    }
}
