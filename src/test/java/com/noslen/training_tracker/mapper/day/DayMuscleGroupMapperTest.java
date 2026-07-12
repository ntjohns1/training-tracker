package com.noslen.training_tracker.mapper.day;

import com.noslen.training_tracker.dto.day.response.DayMuscleGroupResponse;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.day.DayMuscleGroup;
import com.noslen.training_tracker.model.progression.MuscleGroup;
import com.noslen.training_tracker.enums.MgName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DayMuscleGroupMapperTest {

    private DayMuscleGroupMapper mapper;
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

        sampleEntity = DayMuscleGroup.builder()
                .id(1L)
                .day(day)
                .muscleGroup(muscleGroup)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    void toPayload_WithValidEntity_ShouldReturnPayload() {
        // When
        DayMuscleGroupResponse result = mapper.toPayload(sampleEntity);

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
        DayMuscleGroupResponse result = mapper.toPayload(null);

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
        DayMuscleGroupResponse result = mapper.toPayload(entityWithRelationships);

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
        DayMuscleGroupResponse result = mapper.toPayload(entityWithNullRelationships);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertNull(result.dayId());
        assertNull(result.muscleGroupId());
        assertEquals(now, result.createdAt());
        assertEquals(now, result.updatedAt());
    }

    @Test
    void toPayloadList_WithValidList_ShouldMapAll() {
        // When
        List<DayMuscleGroupResponse> result = mapper.toPayloadList(List.of(sampleEntity));

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleEntity.getId(), result.get(0).id());
    }

    @Test
    void toPayloadList_WithNullList_ShouldReturnNull() {
        // When
        List<DayMuscleGroupResponse> result = mapper.toPayloadList(null);

        // Then
        assertNull(result);
    }
}
