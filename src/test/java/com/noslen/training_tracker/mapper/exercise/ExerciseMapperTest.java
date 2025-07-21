package com.noslen.training_tracker.mapper.exercise;

import com.noslen.training_tracker.dto.exercise.ExercisePayload;
import com.noslen.training_tracker.dto.exercise.ExerciseNotePayload;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExerciseMapperTest {

    @Mock
    private ExerciseNoteMapper exerciseNoteMapper;

    @InjectMocks
    private ExerciseMapper exerciseMapper;

    private ExercisePayload testPayload;
    private Exercise testEntity;
    private Instant testTime;
    private ExerciseNotePayload testNotePayload;
    private ExerciseNote testNoteEntity;

    @BeforeEach
    void setUp() {
        testTime = Instant.now();
        
        testNotePayload = new ExerciseNotePayload(
                1L, 2L, 3L, 4L, 5L, testTime, testTime, "Test note"
        );
        
        testNoteEntity = ExerciseNote.builder()
                .id(1L)
                .userId(3L)
                .noteId(4L)
                .createdAt(testTime)
                .updatedAt(testTime)
                .text("Test note")
                .build();

        testPayload = new ExercisePayload(
                1L, "Test Exercise", 2L, "youtube123", "strength", 3L,
                testTime, testTime, null, "primary", Arrays.asList(testNotePayload)
        );

        testEntity = Exercise.builder()
                .id(1L)
                .name("Test Exercise")
                .muscleGroupId(2L)
                .youtubeId("youtube123")
                .exerciseType("strength")
                .userId(3L)
                .createdAt(testTime)
                .updatedAt(testTime)
                .deletedAt(null)
                .mgSubType("primary")
                .notes(Arrays.asList(testNoteEntity))
                .build();
    }

    @Test
    void toEntity_WithValidPayload_ShouldMapCorrectly() {
        // Given
        when(exerciseNoteMapper.toEntity(any(ExerciseNotePayload.class))).thenReturn(testNoteEntity);

        // When
        Exercise result = exerciseMapper.toEntity(testPayload);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Exercise");
        assertThat(result.getMuscleGroupId()).isEqualTo(2L);
        assertThat(result.getYoutubeId()).isEqualTo("youtube123");
        assertThat(result.getExerciseType()).isEqualTo("strength");
        assertThat(result.getUserId()).isEqualTo(3L);
        assertThat(result.getCreatedAt()).isEqualTo(testTime);
        assertThat(result.getUpdatedAt()).isEqualTo(testTime);
        assertThat(result.getDeletedAt()).isNull();
        assertThat(result.getMgSubType()).isEqualTo("primary");
        assertThat(result.getNotes()).hasSize(1);
    }

    @Test
    void toEntity_WithNullPayload_ShouldReturnNull() {
        // When
        Exercise result = exerciseMapper.toEntity(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toEntity_WithNullNotes_ShouldHandleGracefully() {
        // Given
        ExercisePayload payloadWithoutNotes = new ExercisePayload(
                1L, "Test Exercise", 2L, "youtube123", "strength", 3L,
                testTime, testTime, null, "primary", null
        );

        // When
        Exercise result = exerciseMapper.toEntity(payloadWithoutNotes);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNotes()).isNull();
    }

    @Test
    void toPayload_WithValidEntity_ShouldMapCorrectly() {
        // Given
        when(exerciseNoteMapper.toPayloadList(anyList())).thenReturn(Arrays.asList(testNotePayload));

        // When
        ExercisePayload result = exerciseMapper.toPayload(testEntity);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Test Exercise");
        assertThat(result.muscleGroupId()).isEqualTo(2L);
        assertThat(result.youtubeId()).isEqualTo("youtube123");
        assertThat(result.exerciseType()).isEqualTo("strength");
        assertThat(result.userId()).isEqualTo(3L);
        assertThat(result.createdAt()).isEqualTo(testTime);
        assertThat(result.updatedAt()).isEqualTo(testTime);
        assertThat(result.deletedAt()).isNull();
        assertThat(result.mgSubType()).isEqualTo("primary");
        assertThat(result.notes()).hasSize(1);
    }

    @Test
    void toPayload_WithNullEntity_ShouldReturnNull() {
        // When
        ExercisePayload result = exerciseMapper.toPayload(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void updateEntity_WithValidData_ShouldUpdateMutableFields() {
        // Given
        Exercise existing = Exercise.builder()
                .id(1L)
                .name("Original Exercise")
                .muscleGroupId(2L)
                .youtubeId("original123")
                .exerciseType("cardio")
                .userId(3L)
                .createdAt(testTime)
                .updatedAt(testTime)
                .mgSubType("secondary")
                .build();

        ExercisePayload updatePayload = new ExercisePayload(
                0L, "Updated Exercise", 4L, "updated456", "strength", 5L,
                null, testTime.plusSeconds(60), testTime.plusSeconds(120), "primary", null
        );

        // When
        exerciseMapper.updateEntity(existing, updatePayload);

        // Then
        assertThat(existing.getName()).isEqualTo("Updated Exercise");
        assertThat(existing.getMuscleGroupId()).isEqualTo(4L);
        assertThat(existing.getYoutubeId()).isEqualTo("updated456");
        assertThat(existing.getExerciseType()).isEqualTo("strength");
        assertThat(existing.getUserId()).isEqualTo(5L);
        assertThat(existing.getUpdatedAt()).isEqualTo(testTime.plusSeconds(60));
        assertThat(existing.getDeletedAt()).isEqualTo(testTime.plusSeconds(120));
        assertThat(existing.getMgSubType()).isEqualTo("primary");
        // ID and createdAt should remain unchanged
        assertThat(existing.getId()).isEqualTo(1L);
        assertThat(existing.getCreatedAt()).isEqualTo(testTime);
    }

    @Test
    void updateEntity_WithNullInputs_ShouldHandleGracefully() {
        // When/Then - should not throw exceptions
        exerciseMapper.updateEntity(null, testPayload);
        exerciseMapper.updateEntity(testEntity, null);
        exerciseMapper.updateEntity(null, null);
    }

    @Test
    void updateEntity_WithZeroValues_ShouldNotUpdate() {
        // Given
        Exercise existing = Exercise.builder()
                .id(1L)
                .name("Original Exercise")
                .muscleGroupId(2L)
                .userId(3L)
                .createdAt(testTime)
                .updatedAt(testTime)
                .build();

        ExercisePayload updatePayload = new ExercisePayload(
                0L, null, 0L, null, null, 0L,
                null, null, null, null, null
        );

        // When
        exerciseMapper.updateEntity(existing, updatePayload);

        // Then - nothing should change
        assertThat(existing.getName()).isEqualTo("Original Exercise");
        assertThat(existing.getMuscleGroupId()).isEqualTo(2L);
        assertThat(existing.getUserId()).isEqualTo(3L);
    }

    @Test
    void mergeEntity_WithValidData_ShouldCreateNewEntityWithUpdatedFields() {
        // Given
        Exercise existing = Exercise.builder()
                .id(1L)
                .name("Original Exercise")
                .muscleGroupId(2L)
                .youtubeId("original123")
                .exerciseType("cardio")
                .userId(3L)
                .createdAt(testTime)
                .updatedAt(testTime)
                .mgSubType("secondary")
                .build();

        ExercisePayload updatePayload = new ExercisePayload(
                0L, "Updated Exercise", 4L, "updated456", "strength", 5L,
                null, testTime.plusSeconds(60), null, "primary", null
        );

        // When
        Exercise result = exerciseMapper.mergeEntity(existing, updatePayload);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L); // preserved from existing
        assertThat(result.getName()).isEqualTo("Updated Exercise"); // updated from payload
        assertThat(result.getMuscleGroupId()).isEqualTo(4L); // updated from payload
        assertThat(result.getYoutubeId()).isEqualTo("updated456"); // updated from payload
        assertThat(result.getExerciseType()).isEqualTo("strength"); // updated from payload
        assertThat(result.getUserId()).isEqualTo(5L); // updated from payload
        assertThat(result.getCreatedAt()).isEqualTo(testTime); // preserved from existing
        assertThat(result.getUpdatedAt()).isEqualTo(testTime.plusSeconds(60)); // updated from payload
        assertThat(result.getMgSubType()).isEqualTo("primary"); // updated from payload
    }

    @Test
    void mergeEntity_WithZeroValues_ShouldKeepExistingValues() {
        // Given
        Exercise existing = Exercise.builder()
                .id(1L)
                .name("Original Exercise")
                .muscleGroupId(2L)
                .userId(3L)
                .createdAt(testTime)
                .updatedAt(testTime)
                .build();

        ExercisePayload updatePayload = new ExercisePayload(
                0L, null, 0L, null, null, 0L,
                null, null, null, null, null
        );

        // When
        Exercise result = exerciseMapper.mergeEntity(existing, updatePayload);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L); // preserved
        assertThat(result.getName()).isEqualTo("Original Exercise"); // preserved (null means keep existing)
        assertThat(result.getMuscleGroupId()).isEqualTo(2L); // preserved (0 means keep existing)
        assertThat(result.getUserId()).isEqualTo(3L); // preserved (0 means keep existing)
        assertThat(result.getCreatedAt()).isEqualTo(testTime); // preserved
        assertThat(result.getUpdatedAt()).isEqualTo(testTime); // preserved (null means keep existing)
    }

    @Test
    void mergeEntity_WithNullExisting_ShouldReturnNewEntity() {
        // Given
        when(exerciseNoteMapper.toEntity(any())).thenReturn(testNoteEntity);

        // When
        Exercise result = exerciseMapper.mergeEntity(null, testPayload);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testPayload.id());
        assertThat(result.getName()).isEqualTo(testPayload.name());
        assertThat(result.getMuscleGroupId()).isEqualTo(testPayload.muscleGroupId());
    }

    @Test
    void mergeEntity_WithNullPayload_ShouldReturnExisting() {
        // When
        Exercise result = exerciseMapper.mergeEntity(testEntity, null);

        // Then
        assertThat(result).isSameAs(testEntity);
    }

    @Test
    void toPayloadList_WithValidList_ShouldMapAll() {
        // Given
        List<Exercise> entities = Arrays.asList(testEntity, testEntity);
        when(exerciseNoteMapper.toPayloadList(anyList())).thenReturn(Arrays.asList(testNotePayload));

        // When
        List<ExercisePayload> result = exerciseMapper.toPayloadList(entities);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(1).id()).isEqualTo(1L);
    }

    @Test
    void toPayloadList_WithNullList_ShouldReturnNull() {
        // When
        List<ExercisePayload> result = exerciseMapper.toPayloadList(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toPayloadList_WithEmptyList_ShouldReturnEmptyList() {
        // When
        List<ExercisePayload> result = exerciseMapper.toPayloadList(Collections.emptyList());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void toPayloadList_WithMixedNullEntities_ShouldHandleGracefully() {
        // Given
        List<Exercise> entitiesWithNull = Arrays.asList(testEntity, null, testEntity);
        when(exerciseNoteMapper.toPayloadList(anyList())).thenReturn(Arrays.asList(testNotePayload));

        // When
        List<ExercisePayload> result = exerciseMapper.toPayloadList(entitiesWithNull);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(1)).isNull();
        assertThat(result.get(2)).isNotNull();
    }
}
