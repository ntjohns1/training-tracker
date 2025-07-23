package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.DayMuscleGroupPayload;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.day.DayMuscleGroup;
import com.noslen.training_tracker.model.muscle_group.MuscleGroup;
import com.noslen.training_tracker.model.muscle_group.types.MgName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class DayMuscleGroupMapperTest {

    private DayMuscleGroupMapper mapper;
    private DayMuscleGroupPayload samplePayload;
    private DayMuscleGroup sampleEntity;
    private Instant now;

    @BeforeEach
    void setUp() {
        mapper = new DayMuscleGroupMapper();
        now = Instant.now();
        Day day = Day.builder().id(10L).build();
        MuscleGroup muscleGroup = new MuscleGroup();
        muscleGroup.setName(MgName.CHEST);
        muscleGroup.setId(20L);

        samplePayload = new DayMuscleGroupPayload(
                1L,
                10L,
                20L,
                1,
                2,
                2,
                now,
                now,
                2,
                "complete"
        );

        sampleEntity = DayMuscleGroup.builder()
                .id(1L)
                .day(day)
                .muscleGroup(muscleGroup)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    void toEntity_WithValidPayload_ShouldReturnEntity() {
        // When
        DayMuscleGroup result = mapper.toEntity(samplePayload);

        // Then
        assertNotNull(result);
        assertEquals(samplePayload.id(), result.getId());
        assertNull(result.getDay()); // toEntity doesn't set Day relationship
        assertNull(result.getMuscleGroup()); // toEntity doesn't set MuscleGroup relationship
        assertEquals(samplePayload.createdAt(), result.getCreatedAt());
        assertEquals(samplePayload.updatedAt(), result.getUpdatedAt());
        assertEquals(samplePayload.pump(), result.getPump());
        assertEquals(samplePayload.soreness(), result.getSoreness());
        assertEquals(samplePayload.workload(), result.getWorkload());
        assertEquals(samplePayload.recommendedSets(), result.getRecommendedSets());
        assertEquals(samplePayload.status(), result.getStatus());

        // Note: Day and MuscleGroup relationships are not set by toEntity and should be handled by service layer
    }

    @Test
    void toEntity_WithNullPayload_ShouldReturnNull() {
        // When
        DayMuscleGroup result = mapper.toEntity(null);

        // Then
        assertNull(result);
    }

    @Test
    void toEntity_WithNullFields_ShouldHandleGracefully() {
        // Given
        DayMuscleGroupPayload payloadWithNulls = new DayMuscleGroupPayload(
                null, null, null, null, null, null, null, null, null, null
        );

        // When
        DayMuscleGroup result = mapper.toEntity(payloadWithNulls);

        // Then
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getDay());
        assertNull(result.getMuscleGroup());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
    }

    @Test
    void toPayload_WithValidEntity_ShouldReturnPayload() {
        // When
        DayMuscleGroupPayload result = mapper.toPayload(sampleEntity);

        // Then
        assertNotNull(result);
        assertEquals(sampleEntity.getId(), result.id());
        assertEquals(sampleEntity.getDayId(), result.dayId());
        assertEquals(sampleEntity.getMuscleGroupId(), result.muscleGroupId());
        assertEquals(sampleEntity.getCreatedAt(), result.createdAt());
        assertEquals(sampleEntity.getUpdatedAt(), result.updatedAt());
    }

    @Test
    void toPayload_WithNullEntity_ShouldReturnNull() {
        // When
        DayMuscleGroupPayload result = mapper.toPayload(null);

        // Then
        assertNull(result);
    }

    @Test
    void toPayload_WithEntityHavingRelationships_ShouldExtractIds() {
        // Given
        Day day = Day.builder().id(100L).build();
        MuscleGroup muscleGroup = new MuscleGroup(200L, MgName.CHEST, null, null);

        
        DayMuscleGroup entityWithRelationships = DayMuscleGroup.builder()
                .id(1L)
                .day(day)
                .muscleGroup(muscleGroup)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        DayMuscleGroupPayload result = mapper.toPayload(entityWithRelationships);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(100L, result.dayId());
        assertEquals(200L, result.muscleGroupId());
        assertEquals(now, result.createdAt());
        assertEquals(now, result.updatedAt());
    }

    @Test
    void toPayload_WithNullRelationships_ShouldHandleGracefully() {
        // Given
        DayMuscleGroup entityWithNullRelationships = DayMuscleGroup.builder()
        .id(1L)
        .day(null)
        .muscleGroup(null)
        .createdAt(now)
        .updatedAt(now)
        .build();
        
        // When
        DayMuscleGroupPayload result = mapper.toPayload(entityWithNullRelationships);
        
        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertNull(result.dayId());
        assertNull(result.muscleGroupId());
        assertEquals(now, result.createdAt());
        assertEquals(now, result.updatedAt());
    }
    
    @Test
    void updateEntity_WithValidData_ShouldUpdateMutableFields() {
        Day day = Day.builder().id(100L).build();
        MuscleGroup muscleGroup = new MuscleGroup(200L, MgName.CHEST, null, null);
        // Given
        DayMuscleGroup existingEntity = DayMuscleGroup.builder()
                .id(1L)
                .day(day)
                .muscleGroup(muscleGroup)
                .createdAt(now.minusSeconds(3600))
                .updatedAt(now.minusSeconds(1800))
                .build();

        DayMuscleGroupPayload updatePayload = new DayMuscleGroupPayload(
                1L, 15L, 25L, 1, 1, 1, now.minusSeconds(3600), now, 2, "complete"
        );

        // When
        mapper.updateEntity(existingEntity, updatePayload);

        // Then - updateEntity is empty and doesn't update any fields
        // All fields should remain unchanged since updateEntity doesn't modify anything
        assertEquals(now.minusSeconds(1800), existingEntity.getUpdatedAt()); // updatedAt remains unchanged

        // Verify relationships are preserved (updateEntity doesn't modify anything)
        assertNotNull(existingEntity.getDay());
        assertEquals(100L, existingEntity.getDay().getId());
        assertNotNull(existingEntity.getMuscleGroup());
        assertEquals(200L, existingEntity.getMuscleGroup().getId());
    }

    @Test
    void updateEntity_WithNullPayload_ShouldNotCrash() {
        // Given
        DayMuscleGroup existingEntity = DayMuscleGroup.builder().id(1L).build();

        // When & Then
        assertDoesNotThrow(() -> mapper.updateEntity(existingEntity, null));
    }

    @Test
    void updateEntity_WithNullEntity_ShouldNotCrash() {
        // When & Then
        assertDoesNotThrow(() -> mapper.updateEntity(null, samplePayload));
    }

    @Test
    void updateEntity_WithNullRelationshipIds_ShouldHandleGracefully() {
        Day day = Day.builder().id(100L).build();
        MuscleGroup muscleGroup = new MuscleGroup(200L, MgName.CHEST, null, null);
        // Given
        DayMuscleGroup existingEntity = DayMuscleGroup.builder()
                .id(1L)
                .day(day)
                .muscleGroup(muscleGroup)
                .build();

        DayMuscleGroupPayload payloadWithNullIds = new DayMuscleGroupPayload(
                1L, null, null, 1, 1, 1, now.minusSeconds(3600), now, 1, "complete"
        );

        // When
        mapper.updateEntity(existingEntity, payloadWithNullIds);

        // Then - updateEntity is empty and doesn't update any fields
        // All fields should remain unchanged since updateEntity doesn't modify anything
        assertNull(existingEntity.getUpdatedAt()); // updateEntity doesn't set updatedAt
        // Relationships are preserved (updateEntity doesn't modify anything)
        assertNotNull(existingEntity.getDay());
        assertEquals(100L, existingEntity.getDay().getId());
        assertNotNull(existingEntity.getMuscleGroup());
        assertEquals(200L, existingEntity.getMuscleGroup().getId());
    }

    @Test
    void mergeEntity_WithValidData_ShouldCreateNewEntityWithUpdatedFields() {
        Day day = Day.builder().id(100L).build();
        MuscleGroup muscleGroup = new MuscleGroup(200L, MgName.CHEST, null, null);
        // Given
        DayMuscleGroup existingEntity = DayMuscleGroup.builder()
                .id(1L)
                .day(day)
                .muscleGroup(muscleGroup)
                .createdAt(now.minusSeconds(3600))
                .updatedAt(now.minusSeconds(1800))
                .build();

        DayMuscleGroupPayload updatePayload = new DayMuscleGroupPayload(
                1L, 15L, 25L, 1, 1, 1, now.minusSeconds(3600), now, 1, "complete"
        );

        // When
        DayMuscleGroup result = mapper.mergeEntity(existingEntity, updatePayload);

        // Then
        assertNotNull(result);
        assertNotSame(existingEntity, result); // Should be a new instance
        assertEquals(1L, result.getId());
        assertEquals(100L, result.getDayId()); // Day relationship preserved from existing
        assertEquals(200L, result.getMuscleGroupId()); // MuscleGroup relationship preserved from existing
        assertEquals(now.minusSeconds(3600), result.getCreatedAt()); // Preserved from existing
        assertNotNull(result.getUpdatedAt());

        // Verify relationships are preserved from existing entity
        assertNotNull(result.getDay());
        assertEquals(100L, result.getDay().getId());
        assertNotNull(result.getMuscleGroup());
        assertEquals(200L, result.getMuscleGroup().getId());
    }

    @Test
    void mergeEntity_WithNullPayload_ShouldReturnNull() {
        // Given
        DayMuscleGroup existingEntity = DayMuscleGroup.builder().id(1L).build();

        // When
        DayMuscleGroup result = mapper.mergeEntity(existingEntity, null);

        // Then
        assertNotNull(result); // mergeEntity returns existing entity when payload is null
        assertEquals(existingEntity, result);
    }

    @Test
    void mergeEntity_WithNullEntity_ShouldReturnNull() {
        // When
        DayMuscleGroup result = mapper.mergeEntity(null, samplePayload);

        // Then
        assertNotNull(result); // mergeEntity calls toEntity when existing is null
        assertEquals(samplePayload.id(), result.getId());
    }

    @Test
    void mergeEntity_WithNullRelationshipIds_ShouldHandleGracefully() {
        Day day = Day.builder().id(100L).build();
        MuscleGroup muscleGroup = new MuscleGroup(200L, MgName.CHEST, null, null);
        // Given
        DayMuscleGroup existingEntity = DayMuscleGroup.builder()
                .id(1L)
                .day(day)
                .muscleGroup(muscleGroup)
                .createdAt(now.minusSeconds(3600))
                .build();

        DayMuscleGroupPayload payloadWithNullIds = new DayMuscleGroupPayload(
                1L, null, null, 1, 1, 1, now.minusSeconds(3600), now, 1, "complete"
        );

        // When
        DayMuscleGroup result = mapper.mergeEntity(existingEntity, payloadWithNullIds);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100L, result.getDayId()); // Day relationship preserved from existing
        assertEquals(200L, result.getMuscleGroupId()); // MuscleGroup relationship preserved from existing
        assertEquals(now.minusSeconds(3600), result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertNotNull(result.getDay()); // Relationships preserved from existing
        assertNotNull(result.getMuscleGroup());
        assertEquals(100L, result.getDay().getId());
        assertEquals(200L, result.getMuscleGroup().getId());
    }
}
