package com.noslen.training_tracker.mapper.progression;

import com.noslen.training_tracker.dto.progression.response.MuscleGroupResponse;
import com.noslen.training_tracker.model.progression.MuscleGroup;
import com.noslen.training_tracker.enums.MgName;
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
class MuscleGroupMapperTest {

    @InjectMocks
    private MuscleGroupMapper muscleGroupMapper;

    private MuscleGroup testEntity;
    private Instant testTime;

    @BeforeEach
    void setUp() {
        testTime = Instant.now();

        testEntity = new MuscleGroup();
        testEntity.setId(1L);
        testEntity.setName(MgName.CHEST);
        testEntity.setCreatedAt(testTime);
        testEntity.setUpdatedAt(testTime);
    }

    @Test
    void toPayload_WithValidEntity_ShouldMapCorrectly() {
        MuscleGroupResponse result = muscleGroupMapper.toPayload(testEntity);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Chest");
        assertThat(result.createdAt()).isEqualTo(testTime);
        assertThat(result.updatedAt()).isEqualTo(testTime);
    }

    @Test
    void toPayload_WithNullEntity_ShouldReturnNull() {
        assertThat(muscleGroupMapper.toPayload(null)).isNull();
    }

    @Test
    void toPayloadList_WithValidList_ShouldMapAll() {
        List<MuscleGroup> entities = Arrays.asList(testEntity, testEntity);
        List<MuscleGroupResponse> result = muscleGroupMapper.toPayloadList(entities);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
    }

    @Test
    void toPayloadList_WithNullList_ShouldReturnNull() {
        assertThat(muscleGroupMapper.toPayloadList(null)).isNull();
    }

    @Test
    void toPayloadList_WithEmptyList_ShouldReturnEmptyList() {
        assertThat(muscleGroupMapper.toPayloadList(Collections.emptyList())).isEmpty();
    }
}
