package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.DayExercisePayload;
import com.noslen.training_tracker.dto.day.ExerciseSetPayload;
import com.noslen.training_tracker.enums.Status;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.model.day.ExerciseSet;
import com.noslen.training_tracker.enums.SetType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DayExerciseMapperTest {

    @Mock
    private ExerciseSetMapper exerciseSetMapper;

    @InjectMocks
    private DayExerciseMapper dayExerciseMapper;

    private DayExercisePayload testPayload;
    private DayExercise testEntity;
    private ExerciseSetPayload testSetPayload;
    private ExerciseSet testSetEntity;
    private Instant testTime;

    @BeforeEach
    void setUp() {
        testTime = Instant.now();
        
        testSetPayload = new ExerciseSetPayload(
                1L, 1L, 1, "regular", 100.0f, 105.0f, 95.0f, 110.0f,
                10, 12, 80.0f, "kg", testTime, testTime, "complete"
        );
        
        testSetEntity = ExerciseSet.builder()
                .id(1L)
                .position(1)
                .setType(SetType.REGULAR)
                .weight(100.0f)
                .weightTarget(105.0f)
                .weightTargetMin(95.0f)
                .weightTargetMax(110.0f)
                .reps(10)
                .repsTarget(12)
                .bodyweight(80.0f)
                .unit("kg")
                .createdAt(testTime)
                .finishedAt(testTime)
                .status(Status.COMPLETE)
                .build();

        testPayload = new DayExercisePayload(
                1L, 2L, 3L, 1, 2, testTime, testTime, 4L, 5L,
                List.of(testSetPayload), "ready"
        );

        testEntity = DayExercise.builder()
                .id(1L)
                .day(com.noslen.training_tracker.model.day.Day.builder().id(2L).build())
                .exercise(com.noslen.training_tracker.model.exercise.Exercise.builder().id(3L).build())
                .position(1)
                .jointPain(2)
                .createdAt(testTime)
                .updatedAt(testTime)
                .sourceDayExerciseId(4L)
                .muscleGroup(com.noslen.training_tracker.model.day.DayMuscleGroup.builder().id(5L).build())
                .status(Status.READY)
                .sets(List.of(testSetEntity))
                .build();
    }

    @Test
    void toEntity_WithValidPayload_ShouldMapCorrectly() {
        // Given
        when(exerciseSetMapper.toEntity(any(ExerciseSetPayload.class))).thenReturn(testSetEntity);

        // When
        DayExercise result = dayExerciseMapper.toEntity(testPayload);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPosition()).isEqualTo(1);
        assertThat(result.getJointPain()).isEqualTo(2);
        assertThat(result.getCreatedAt()).isEqualTo(testTime);
        assertThat(result.getUpdatedAt()).isEqualTo(testTime);
        assertThat(result.getSourceDayExerciseId()).isEqualTo(4L);
        assertThat(result.getStatus()).isEqualTo(Status.READY);
        assertThat(result.getDayId()).isEqualTo(2L);
        assertThat(result.getExerciseId()).isEqualTo(3L);
        assertThat(result.getMuscleGroupId()).isEqualTo(5L);
        assertThat(result.getSets()).hasSize(1);
    }

    @Test
    void toEntity_WithNullPayload_ShouldReturnNull() {
        // When
        DayExercise result = dayExerciseMapper.toEntity(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toEntity_WithNullCreatedAt_ShouldSetCurrentTime() {
        // Given
        DayExercisePayload payloadWithNullCreatedAt = new DayExercisePayload(
                1L, 2L, 3L, 1, 2, null, testTime, 4L, 5L,
                Collections.emptyList(), "ready"
        );

        // When
        DayExercise result = dayExerciseMapper.toEntity(payloadWithNullCreatedAt);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getCreatedAt()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    void toPayload_WithValidEntity_ShouldMapCorrectly() {
        // Given
        when(exerciseSetMapper.toPayload(any(ExerciseSet.class))).thenReturn(testSetPayload);

        // When
        DayExercisePayload result = dayExerciseMapper.toPayload(testEntity);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.dayId()).isEqualTo(2L);
        assertThat(result.exerciseId()).isEqualTo(3L);
        assertThat(result.position()).isEqualTo(1);
        assertThat(result.jointPain()).isEqualTo(2);
        assertThat(result.createdAt()).isEqualTo(testTime);
        assertThat(result.updatedAt()).isEqualTo(testTime);
        assertThat(result.sourceDayExerciseId()).isEqualTo(4L);
        assertThat(result.muscleGroupId()).isEqualTo(5L);
        assertThat(result.status()).isEqualTo("ready");
        assertThat(result.sets()).hasSize(1);
    }

    @Test
    void toPayload_WithNullEntity_ShouldReturnNull() {
        // When
        DayExercisePayload result = dayExerciseMapper.toPayload(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toPayload_WithNullSets_ShouldHandleGracefully() {
        // Given
        DayExercise entityWithNullSets = DayExercise.builder()
                .id(1L)
                .position(1)
                .jointPain(2)
                .createdAt(testTime)
                .updatedAt(testTime)
                .status(Status.READY)
                .sets(null)
                .build();

        // When
        DayExercisePayload result = dayExerciseMapper.toPayload(entityWithNullSets);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.sets()).isNull();
    }

    @Test
    void updateEntity_WithValidData_ShouldUpdateMutableFields() {
        // Given
        DayExercise existing = DayExercise.builder()
                .id(1L)
                .position(1)
                .jointPain(2)
                .createdAt(testTime)
                .updatedAt(testTime)
                .status(Status.READY)
                .build();

        DayExercisePayload updatePayload = new DayExercisePayload(
                null, 10L, 11L, 5, 3, null, null, null, 12L,
                null, "ready"
        );

        // When
        dayExerciseMapper.updateEntity(existing, updatePayload);

        // Then
        assertThat(existing.getUpdatedAt()).isAfter(testTime);
        assertThat(existing.getDayId()).isEqualTo(10L);
        assertThat(existing.getExerciseId()).isEqualTo(11L);
        assertThat(existing.getMuscleGroupId()).isEqualTo(12L);
        // Note: position, jointPain, status cannot be updated due to immutable design
        assertThat(existing.getPosition()).isEqualTo(1); // unchanged
        assertThat(existing.getJointPain()).isEqualTo(2); // unchanged
        assertThat(existing.getStatus()).isEqualTo(Status.READY); // unchanged
    }

    @Test
    void updateEntity_WithNullInputs_ShouldHandleGracefully() {
        // When/Then - should not throw exceptions
        dayExerciseMapper.updateEntity(null, testPayload);
        dayExerciseMapper.updateEntity(testEntity, null);
        dayExerciseMapper.updateEntity(null, null);
    }

    @Test
    void mergeEntity_WithValidData_ShouldCreateNewEntityWithUpdatedFields() {
        // Given
        DayExercise existing = DayExercise.builder()
                .id(1L)
                .position(1)
                .jointPain(2)
                .createdAt(testTime)
                .updatedAt(testTime)
                .status(Status.READY)
                .build();

        DayExercisePayload updatePayload = new DayExercisePayload(
                null, 10L, 11L, 5, 3, null, null, null, 12L,
                null, "ready"
        );

        // When
        DayExercise result = dayExerciseMapper.mergeEntity(existing, updatePayload);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L); // preserved
        assertThat(result.getCreatedAt()).isEqualTo(testTime); // preserved
        assertThat(result.getUpdatedAt()).isAfter(testTime); // updated
        assertThat(result.getDayId()).isEqualTo(10L); // updated
        assertThat(result.getExerciseId()).isEqualTo(11L); // updated
        assertThat(result.getMuscleGroupId()).isEqualTo(12L); // updated
        assertThat(result.getPosition()).isEqualTo(5); // updated
        assertThat(result.getJointPain()).isEqualTo(3); // updated
        assertThat(result.getStatus()).isEqualTo(Status.READY); // updated
    }

    @Test
    void mergeEntity_WithNullExisting_ShouldReturnNewEntity() {
        // When
        DayExercise result = dayExerciseMapper.mergeEntity(null, testPayload);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testPayload.id());
    }

    @Test
    void mergeEntity_WithNullPayload_ShouldReturnExisting() {
        // When
        DayExercise result = dayExerciseMapper.mergeEntity(testEntity, null);

        // Then
        assertThat(result).isSameAs(testEntity);
    }

    @Test
    void toPayloadList_WithValidList_ShouldMapAll() {
        // Given
        when(exerciseSetMapper.toPayload(any(ExerciseSet.class))).thenReturn(testSetPayload);
        List<DayExercise> entities = Arrays.asList(testEntity, testEntity);

        // When
        List<DayExercisePayload> result = dayExerciseMapper.toPayloadList(entities);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(1).id()).isEqualTo(1L);
    }

    @Test
    void toPayloadList_WithNullList_ShouldReturnNull() {
        // When
        List<DayExercisePayload> result = dayExerciseMapper.toPayloadList(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toPayloadList_WithEmptyList_ShouldReturnEmptyList() {
        // When
        List<DayExercisePayload> result = dayExerciseMapper.toPayloadList(Collections.emptyList());

        // Then
        assertThat(result).isEmpty();
    }
}
