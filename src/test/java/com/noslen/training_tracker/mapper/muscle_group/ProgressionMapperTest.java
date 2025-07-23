package com.noslen.training_tracker.mapper.muscle_group;

import com.noslen.training_tracker.dto.muscle_group.ProgressionPayload;
import com.noslen.training_tracker.model.muscle_group.Progression;
import com.noslen.training_tracker.model.muscle_group.types.MgProgressionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProgressionMapperTest {

    @InjectMocks
    private ProgressionMapper progressionMapper;

    private ProgressionPayload testPayload;
    private Progression testEntity;

    @BeforeEach
    void setUp() {
        testPayload = new ProgressionPayload(
                1L, 2L, MgProgressionType.REGULAR
        );

        testEntity = new Progression(
                1L, 2L, MgProgressionType.REGULAR, null
        );
    }

    @Test
    void toEntity_WithValidPayload_ShouldMapCorrectly() {
        // When
        Progression result = progressionMapper.toEntity(testPayload);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getMuscleGroupId()).isEqualTo(2L);
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
        Progression existing = new Progression(
                1L, 2L, MgProgressionType.REGULAR, null
        );

        ProgressionPayload updatePayload = new ProgressionPayload(
                0L, 0L, MgProgressionType.SECONDARY
                // Only this should be updated
        );

        // When
        progressionMapper.updateEntity(existing, updatePayload);

        // Then
        assertThat(existing.getMgProgressionType()).isEqualTo(MgProgressionType.SECONDARY);
        // Other fields should remain unchanged since they don't have setters
        assertThat(existing.getId()).isEqualTo(1L);
        assertThat(existing.getMuscleGroupId()).isEqualTo(2L);
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
