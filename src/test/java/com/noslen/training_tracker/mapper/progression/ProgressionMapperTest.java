package com.noslen.training_tracker.mapper.progression;

import com.noslen.training_tracker.dto.progression.response.ProgressionResponse;
import com.noslen.training_tracker.model.progression.MuscleGroup;
import com.noslen.training_tracker.model.progression.Progression;
import com.noslen.training_tracker.enums.MgProgressionType;
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

    private Progression testEntity;

    @BeforeEach
    void setUp() {
        MuscleGroup testMuscleGroup = new MuscleGroup();
        testMuscleGroup.setId(2L);

        testEntity = new Progression(
                1L, testMuscleGroup, MgProgressionType.REGULAR, null
        );
    }

    @Test
    void toPayload_WithValidEntity_ShouldMapCorrectly() {
        ProgressionResponse result = progressionMapper.toPayload(testEntity);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.muscleGroupId()).isEqualTo(2L);
        assertThat(result.mgProgressionType()).isEqualTo(MgProgressionType.REGULAR);
    }

    @Test
    void toPayload_WithNullEntity_ShouldReturnNull() {
        assertThat(progressionMapper.toPayload(null)).isNull();
    }

    @Test
    void toPayloadList_WithValidList_ShouldMapAll() {
        List<Progression> entities = Arrays.asList(testEntity, testEntity);
        List<ProgressionResponse> result = progressionMapper.toPayloadList(entities);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
    }

    @Test
    void toPayloadList_WithNullList_ShouldReturnNull() {
        assertThat(progressionMapper.toPayloadList(null)).isNull();
    }

    @Test
    void toPayloadList_WithEmptyList_ShouldReturnEmptyList() {
        assertThat(progressionMapper.toPayloadList(Collections.emptyList())).isEmpty();
    }
}
