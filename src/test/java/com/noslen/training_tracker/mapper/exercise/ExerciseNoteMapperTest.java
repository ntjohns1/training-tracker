package com.noslen.training_tracker.mapper.exercise;

import com.noslen.training_tracker.dto.exercise.response.ExerciseNoteResponse;
import com.noslen.training_tracker.model.exercise.ExerciseNote;
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
class ExerciseNoteMapperTest {

    @InjectMocks
    private ExerciseNoteMapper exerciseNoteMapper;

    private ExerciseNote testEntity;
    private Instant testTime;

    @BeforeEach
    void setUp() {
        testTime = Instant.now();

        testEntity = new ExerciseNote();
        testEntity.setId(1L);
        testEntity.setUserId(3L);
        testEntity.setNoteId(4L);
        testEntity.setCreatedAt(testTime);
        testEntity.setUpdatedAt(testTime);
        testEntity.setText("Test note text");
    }

    @Test
    void toPayload_WithValidEntity_ShouldMapCorrectly() {
        ExerciseNoteResponse result = exerciseNoteMapper.toPayload(testEntity);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.exerciseId()).isNull(); // No exercise relationship set
        assertThat(result.userId()).isEqualTo(3L);
        assertThat(result.noteId()).isEqualTo(4L);
        assertThat(result.dayExerciseId()).isNull(); // No dayExercise relationship set
        assertThat(result.createdAt()).isEqualTo(testTime);
        assertThat(result.updatedAt()).isEqualTo(testTime);
        assertThat(result.text()).isEqualTo("Test note text");
    }

    @Test
    void toPayload_WithNullEntity_ShouldReturnNull() {
        assertThat(exerciseNoteMapper.toPayload(null)).isNull();
    }

    @Test
    void toPayloadList_WithValidList_ShouldMapAll() {
        List<ExerciseNote> entities = Arrays.asList(testEntity, testEntity);
        List<ExerciseNoteResponse> result = exerciseNoteMapper.toPayloadList(entities);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(1).id()).isEqualTo(1L);
    }

    @Test
    void toPayloadList_WithNullList_ShouldReturnNull() {
        assertThat(exerciseNoteMapper.toPayloadList(null)).isNull();
    }

    @Test
    void toPayloadList_WithEmptyList_ShouldReturnEmptyList() {
        assertThat(exerciseNoteMapper.toPayloadList(Collections.emptyList())).isEmpty();
    }

    @Test
    void toPayloadList_WithMixedNullEntities_ShouldHandleGracefully() {
        List<ExerciseNote> entitiesWithNull = Arrays.asList(testEntity, null, testEntity);
        List<ExerciseNoteResponse> result = exerciseNoteMapper.toPayloadList(entitiesWithNull);

        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(1)).isNull();
        assertThat(result.get(2)).isNotNull();
    }
}
