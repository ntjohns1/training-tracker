package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.response.ExerciseSetResponse;
import com.noslen.training_tracker.enums.Status;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.model.day.ExerciseSet;
import com.noslen.training_tracker.enums.SetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ExerciseSetMapperTest {

    @InjectMocks
    private ExerciseSetMapper exerciseSetMapper;

    private ExerciseSet testEntity;
    private DayExercise testDayExercise;
    private Instant testTime;

    @BeforeEach
    void setUp() {
        testTime = Instant.now();

        testDayExercise = DayExercise.builder()
                .id(1L)
                .position(1)
                .build();

        testEntity = new ExerciseSet();
        testEntity.setId(1L);
        testEntity.setDayExercise(testDayExercise);
        testEntity.setPosition(1);
        testEntity.setSetType(SetType.REGULAR);
        testEntity.setWeight(100.0f);
        testEntity.setWeightTarget(105.0f);
        testEntity.setWeightTargetMin(95.0f);
        testEntity.setWeightTargetMax(110.0f);
        testEntity.setReps(10);
        testEntity.setRepsTarget(12);
        testEntity.setBodyweight(80.0f);
        testEntity.setUnit(exerciseSetMapper.stringToUnit("kg"));
        testEntity.setCreatedAt(testTime);
        testEntity.setFinishedAt(testTime);
        testEntity.setStatus(Status.COMPLETE);
    }

    @Test
    void toPayload_WithValidEntity_ShouldMapCorrectly() {
        // When
        ExerciseSetResponse result = exerciseSetMapper.toPayload(testEntity);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.dayExerciseId()).isEqualTo(1L);
        assertThat(result.position()).isEqualTo(1);
        assertThat(result.setType()).isEqualTo("regular");
        assertThat(result.weight()).isEqualTo(100.0f);
        assertThat(result.weightTarget()).isEqualTo(105.0f);
        assertThat(result.weightTargetMin()).isEqualTo(95.0f);
        assertThat(result.weightTargetMax()).isEqualTo(110.0f);
        assertThat(result.reps()).isEqualTo(10);
        assertThat(result.repsTarget()).isEqualTo(12);
        assertThat(result.bodyweight()).isEqualTo(80.0f);
        assertThat(result.unit()).isEqualTo("kg");
        assertThat(result.createdAt()).isEqualTo(testTime);
        assertThat(result.finishedAt()).isEqualTo(testTime);
        assertThat(result.status()).isEqualTo("complete");
    }

    @Test
    void toPayload_WithNullEntity_ShouldReturnNull() {
        // When
        ExerciseSetResponse result = exerciseSetMapper.toPayload(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toPayload_WithNullDayExercise_ShouldHandleGracefully() {
        // Given
        ExerciseSet entityWithNullDayExercise = new ExerciseSet();
        entityWithNullDayExercise.setId(1L);
        entityWithNullDayExercise.setPosition(1);
        entityWithNullDayExercise.setSetType(SetType.REGULAR);
        entityWithNullDayExercise.setWeight(100.0f);
        entityWithNullDayExercise.setReps(10);
        entityWithNullDayExercise.setUnit(exerciseSetMapper.stringToUnit("kg"));
        entityWithNullDayExercise.setCreatedAt(testTime);
        entityWithNullDayExercise.setStatus(Status.COMPLETE);
        entityWithNullDayExercise.setDayExercise(null);

        // When
        ExerciseSetResponse result = exerciseSetMapper.toPayload(entityWithNullDayExercise);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.dayExerciseId()).isNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void toPayloadList_WithValidList_ShouldMapAll() {
        // Given
        List<ExerciseSet> entities = Arrays.asList(testEntity, testEntity);

        // When
        List<ExerciseSetResponse> result = exerciseSetMapper.toPayloadList(entities);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(1).id()).isEqualTo(1L);
    }

    @Test
    void toPayloadList_WithNullList_ShouldReturnNull() {
        // When
        List<ExerciseSetResponse> result = exerciseSetMapper.toPayloadList(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toPayloadList_WithEmptyList_ShouldReturnEmptyList() {
        // When
        List<ExerciseSetResponse> result = exerciseSetMapper.toPayloadList(Collections.emptyList());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void toPayloadList_WithMixedNullEntities_ShouldHandleGracefully() {
        // Given
        List<ExerciseSet> entitiesWithNull = Arrays.asList(testEntity, null, testEntity);

        // When
        List<ExerciseSetResponse> result = exerciseSetMapper.toPayloadList(entitiesWithNull);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(1)).isNull();
        assertThat(result.get(2)).isNotNull();
    }
}
