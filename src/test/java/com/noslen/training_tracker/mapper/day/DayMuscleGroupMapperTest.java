package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.DayMuscleGroupPayload;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.day.DayMuscleGroup;
import com.noslen.training_tracker.model.muscle_group.MuscleGroup;
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
        muscleGroup.setId(20L);

        samplePayload = new DayMuscleGroupPayload(
                1L,
                day,
                muscleGroup,
                now,
                now
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
        assertEquals(samplePayload.day(), result.getDay());
        assertEquals(samplePayload.muscleGroup(), result.getMuscleGroup());
        assertEquals(samplePayload.createdAt(), result.getCreatedAt());
        assertEquals(samplePayload.updatedAt(), result.getUpdatedAt());

        // Verify relationships are set
        assertNotNull(result.getDay());
        assertEquals(samplePayload.day().getId(), result.getDay().getId());
        assertNotNull(result.getMuscleGroup());
        assertEquals(samplePayload.muscleGroup().getId(), result.getMuscleGroup().getId());
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
                null, null, null, null, null
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
        MuscleGroup muscleGroup = MuscleGroup.builder().id(200L).build();
        
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
        // Given
        DayMuscleGroup existingEntity = DayMuscleGroup.builder()
                .id(1L)
                .dayId(10L)
                .muscleGroupId(20L)
                .createdAt(now.minusSeconds(3600))
                .updatedAt(now.minusSeconds(1800))
                .build();

        DayMuscleGroupPayload updatePayload = new DayMuscleGroupPayload(
                1L, 15L, 25L, now.minusSeconds(3600), now
        );

        // When
        mapper.updateEntity(existingEntity, updatePayload);

        // Then
        // Verify updatedAt is set to current time (not from payload)
        assertNotNull(existingEntity.getUpdatedAt());
        assertTrue(existingEntity.getUpdatedAt().isAfter(now.minusSeconds(10)));

        // Verify relationships are updated
        assertNotNull(existingEntity.getDay());
        assertEquals(15L, existingEntity.getDay().getId());
        assertNotNull(existingEntity.getMuscleGroup());
        assertEquals(25L, existingEntity.getMuscleGroup().getId());
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
        // Given
        DayMuscleGroup existingEntity = DayMuscleGroup.builder()
                .id(1L)
                .dayId(10L)
                .muscleGroupId(20L)
                .build();

        DayMuscleGroupPayload payloadWithNullIds = new DayMuscleGroupPayload(
                1L, null, null, now, now
        );

        // When
        mapper.updateEntity(existingEntity, payloadWithNullIds);

        // Then
        assertNotNull(existingEntity.getUpdatedAt());
        // Relationships should not be updated when IDs are null
        assertNull(existingEntity.getDay());
        assertNull(existingEntity.getMuscleGroup());
    }

    @Test
    void mergeEntity_WithValidData_ShouldCreateNewEntityWithUpdatedFields() {
        // Given
        DayMuscleGroup existingEntity = DayMuscleGroup.builder()
                .id(1L)
                .dayId(10L)
                .muscleGroupId(20L)
                .createdAt(now.minusSeconds(3600))
                .updatedAt(now.minusSeconds(1800))
                .build();

        DayMuscleGroupPayload updatePayload = new DayMuscleGroupPayload(
                1L, 15L, 25L, now.minusSeconds(3600), now
        );

        // When
        DayMuscleGroup result = mapper.mergeEntity(existingEntity, updatePayload);

        // Then
        assertNotNull(result);
        assertNotSame(existingEntity, result); // Should be a new instance
        assertEquals(1L, result.getId());
        assertEquals(15L, result.getDayId());
        assertEquals(25L, result.getMuscleGroupId());
        assertEquals(now.minusSeconds(3600), result.getCreatedAt()); // Preserved from existing
        assertNotNull(result.getUpdatedAt());

        // Verify relationships are set
        assertNotNull(result.getDay());
        assertEquals(15L, result.getDay().getId());
        assertNotNull(result.getMuscleGroup());
        assertEquals(25L, result.getMuscleGroup().getId());
    }

    @Test
    void mergeEntity_WithNullPayload_ShouldReturnNull() {
        // Given
        DayMuscleGroup existingEntity = DayMuscleGroup.builder().id(1L).build();

        // When
        DayMuscleGroup result = mapper.mergeEntity(existingEntity, null);

        // Then
        assertNull(result);
    }

    @Test
    void mergeEntity_WithNullEntity_ShouldReturnNull() {
        // When
        DayMuscleGroup result = mapper.mergeEntity(null, samplePayload);

        // Then
        assertNull(result);
    }

    @Test
    void mergeEntity_WithNullRelationshipIds_ShouldHandleGracefully() {
        // Given
        DayMuscleGroup existingEntity = DayMuscleGroup.builder()
                .id(1L)
                .dayId(10L)
                .muscleGroupId(20L)
                .createdAt(now.minusSeconds(3600))
                .build();

        DayMuscleGroupPayload payloadWithNullIds = new DayMuscleGroupPayload(
                1L, null, null, now.minusSeconds(3600), now
        );

        // When
        DayMuscleGroup result = mapper.mergeEntity(existingEntity, payloadWithNullIds);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNull(result.getDayId());
        assertNull(result.getMuscleGroupId());
        assertEquals(now.minusSeconds(3600), result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertNull(result.getDay());
        assertNull(result.getMuscleGroup());
    }
}
