package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.ExerciseSetPayload;
import com.noslen.training_tracker.enums.Status;
import com.noslen.training_tracker.enums.Unit;
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

    private ExerciseSetPayload testPayload;
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

        testPayload = new ExerciseSetPayload(
                1L, 2L, 1, "regular", 100.0f, 105.0f, 95.0f, 110.0f,
                10, 12, 80.0f, "kgs", testTime, testTime, "complete"
        );

        testEntity = ExerciseSet.builder()
                .id(1L)
                .dayExercise(testDayExercise)
                .position(1)
                .setType(SetType.REGULAR)
                .weight(100.0f)
                .weightTarget(105.0f)
                .weightTargetMin(95.0f)
                .weightTargetMax(110.0f)
                .reps(10)
                .repsTarget(12)
                .bodyweight(80.0f)
                .unit(exerciseSetMapper.stringToUnit("kgs"))
                .createdAt(testTime)
                .finishedAt(testTime)
                .status(Status.COMPLETE)
                .build();
    }

    @Test
    void toEntity_WithValidPayload_ShouldMapCorrectly() {
        // When
        ExerciseSet result = exerciseSetMapper.toEntity(testPayload);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPosition()).isEqualTo(1);
        assertThat(result.getSetType()).isEqualTo(SetType.REGULAR);
        assertThat(result.getWeight()).isEqualTo(100.0f);
        assertThat(result.getWeightTarget()).isEqualTo(105.0f);
        assertThat(result.getWeightTargetMin()).isEqualTo(95.0f);
        assertThat(result.getWeightTargetMax()).isEqualTo(110.0f);
        assertThat(result.getReps()).isEqualTo(10);
        assertThat(result.getRepsTarget()).isEqualTo(12);
        assertThat(result.getBodyweight()).isEqualTo(80.0f);
        assertThat(result.getUnit()).isEqualTo(exerciseSetMapper.stringToUnit("kgs"));
        assertThat(result.getCreatedAt()).isEqualTo(testTime);
        assertThat(result.getFinishedAt()).isEqualTo(testTime);
        assertThat(result.getStatus()).isEqualTo(Status.COMPLETE);
        assertThat(result.getDayExercise()).isNull(); // Not set in basic toEntity
    }

    @Test
    void toEntity_WithNullPayload_ShouldReturnNull() {
        // When
        ExerciseSet result = exerciseSetMapper.toEntity(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toEntity_WithDayExerciseContext_ShouldSetDayExercise() {
        // When
        ExerciseSet result = exerciseSetMapper.toEntity(testPayload, testDayExercise);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDayExercise()).isEqualTo(testDayExercise);
        assertThat(result.getPosition()).isEqualTo(1);
        assertThat(result.getSetType()).isEqualTo(SetType.REGULAR);
    }

    @Test
    void toEntity_WithNullDayExerciseContext_ShouldNotSetDayExercise() {
        // When
        ExerciseSet result = exerciseSetMapper.toEntity(testPayload, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDayExercise()).isNull();
    }

    @Test
    void toEntity_WithNullPayloadAndDayExercise_ShouldReturnNull() {
        // When
        ExerciseSet result = exerciseSetMapper.toEntity(null, testDayExercise);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toPayload_WithValidEntity_ShouldMapCorrectly() {
        // When
        ExerciseSetPayload result = exerciseSetMapper.toPayload(testEntity);

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
        assertThat(result.unit()).isEqualTo("kgs");
        assertThat(result.createdAt()).isEqualTo(testTime);
        assertThat(result.finishedAt()).isEqualTo(testTime);
        assertThat(result.status()).isEqualTo("complete");
    }

    @Test
    void toPayload_WithNullEntity_ShouldReturnNull() {
        // When
        ExerciseSetPayload result = exerciseSetMapper.toPayload(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toPayload_WithNullDayExercise_ShouldHandleGracefully() {
        // Given
        ExerciseSet entityWithNullDayExercise = ExerciseSet.builder()
                .id(1L)
                .position(1)
                .setType(SetType.REGULAR)
                .weight(100.0f)
                .reps(10)
                .unit(exerciseSetMapper.stringToUnit("kgs"))
                .createdAt(testTime)
                .status(Status.COMPLETE)
                .dayExercise(null)
                .build();

        // When
        ExerciseSetPayload result = exerciseSetMapper.toPayload(entityWithNullDayExercise);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.dayExerciseId()).isNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void updateEntity_WithValidData_ShouldUpdateMutableFields() {
        // Given
        ExerciseSet existing = ExerciseSet.builder()
                .id(1L)
                .position(1)
                .setType(SetType.REGULAR)
                .weight(100.0f)
                .reps(10)
                .unit(exerciseSetMapper.stringToUnit("kgs"))
                .createdAt(testTime)
                .finishedAt(null)
                .status(Status.READY)
                .build();

        Instant newFinishedTime = testTime.plusSeconds(3600);
        ExerciseSetPayload updatePayload = new ExerciseSetPayload(
                null, null, 2, "myo_rep", 110.0f, 115.0f, 105.0f, 120.0f,
                12, 15, 85.0f, "lbs", testTime.plusSeconds(60), newFinishedTime, "complete"
        );

        // When
        exerciseSetMapper.updateEntity(existing, updatePayload);

        // Then
        // Only mutable fields should be updated
        assertThat(existing.getCreatedAt()).isEqualTo(testTime.plusSeconds(60));
        assertThat(existing.getFinishedAt()).isEqualTo(newFinishedTime);
        assertThat(existing.getStatus()).isEqualTo(Status.COMPLETE);
        
        // Immutable fields should remain unchanged
        assertThat(existing.getPosition()).isEqualTo(1); // unchanged
        assertThat(existing.getSetType()).isEqualTo(SetType.REGULAR); // unchanged
        assertThat(existing.getWeight()).isEqualTo(100.0f); // unchanged
        assertThat(existing.getReps()).isEqualTo(10); // unchanged
        assertThat(existing.getUnit()).isEqualTo(Unit.KGS); // unchanged
    }

    @Test
    void updateEntity_WithNullInputs_ShouldHandleGracefully() {
        // When/Then - should not throw exceptions
        exerciseSetMapper.updateEntity(null, testPayload);
        exerciseSetMapper.updateEntity(testEntity, null);
        exerciseSetMapper.updateEntity(null, null);
    }

    @Test
    void updateEntity_WithPartialData_ShouldUpdateOnlyProvidedFields() {
        // Given
        ExerciseSet existing = ExerciseSet.builder()
                .id(1L)
                .createdAt(testTime)
                .finishedAt(null)
                .status(Status.READY)
                .build();

        ExerciseSetPayload partialPayload = new ExerciseSetPayload(
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, testTime.plusSeconds(3600), "complete"
        );

        // When
        exerciseSetMapper.updateEntity(existing, partialPayload);

        // Then
        assertThat(existing.getCreatedAt()).isEqualTo(testTime); // unchanged (null in payload)
        assertThat(existing.getFinishedAt()).isEqualTo(testTime.plusSeconds(3600)); // updated
        assertThat(existing.getStatus()).isEqualTo(Status.COMPLETE); // updated
    }

    @Test
    void mergeEntity_WithValidData_ShouldCreateNewEntityWithUpdatedFields() {
        // Given
        ExerciseSet existing = ExerciseSet.builder()
                .id(1L)
                .dayExercise(testDayExercise)
                .position(1)
                .setType(SetType.REGULAR)
                .weight(100.0f)
                .reps(10)
                .unit(exerciseSetMapper.stringToUnit("kgs"))
                .createdAt(testTime)
                .finishedAt(null)
                .status(Status.READY)
                .build();

        ExerciseSetPayload updatePayload = new ExerciseSetPayload(
                null, null, 2, "myorep", 110.0f, 115.0f, 105.0f, 120.0f,
                12, 15, 85.0f, "lbs", null, testTime.plusSeconds(3600), "complete"
        );

        // When
        ExerciseSet result = exerciseSetMapper.mergeEntity(existing, updatePayload);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L); // preserved
        assertThat(result.getDayExercise()).isEqualTo(testDayExercise); // preserved
        assertThat(result.getCreatedAt()).isEqualTo(testTime); // preserved (null in payload)
        
        // Updated fields
        assertThat(result.getPosition()).isEqualTo(2);
        assertThat(result.getSetType()).isEqualTo(SetType.MYOREP);
        assertThat(result.getWeight()).isEqualTo(110.0f);
        assertThat(result.getWeightTarget()).isEqualTo(115.0f);
        assertThat(result.getWeightTargetMin()).isEqualTo(105.0f);
        assertThat(result.getWeightTargetMax()).isEqualTo(120.0f);
        assertThat(result.getReps()).isEqualTo(12);
        assertThat(result.getRepsTarget()).isEqualTo(15);
        assertThat(result.getBodyweight()).isEqualTo(85.0f);
        assertThat(result.getUnit()).isEqualTo(Unit.LBS);
        assertThat(result.getFinishedAt()).isEqualTo(testTime.plusSeconds(3600));
        assertThat(result.getStatus()).isEqualTo(Status.COMPLETE);
    }

    @Test
    void mergeEntity_WithNullExisting_ShouldReturnNewEntity() {
        // When
        ExerciseSet result = exerciseSetMapper.mergeEntity(null, testPayload);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testPayload.id());
        assertThat(result.getPosition()).isEqualTo(testPayload.position());
    }

    @Test
    void mergeEntity_WithNullPayload_ShouldReturnExisting() {
        // When
        ExerciseSet result = exerciseSetMapper.mergeEntity(testEntity, null);

        // Then
        assertThat(result).isSameAs(testEntity);
    }

    @Test
    void toPayloadList_WithValidList_ShouldMapAll() {
        // Given
        List<ExerciseSet> entities = Arrays.asList(testEntity, testEntity);

        // When
        List<ExerciseSetPayload> result = exerciseSetMapper.toPayloadList(entities);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(1).id()).isEqualTo(1L);
    }

    @Test
    void toPayloadList_WithNullList_ShouldReturnNull() {
        // When
        List<ExerciseSetPayload> result = exerciseSetMapper.toPayloadList(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toPayloadList_WithEmptyList_ShouldReturnEmptyList() {
        // When
        List<ExerciseSetPayload> result = exerciseSetMapper.toPayloadList(Collections.emptyList());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void toPayloadList_WithMixedNullEntities_ShouldHandleGracefully() {
        // Given
        List<ExerciseSet> entitiesWithNull = Arrays.asList(testEntity, null, testEntity);

        // When
        List<ExerciseSetPayload> result = exerciseSetMapper.toPayloadList(entitiesWithNull);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(1)).isNull();
        assertThat(result.get(2)).isNotNull();
    }
}
