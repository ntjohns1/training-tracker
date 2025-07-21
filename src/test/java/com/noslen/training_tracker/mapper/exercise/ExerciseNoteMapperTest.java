package com.noslen.training_tracker.mapper.exercise;

import com.noslen.training_tracker.dto.exercise.ExerciseNotePayload;
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

    private ExerciseNotePayload testPayload;
    private ExerciseNote testEntity;
    private Instant testTime;

    @BeforeEach
    void setUp() {
        testTime = Instant.now();
        
        testPayload = new ExerciseNotePayload(
                1L, 2L, 3L, 4L, 5L, testTime, testTime, "Test note text"
        );

        testEntity = ExerciseNote.builder()
                .id(1L)
                .userId(3L)
                .noteId(4L)
                .createdAt(testTime)
                .updatedAt(testTime)
                .text("Test note text")
                .build();
    }

    @Test
    void toEntity_WithValidPayload_ShouldMapCorrectly() {
        // When
        ExerciseNote result = exerciseNoteMapper.toEntity(testPayload);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(3L);
        assertThat(result.getNoteId()).isEqualTo(4L);
        assertThat(result.getCreatedAt()).isEqualTo(testTime);
        assertThat(result.getUpdatedAt()).isEqualTo(testTime);
        assertThat(result.getText()).isEqualTo("Test note text");
        // Note: exercise and dayExercise relationships are not set in mapper
        assertThat(result.getExercise()).isNull();
        assertThat(result.getDayExercise()).isNull();
    }

    @Test
    void toEntity_WithNullPayload_ShouldReturnNull() {
        // When
        ExerciseNote result = exerciseNoteMapper.toEntity(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toPayload_WithValidEntity_ShouldMapCorrectly() {
        // When
        ExerciseNotePayload result = exerciseNoteMapper.toPayload(testEntity);

        // Then
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
        // When
        ExerciseNotePayload result = exerciseNoteMapper.toPayload(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void updateEntity_WithValidData_ShouldUpdateMutableFields() {
        // Given
        ExerciseNote existing = ExerciseNote.builder()
                .id(1L)
                .userId(3L)
                .noteId(4L)
                .createdAt(testTime)
                .updatedAt(testTime)
                .text("Original text")
                .build();

        ExerciseNotePayload updatePayload = new ExerciseNotePayload(
                0L, 0L, 0L, 0L, 0L, null, testTime.plusSeconds(60), "Updated text"
        );

        // When
        exerciseNoteMapper.updateEntity(existing, updatePayload);

        // Then
        assertThat(existing.getText()).isEqualTo("Updated text");
        assertThat(existing.getUpdatedAt()).isEqualTo(testTime.plusSeconds(60));
        // Other fields should remain unchanged
        assertThat(existing.getId()).isEqualTo(1L);
        assertThat(existing.getUserId()).isEqualTo(3L);
        assertThat(existing.getNoteId()).isEqualTo(4L);
        assertThat(existing.getCreatedAt()).isEqualTo(testTime);
    }

    @Test
    void updateEntity_WithNullInputs_ShouldHandleGracefully() {
        // When/Then - should not throw exceptions
        exerciseNoteMapper.updateEntity(null, testPayload);
        exerciseNoteMapper.updateEntity(testEntity, null);
        exerciseNoteMapper.updateEntity(null, null);
    }

    @Test
    void updateEntity_WithNullValues_ShouldNotUpdate() {
        // Given
        ExerciseNote existing = ExerciseNote.builder()
                .id(1L)
                .userId(3L)
                .noteId(4L)
                .createdAt(testTime)
                .updatedAt(testTime)
                .text("Original text")
                .build();

        ExerciseNotePayload updatePayload = new ExerciseNotePayload(
                0L, 0L, 0L, 0L, 0L, null, null, null
        );

        // When
        exerciseNoteMapper.updateEntity(existing, updatePayload);

        // Then - nothing should change
        assertThat(existing.getText()).isEqualTo("Original text");
        assertThat(existing.getUpdatedAt()).isEqualTo(testTime);
    }

    @Test
    void mergeEntity_WithValidData_ShouldCreateNewEntityWithUpdatedFields() {
        // Given
        ExerciseNote existing = ExerciseNote.builder()
                .id(1L)
                .userId(3L)
                .noteId(4L)
                .createdAt(testTime)
                .updatedAt(testTime)
                .text("Original text")
                .build();

        ExerciseNotePayload updatePayload = new ExerciseNotePayload(
                0L, 0L, 5L, 6L, 0L, null, testTime.plusSeconds(60), "Updated text"
        );

        // When
        ExerciseNote result = exerciseNoteMapper.mergeEntity(existing, updatePayload);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L); // preserved from existing
        assertThat(result.getUserId()).isEqualTo(5L); // updated from payload
        assertThat(result.getNoteId()).isEqualTo(6L); // updated from payload
        assertThat(result.getCreatedAt()).isEqualTo(testTime); // preserved from existing
        assertThat(result.getUpdatedAt()).isEqualTo(testTime.plusSeconds(60)); // updated from payload
        assertThat(result.getText()).isEqualTo("Updated text"); // updated from payload
    }

    @Test
    void mergeEntity_WithZeroValues_ShouldKeepExistingValues() {
        // Given
        ExerciseNote existing = ExerciseNote.builder()
                .id(1L)
                .userId(3L)
                .noteId(4L)
                .createdAt(testTime)
                .updatedAt(testTime)
                .text("Original text")
                .build();

        ExerciseNotePayload updatePayload = new ExerciseNotePayload(
                0L, 0L, 0L, 0L, 0L, null, null, null
        );

        // When
        ExerciseNote result = exerciseNoteMapper.mergeEntity(existing, updatePayload);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L); // preserved
        assertThat(result.getUserId()).isEqualTo(3L); // preserved (0 means keep existing)
        assertThat(result.getNoteId()).isEqualTo(4L); // preserved (0 means keep existing)
        assertThat(result.getCreatedAt()).isEqualTo(testTime); // preserved
        assertThat(result.getUpdatedAt()).isEqualTo(testTime); // preserved (null means keep existing)
        assertThat(result.getText()).isEqualTo("Original text"); // preserved (null means keep existing)
    }

    @Test
    void mergeEntity_WithNullExisting_ShouldReturnNewEntity() {
        // When
        ExerciseNote result = exerciseNoteMapper.mergeEntity(null, testPayload);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testPayload.id());
        assertThat(result.getUserId()).isEqualTo(testPayload.userId());
        assertThat(result.getNoteId()).isEqualTo(testPayload.noteId());
        assertThat(result.getText()).isEqualTo(testPayload.text());
    }

    @Test
    void mergeEntity_WithNullPayload_ShouldReturnExisting() {
        // When
        ExerciseNote result = exerciseNoteMapper.mergeEntity(testEntity, null);

        // Then
        assertThat(result).isSameAs(testEntity);
    }

    @Test
    void toPayloadList_WithValidList_ShouldMapAll() {
        // Given
        List<ExerciseNote> entities = Arrays.asList(testEntity, testEntity);

        // When
        List<ExerciseNotePayload> result = exerciseNoteMapper.toPayloadList(entities);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(1).id()).isEqualTo(1L);
    }

    @Test
    void toPayloadList_WithNullList_ShouldReturnNull() {
        // When
        List<ExerciseNotePayload> result = exerciseNoteMapper.toPayloadList(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toPayloadList_WithEmptyList_ShouldReturnEmptyList() {
        // When
        List<ExerciseNotePayload> result = exerciseNoteMapper.toPayloadList(Collections.emptyList());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void toPayloadList_WithMixedNullEntities_ShouldHandleGracefully() {
        // Given
        List<ExerciseNote> entitiesWithNull = Arrays.asList(testEntity, null, testEntity);

        // When
        List<ExerciseNotePayload> result = exerciseNoteMapper.toPayloadList(entitiesWithNull);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(1)).isNull();
        assertThat(result.get(2)).isNotNull();
    }
}
