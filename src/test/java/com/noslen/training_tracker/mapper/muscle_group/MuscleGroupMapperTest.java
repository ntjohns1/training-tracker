package com.noslen.training_tracker.mapper.muscle_group;

import com.noslen.training_tracker.dto.muscle_group.MuscleGroupPayload;
import com.noslen.training_tracker.model.muscle_group.MuscleGroup;
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

    private MuscleGroupPayload testPayload;
    private MuscleGroup testEntity;
    private Instant testTime;

    @BeforeEach
    void setUp() {
        testTime = Instant.now();
        
        testPayload = new MuscleGroupPayload(
                1L, "Chest", testTime, testTime
        );

        testEntity = new MuscleGroup();
        testEntity.setId(1L);
        testEntity.setName(MgName.CHEST);
        testEntity.setCreatedAt(testTime);
        testEntity.setUpdatedAt(testTime);
    }

    @Test
    void toEntity_WithValidPayload_ShouldMapCorrectly() {
        // When
        MuscleGroup result = muscleGroupMapper.toEntity(testPayload);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo(MgName.CHEST);
        assertThat(result.getCreatedAt()).isEqualTo(testTime);
        assertThat(result.getUpdatedAt()).isEqualTo(testTime);
    }

    @Test
    void toEntity_WithNullPayload_ShouldReturnNull() {
        // When
        MuscleGroup result = muscleGroupMapper.toEntity(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toEntity_WithNullCreatedAt_ShouldSetCurrentTime() {
        // Given
        MuscleGroupPayload payloadWithNullCreatedAt = new MuscleGroupPayload(
                1L, "Chest", null, testTime
        );

        // When
        MuscleGroup result = muscleGroupMapper.toEntity(payloadWithNullCreatedAt);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getCreatedAt()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    void toEntity_WithNullUpdatedAt_ShouldSetCurrentTime() {
        // Given
        MuscleGroupPayload payloadWithNullUpdatedAt = new MuscleGroupPayload(
                1L, "Chest", testTime, null
        );

        // When
        MuscleGroup result = muscleGroupMapper.toEntity(payloadWithNullUpdatedAt);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    void toPayload_WithValidEntity_ShouldMapCorrectly() {
        // When
        MuscleGroupPayload result = muscleGroupMapper.toPayload(testEntity);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Chest");
        assertThat(result.createdAt()).isEqualTo(testTime);
        assertThat(result.updatedAt()).isEqualTo(testTime);
    }

    @Test
    void toPayload_WithNullEntity_ShouldReturnNull() {
        // When
        MuscleGroupPayload result = muscleGroupMapper.toPayload(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void updateEntity_WithValidData_ShouldUpdateFields() {
        // Given
        MuscleGroup existing = new MuscleGroup();
        existing.setId(1L);
        existing.setName(MgName.CHEST);
        existing.setCreatedAt(testTime);
        existing.setUpdatedAt(testTime);

        MuscleGroupPayload updatePayload = new MuscleGroupPayload(
                null, "Back", null, null
        );

        // When
        muscleGroupMapper.updateEntity(existing, updatePayload);

        // Then
        assertThat(existing.getName()).isEqualTo(MgName.BACK);
        assertThat(existing.getUpdatedAt()).isAfter(testTime);
        assertThat(existing.getId()).isEqualTo(1L); // Should remain unchanged
        assertThat(existing.getCreatedAt()).isEqualTo(testTime); // Should remain unchanged
    }

    @Test
    void updateEntity_WithNullInputs_ShouldHandleGracefully() {
        // When/Then - should not throw exceptions
        muscleGroupMapper.updateEntity(null, testPayload);
        muscleGroupMapper.updateEntity(testEntity, null);
        muscleGroupMapper.updateEntity(null, null);
    }

    @Test
    void updateEntity_WithPartialData_ShouldUpdateOnlyProvidedFields() {
        // Given
        MuscleGroup existing = new MuscleGroup();
        existing.setId(1L);
        existing.setName(MgName.CHEST);
        existing.setCreatedAt(testTime);
        existing.setUpdatedAt(testTime);

        MuscleGroupPayload partialPayload = new MuscleGroupPayload(
                null, null, testTime.plusSeconds(3600), null
        );

        // When
        muscleGroupMapper.updateEntity(existing, partialPayload);

        // Then
        assertThat(existing.getName()).isEqualTo(MgName.CHEST); // unchanged (null in payload)
        assertThat(existing.getCreatedAt()).isEqualTo(testTime.plusSeconds(3600)); // updated
        assertThat(existing.getUpdatedAt()).isAfter(testTime); // always updated
    }

    @Test
    void mergeEntity_WithValidData_ShouldCreateNewEntityWithUpdatedFields() {
        // Given
        MuscleGroup existing = new MuscleGroup();
        existing.setId(1L);
        existing.setName(MgName.CHEST);
        existing.setCreatedAt(testTime);
        existing.setUpdatedAt(testTime);

        MuscleGroupPayload updatePayload = new MuscleGroupPayload(
                null, "Back", null, null
        );

        // When
        MuscleGroup result = muscleGroupMapper.mergeEntity(existing, updatePayload);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L); // preserved
        assertThat(result.getCreatedAt()).isEqualTo(testTime); // preserved
        assertThat(result.getUpdatedAt()).isAfter(testTime); // updated
        assertThat(result.getName()).isEqualTo(MgName.BACK); // updated
    }

    @Test
    void mergeEntity_WithNullExisting_ShouldReturnNewEntity() {
        // When
        MuscleGroup result = muscleGroupMapper.mergeEntity(null, testPayload);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testPayload.id());
        assertThat(result.getName()).isEqualTo(MgName.CHEST);
    }

    @Test
    void mergeEntity_WithNullPayload_ShouldReturnExisting() {
        // When
        MuscleGroup result = muscleGroupMapper.mergeEntity(testEntity, null);

        // Then
        assertThat(result).isSameAs(testEntity);
    }

    @Test
    void toPayloadList_WithValidList_ShouldMapAll() {
        // Given
        List<MuscleGroup> entities = Arrays.asList(testEntity, testEntity);

        // When
        List<MuscleGroupPayload> result = muscleGroupMapper.toPayloadList(entities);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(1).id()).isEqualTo(1L);
    }

    @Test
    void toPayloadList_WithNullList_ShouldReturnNull() {
        // When
        List<MuscleGroupPayload> result = muscleGroupMapper.toPayloadList(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void toPayloadList_WithEmptyList_ShouldReturnEmptyList() {
        // When
        List<MuscleGroupPayload> result = muscleGroupMapper.toPayloadList(Collections.emptyList());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void toPayloadList_WithMixedNullEntities_ShouldHandleGracefully() {
        // Given
        List<MuscleGroup> entitiesWithNull = Arrays.asList(testEntity, null, testEntity);

        // When
        List<MuscleGroupPayload> result = muscleGroupMapper.toPayloadList(entitiesWithNull);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(1)).isNull();
        assertThat(result.get(2)).isNotNull();
    }
}
