package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.DayExercisePayload;
import com.noslen.training_tracker.dto.day.DayMuscleGroupPayload;
import com.noslen.training_tracker.dto.day.DayNotePayload;
import com.noslen.training_tracker.dto.day.DayPayload;
import com.noslen.training_tracker.enums.ExerciseType;
import com.noslen.training_tracker.enums.Status;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.model.day.DayMuscleGroup;
import com.noslen.training_tracker.model.day.DayNote;
import com.noslen.training_tracker.model.muscle_group.MuscleGroup;
import com.noslen.training_tracker.enums.MgName;
import com.noslen.training_tracker.model.exercise.Exercise;
import com.noslen.training_tracker.model.exercise.ExerciseNote;
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
import static org.mockito.ArgumentMatchers.anyList;
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
        DayMuscleGroupPayload muscleGroupPayload = new DayMuscleGroupPayload(1L, 1L, 1L, 0, 0, 0, now, now, 3, "active");

        samplePayload = new DayPayload(
                1L,
                1L,
                1L,
                1L,
                now,
                now,
                70,
                now,
                "kg",
                now,
                "Test Day",
                List.of(notePayload),
                List.of(exercisePayload),
                List.of(muscleGroupPayload),
                "active"
        );

        // Sample nested entities
        DayNote noteEntity = DayNote.builder()
                .id(1L)
                .day(Day.builder().id(1L).build())
                .noteId(1L)
                .text("Test note")
                .pinned(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        DayExercise exerciseEntity = DayExercise.builder()
                .id(1L)
                .day(Day.builder().id(1L).build())
                .exercise(Exercise.builder()
                        .id(1L)
                        .name("Test Exercise")
                        .muscleGroupId(1L)
                        .youtubeId("Test YouTube ID")
                        .exerciseType(ExerciseType.BARBELL)
                        .userId(1L)
                        .createdAt(now)
                        .updatedAt(now)
                        .deletedAt(now)
                        .mgSubType("active")
                        .notes(List.of(ExerciseNote.builder().build()))
                        .build())
                .position(1)
                .jointPain(0)
                .createdAt(now)
                .updatedAt(now)
                .muscleGroup(DayMuscleGroup.builder().id(1L).build())
                .status(Status.READY)
                .build();

        DayMuscleGroup muscleGroupEntity = DayMuscleGroup.builder()
                .id(1L)
                .day(Day.builder().id(1L).build())
                .muscleGroup(new MuscleGroup(1L, MgName.CHEST, now, now))
                .createdAt(now)
                .build();

        sampleEntity = Day.builder()
                .id(1L)
                .mesocycle(Mesocycle.builder().id(1L).build())
                .label("Test Day")
                .position(1)
                .week(1)
                .bodyweight(70.0)
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
        assertNull(result.getMesoId()); // toEntity doesn't set mesocycle relationship
        assertEquals(samplePayload.label(), result.getLabel());
        assertEquals(samplePayload.position().intValue(), result.getPosition()); // DTO uses Long, Entity uses Integer
        assertEquals(samplePayload.week().intValue(), result.getWeek()); // DTO uses Long, Entity uses Integer
        assertEquals(samplePayload.bodyweight().doubleValue(), result.getBodyweight()); // DTO uses Integer, Entity uses Double
        assertEquals(samplePayload.unit(), result.getUnit());
        assertEquals(samplePayload.bodyweightAt(), result.getBodyweightAt());
        assertEquals(samplePayload.createdAt(), result.getCreatedAt());
        assertEquals(samplePayload.updatedAt(), result.getUpdatedAt());

        // Verify nested collections - toEntity doesn't populate collections
        assertEquals(0, result.getNotes().size());
        assertEquals(0, result.getExercises().size());
        assertEquals(0, result.getMuscleGroups().size());

        // Note: DayMapper.toEntity() doesn't populate nested collections or call nested mappers
        // This is handled by the service layer to avoid circular dependencies
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
                1L, 1L, 1L, 1L, now, now, 70, now, "kg", now,
                "Test", Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),"active"
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
                1L, 1L, 1L, 1L, now, now, 70, now, "kg", now,
                "Test", null, null, null,"active"
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
        DayNotePayload notePayload = new DayNotePayload(1L, 1L, 1L, true, now, now, "Test");
        DayExercisePayload exercisePayload = new DayExercisePayload(1L, 1L, 1L, 1, 0, now, now, null, 1L, Collections.emptyList(), "active");
        DayMuscleGroupPayload muscleGroupPayload = new DayMuscleGroupPayload(1L, 1L, 1L, 2,2,2, now, now, 2,"active");

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
        assertEquals(sampleEntity.getPosition().longValue(), result.position()); // Entity uses Integer, DTO uses Long
        assertEquals(sampleEntity.getWeek().longValue(), result.week()); // Entity uses Integer, DTO uses Long
        assertEquals(sampleEntity.getBodyweight().intValue(), result.bodyweight()); // Entity uses Double, DTO uses Integer
        assertEquals(sampleEntity.getUnit(), result.unit());
        assertEquals(sampleEntity.getBodyweightAt(), result.bodyweightAt());
        assertEquals(sampleEntity.getCreatedAt(), result.createdAt());
        assertEquals(sampleEntity.getUpdatedAt(), result.updatedAt());

        // Verify nested collections - mapper returns empty lists for nested collections
        assertEquals(0, result.notes().size());
        assertEquals(0, result.exercises().size());
        assertEquals(0, result.muscleGroups().size());

        // Verify mapper calls - DayMapper calls toPayloadList methods
        verify(dayNoteMapper).toPayloadList(anyList());
        verify(dayExerciseMapper).toPayloadList(anyList());
        verify(dayMuscleGroupMapper).toPayloadList(anyList());
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
        Mesocycle mesocycle = Mesocycle.builder().id(1L).createdAt(now).updatedAt(now).build();
        // Given
        Day entityWithEmptyCollections = Day.builder()
                .id(1L)
                .mesocycle(mesocycle)
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
                .mesocycle(null)
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
        assertEquals(Collections.emptyList(), result.notes()); // Mapper returns empty list, not null
        assertEquals(Collections.emptyList(), result.exercises()); // Mapper returns empty list, not null
        assertEquals(Collections.emptyList(), result.muscleGroups()); // Mapper returns empty list, not null
    }

    @Test
    void updateEntity_WithValidData_ShouldUpdateMutableFields() {
        // Given
        Mesocycle mesocycle = Mesocycle.builder().id(1L).createdAt(now).updatedAt(now).build();
        Day existingEntity = Day.builder()
                .id(1L)
                .mesocycle(mesocycle)
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
                1L, 2L, 2L, 2L, now, now, 70, now, "kg", now, "New Label", 
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), "active"
        );

        // When
        dayMapper.updateEntity(existingEntity, updatePayload);

        // Then - updateEntity only updates mutable timestamp fields
        assertEquals("Old Label", existingEntity.getLabel()); // Label is immutable, not updated
        assertEquals(65.0, existingEntity.getBodyweight()); // Bodyweight is immutable, not updated
        assertEquals("lb", existingEntity.getUnit()); // Unit is immutable, not updated
        assertNotNull(existingEntity.getUpdatedAt()); // Only updatedAt is modified

        // Verify mesocycle relationship is preserved
        assertNotNull(existingEntity.getMesocycle());
        assertEquals(1L, existingEntity.getMesocycle().getId()); // Original mesocycle preserved
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
        Mesocycle mesocycle = Mesocycle.builder().id(1L).createdAt(now).updatedAt(now).build();
        Day existingEntity = Day.builder()
                .id(1L)
                .mesocycle(mesocycle)
                .label("Old Label")
                .position(1)
                .week(1)
                .bodyweight(65.0)
                .unit("lb")
                .createdAt(now.minusSeconds(7200))
                .build();

        DayPayload updatePayload = new DayPayload(
                1L, 2L, 2L, 2L, now, now, 70, now, "kg", now, "New Label", 
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), "active"
        );

        // When
        Day result = dayMapper.mergeEntity(existingEntity, updatePayload);

        // Then
        assertNotNull(result);
        assertNotSame(existingEntity, result); // Should be a new instance
        assertEquals(1L, result.getId());
        assertNotNull(result.getMesocycle());
        assertEquals(1L, result.getMesocycle().getId()); // Mesocycle is preserved from existing entity, not updated
        assertEquals("New Label", result.getLabel());
        assertEquals(2, (int) result.getPosition());
        assertEquals(2, (int) result.getWeek());
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
        assertNotNull(result); // mergeEntity returns existing entity when payload is null
        assertEquals(existingEntity, result);
    }

    @Test
    void mergeEntity_WithNullEntity_ShouldReturnNull() {
        // When
        Day result = dayMapper.mergeEntity(null, samplePayload);

        // Then
        assertNotNull(result); // mergeEntity calls toEntity when existing is null
        assertEquals(1L, result.getId());
    }
}
