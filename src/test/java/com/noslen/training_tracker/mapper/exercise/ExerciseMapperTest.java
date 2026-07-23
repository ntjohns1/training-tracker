package com.noslen.training_tracker.mapper.exercise;

import com.noslen.training_tracker.dto.exercise.response.ExerciseNoteResponse;
import com.noslen.training_tracker.dto.exercise.response.ExerciseResponse;
import com.noslen.training_tracker.enums.ExerciseType;
import com.noslen.training_tracker.enums.MgSubType;
import com.noslen.training_tracker.model.exercise.Exercise;
import com.noslen.training_tracker.model.exercise.ExerciseNote;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExerciseMapperTest {

    @Mock
    private ExerciseNoteMapper exerciseNoteMapper;

    @InjectMocks
    private ExerciseMapper exerciseMapper;

    private Exercise testEntity;
    private Instant testTime;
    private ExerciseNoteResponse testNotePayload;

    @BeforeEach
    void setUp() {
        testTime = Instant.now();

        testNotePayload = new ExerciseNoteResponse(
                1L, 2L, 3L, 4L, 5L, testTime, testTime, "Test note"
        );

        ExerciseNote testNoteEntity = new ExerciseNote();
        testNoteEntity.setId(1L);
        testNoteEntity.setText("Test note");

        testEntity = new Exercise();
        testEntity.setId(1L);
        testEntity.setName("Test Exercise");
        testEntity.setMuscleGroupId(2L);
        testEntity.setYoutubeId("youtube123");
        testEntity.setExerciseType(ExerciseType.BARBELL);
        testEntity.setUserId(3L);
        testEntity.setCreatedAt(testTime);
        testEntity.setUpdatedAt(testTime);
        testEntity.setDeletedAt(null);
        testEntity.setMgSubType(MgSubType.VERTICAL);
        testEntity.setNotes(Collections.singletonList(testNoteEntity));
    }

    @Test
    void toPayload_WithValidEntity_ShouldMapCorrectly() {
        when(exerciseNoteMapper.toPayloadList(anyList())).thenReturn(Collections.singletonList(testNotePayload));

        ExerciseResponse result = exerciseMapper.toPayload(testEntity);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Test Exercise");
        assertThat(result.muscleGroupId()).isEqualTo(2L);
        assertThat(result.youtubeId()).isEqualTo("youtube123");
        assertThat(result.exerciseType()).isEqualTo("barbell");
        assertThat(result.userId()).isEqualTo(3L);
        assertThat(result.createdAt()).isEqualTo(testTime);
        assertThat(result.updatedAt()).isEqualTo(testTime);
        assertThat(result.deletedAt()).isNull();
        assertThat(result.mgSubType()).isEqualTo("vertical");
        assertThat(result.notes()).hasSize(1);
    }

    @Test
    void toPayload_WithNullEntity_ShouldReturnNull() {
        assertThat(exerciseMapper.toPayload(null)).isNull();
    }

    @Test
    void toPayloadList_WithValidList_ShouldMapAll() {
        List<Exercise> entities = Arrays.asList(testEntity, testEntity);
        when(exerciseNoteMapper.toPayloadList(anyList())).thenReturn(Collections.singletonList(testNotePayload));

        List<ExerciseResponse> result = exerciseMapper.toPayloadList(entities);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(1).id()).isEqualTo(1L);
    }

    @Test
    void toPayloadList_WithNullList_ShouldReturnNull() {
        assertThat(exerciseMapper.toPayloadList(null)).isNull();
    }

    @Test
    void toPayloadList_WithEmptyList_ShouldReturnEmptyList() {
        assertThat(exerciseMapper.toPayloadList(Collections.emptyList())).isEmpty();
    }

    @Test
    void toPayloadList_WithMixedNullEntities_ShouldHandleGracefully() {
        List<Exercise> entitiesWithNull = Arrays.asList(testEntity, null, testEntity);
        when(exerciseNoteMapper.toPayloadList(anyList())).thenReturn(Collections.singletonList(testNotePayload));

        List<ExerciseResponse> result = exerciseMapper.toPayloadList(entitiesWithNull);

        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(1)).isNull();
        assertThat(result.get(2)).isNotNull();
    }
}
