package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.response.DayExerciseResponse;
import com.noslen.training_tracker.dto.day.response.DayMuscleGroupResponse;
import com.noslen.training_tracker.dto.day.response.DayNoteResponse;
import com.noslen.training_tracker.dto.day.response.DayResponse;
import com.noslen.training_tracker.enums.ExerciseType;
import com.noslen.training_tracker.enums.Status;
import com.noslen.training_tracker.enums.Unit;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.model.day.DayMuscleGroup;
import com.noslen.training_tracker.model.day.DayNote;
import com.noslen.training_tracker.model.progression.MuscleGroup;
import com.noslen.training_tracker.enums.MgName;
import com.noslen.training_tracker.enums.MgSubType;
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

    private DayResponse samplePayload;
    private Day sampleEntity;
    private Instant now;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        now = Instant.now();

        // Sample nested payloads
        DayNoteResponse notePayload = new DayNoteResponse(1L, 1L, 1L, true, now, now, "Test note");
        DayExerciseResponse exercisePayload = new DayExerciseResponse(1L, 1L, 1L, 1, 0, now, now, null, 1L, Collections.emptyList(), "active");
        DayMuscleGroupResponse muscleGroupPayload = new DayMuscleGroupResponse(1L, 1L, 1L, 0, 0, 0, now, now, 3, "active");

        samplePayload = new DayResponse(
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
        DayNote noteEntity = new DayNote();
        noteEntity.setId(1L);
        noteEntity.setDay(Day.builder().id(1L).build());
        noteEntity.setNoteId(1L);
        noteEntity.setText("Test note");
        noteEntity.setPinned(true);
        noteEntity.setCreatedAt(now);
        noteEntity.setUpdatedAt(now);

        Exercise exercise = new Exercise();
        exercise.setId(1L);
        exercise.setName("Test Exercise");
        exercise.setMuscleGroupId(1L);
        exercise.setYoutubeId("Test YouTube ID");
        exercise.setExerciseType(ExerciseType.BARBELL);
        exercise.setUserId(1L);
        exercise.setCreatedAt(now);
        exercise.setUpdatedAt(now);
        exercise.setDeletedAt(now);
        exercise.setMgSubType(MgSubType.VERTICAL);
        ExerciseNote exerciseNote = new ExerciseNote();
        exercise.setNotes(List.of(exerciseNote));

        DayExercise exerciseEntity = DayExercise.builder()
                .id(1L)
                .day(Day.builder().id(1L).build())
                .exercise(exercise)
                .position(1)
                .jointPain(0)
                .createdAt(now)
                .updatedAt(now)
                .muscleGroup(new MuscleGroup(1L, MgName.CHEST, now, now))
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
                .unit(Unit.KG)
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
        assertEquals(Unit.KG, result.getUnit());
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
        DayResponse payloadWithEmptyCollections = new DayResponse(
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
        DayResponse payloadWithNullCollections = new DayResponse(
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
        DayNoteResponse notePayload = new DayNoteResponse(1L, 1L, 1L, true, now, now, "Test");
        DayExerciseResponse exercisePayload = new DayExerciseResponse(1L, 1L, 1L, 1, 0, now, now, null, 1L, Collections.emptyList(), "active");
        DayMuscleGroupResponse muscleGroupPayload = new DayMuscleGroupResponse(1L, 1L, 1L, 2, 2, 2, now, now, 2, "active");

        when(dayNoteMapper.toPayload(any(DayNote.class))).thenReturn(notePayload);
        when(dayExerciseMapper.toPayload(any(DayExercise.class))).thenReturn(exercisePayload);
        when(dayMuscleGroupMapper.toPayload(any(DayMuscleGroup.class))).thenReturn(muscleGroupPayload);

        // When
        DayResponse result = dayMapper.toPayload(sampleEntity);

        // Then
        assertNotNull(result);
        assertEquals(sampleEntity.getId(), result.id());
        assertEquals(sampleEntity.getMesoId(), result.mesoId());
        assertEquals(sampleEntity.getLabel(), result.label());
        assertEquals(sampleEntity.getPosition().longValue(), result.position()); // Entity uses Integer, DTO uses Long
        assertEquals(sampleEntity.getWeek().longValue(), result.week()); // Entity uses Integer, DTO uses Long
        assertEquals(sampleEntity.getBodyweight().intValue(), result.bodyweight()); // Entity uses Double, DTO uses Integer
        assertEquals(dayMapper.unitToString(sampleEntity.getUnit()), result.unit());
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
        DayResponse result = dayMapper.toPayload(null);

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
                .unit(Unit.KG)
                .bodyweightAt(now)
                .createdAt(now)
                .updatedAt(now)
                .notes(Collections.emptyList())
                .exercises(Collections.emptyList())
                .muscleGroups(Collections.emptyList())
                .build();

        // When
        DayResponse result = dayMapper.toPayload(entityWithEmptyCollections);

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
                .unit(Unit.KG)
                .bodyweightAt(now)
                .createdAt(now)
                .updatedAt(now)
                .notes(null)
                .exercises(null)
                .muscleGroups(null)
                .build();

        // When
        DayResponse result = dayMapper.toPayload(entityWithNullCollections);

        // Then
        assertNotNull(result);
        assertEquals(Collections.emptyList(), result.notes()); // Mapper returns empty list, not null
        assertEquals(Collections.emptyList(), result.exercises()); // Mapper returns empty list, not null
        assertEquals(Collections.emptyList(), result.muscleGroups()); // Mapper returns empty list, not null
    }

}
