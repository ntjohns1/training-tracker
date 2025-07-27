package com.noslen.training_tracker.mapper.muscle_group;

import com.noslen.training_tracker.dto.muscle_group.ProgressionPayload;
import com.noslen.training_tracker.model.muscle_group.MuscleGroup;
import com.noslen.training_tracker.model.muscle_group.Progression;
import com.noslen.training_tracker.repository.muscle_group.MuscleGroupRepo;
import com.noslen.training_tracker.enums.MgProgressionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class ProgressionMapperTest {

    @Mock
    private MuscleGroupRepo muscleGroupRepo;

    @InjectMocks
    private ProgressionMapper progressionMapper;

    private ProgressionPayload testPayload;
    private Progression testEntity;
    private MuscleGroup testMuscleGroup;

    @BeforeEach
    void setUp() {
        testPayload = new ProgressionPayload(
                1L, 2L, MgProgressionType.REGULAR
        );

        testMuscleGroup = new MuscleGroup();
        testMuscleGroup.setId(2L);

        testEntity = new Progression(
                1L, testMuscleGroup, MgProgressionType.REGULAR, null
        );
    }

    @Test
    void toEntity_WithValidPayload_ShouldMapCorrectly() {
        // When
        Progression result = progressionMapper.toEntity(testPayload);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getMuscleGroup()).isNull(); // Mapper no longer resolves MuscleGroup
        assertThat(result.getMgProgressionType()).isEqualTo(MgProgressionType.REGULAR);
        assertThat(result.getMesocycle()).isNull();
    }

    @Test
    void toEntity_WithNullPayload_ShouldReturnNull() {
        // When
        Progression result = progressionMapper.toEntity(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toPayload_WithValidEntity_ShouldMapCorrectly() {
        // When
        ProgressionPayload result = progressionMapper.toPayload(testEntity);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.muscleGroupId()).isEqualTo(2L);
        assertThat(result.mgProgressionType()).isEqualTo(MgProgressionType.REGULAR);
    }

    @Test
    void toPayload_WithNullEntity_ShouldReturnNull() {
        // When
        ProgressionPayload result = progressionMapper.toPayload(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void updateEntity_WithValidData_ShouldUpdateMutableField() {
        // Given
        Progression existingEntity = new Progression(
                1L, testMuscleGroup, MgProgressionType.REGULAR, null
        );
        ProgressionPayload updatePayload = new ProgressionPayload(
                1L, 2L, MgProgressionType.SECONDARY
        );
        // No mocking needed since mapper doesn't use repository

        // When
        Progression result = progressionMapper.updateEntity(existingEntity, updatePayload);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getMuscleGroup()).isEqualTo(testMuscleGroup); // Keeps existing MuscleGroup
        assertThat(result.getMgProgressionType()).isEqualTo(MgProgressionType.SECONDARY);
    }

    @Test
    void updateEntity_WithNullInputs_ShouldHandleGracefully() {
        // When/Then - should not throw exceptions
        progressionMapper.updateEntity(null, testPayload);
        progressionMapper.updateEntity(testEntity, null);
        progressionMapper.updateEntity(null, null);
    }

    @Test
    void toPayloadList_WithValidList_ShouldMapAll() {
        // Given
        List<Progression> entities = Arrays.asList(testEntity, testEntity);

        // When
        List<ProgressionPayload> result = progressionMapper.toPayloadList(entities);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(1).id()).isEqualTo(1L);
    }

    @Test
    void toPayloadList_WithNullList_ShouldReturnNull() {
        // When
        List<ProgressionPayload> result = progressionMapper.toPayloadList(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toPayloadList_WithEmptyList_ShouldReturnEmptyList() {
        // When
        List<ProgressionPayload> result = progressionMapper.toPayloadList(Collections.emptyList());

        // Then
        assertThat(result).isEmpty();
    }
}
