package com.noslen.training_tracker.mapper.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.MesoTemplatePayload;
import com.noslen.training_tracker.model.mesocycle.MesoTemplate;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class MesoTemplateMapperTest {

    private MesoTemplateMapper mapper;
    private MesoTemplatePayload samplePayload;
    private MesoTemplate sampleEntity;
    private Instant now;

    @BeforeEach
    void setUp() {
        mapper = new MesoTemplateMapper();
        now = Instant.now();

        samplePayload = new MesoTemplatePayload(
                1L,
                "test-key",
                "Test Template",
                "strength",
                "male",
                100L,
                2L,
                3L,
                4L,
                now,
                now,
                null,
                4
        );

        sampleEntity = MesoTemplate.builder()
                .id(1L)
                .templateKey("test-key")
                .name("Test Template")
                .emphasis("strength")
                .sex("male")
                .userId(100L)
                .sourceTemplate(MesoTemplate.builder().id(2L).build())
                .sourceMeso(Mesocycle.builder().id(3L).build())
                .prevTemplate(MesoTemplate.builder().id(4L).build())
                .createdAt(now)
                .updatedAt(now)
                .deletedAt(null)
                .frequency(4)
                .build();
    }

    @Test
    void toEntity_WithValidPayload_ShouldReturnEntity() {
        // When
        MesoTemplate result = mapper.toEntity(samplePayload);

        // Then
        assertNotNull(result);
        assertEquals(samplePayload.id(), result.getId());
        assertEquals(samplePayload.key(), result.getTemplateKey());
        assertEquals(samplePayload.name(), result.getName());
        assertEquals(samplePayload.emphasis(), result.getEmphasis());
        assertEquals(samplePayload.sex(), result.getSex());
        assertEquals(samplePayload.userId(), result.getUserId());
        assertEquals(samplePayload.frequency(), result.getFrequency());
        assertEquals(samplePayload.createdAt(), result.getCreatedAt());
        assertEquals(samplePayload.updatedAt(), result.getUpdatedAt());
        assertEquals(samplePayload.deletedAt(), result.getDeletedAt());

        // Verify relationships are set
        assertNotNull(result.getSourceTemplate());
        assertEquals(samplePayload.sourceTemplateId(), result.getSourceTemplate().getId());
        assertNotNull(result.getSourceMeso());
        assertEquals(samplePayload.sourceMesoId(), result.getSourceMeso().getId());
        assertNotNull(result.getPrevTemplate());
        assertEquals(samplePayload.prevTemplateId(), result.getPrevTemplate().getId());
    }

    @Test
    void toEntity_WithNullPayload_ShouldReturnNull() {
        // When
        MesoTemplate result = mapper.toEntity(null);

        // Then
        assertNull(result);
    }

    @Test
    void toEntity_WithNullRelationshipIds_ShouldHandleGracefully() {
        // Given
        MesoTemplatePayload payloadWithNullIds = new MesoTemplatePayload(
                1L, "test-key", "Test Template", "strength", "male", 100L,
                null, null, null, now, now, null, 4
        );

        // When
        MesoTemplate result = mapper.toEntity(payloadWithNullIds);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test-key", result.getTemplateKey());
        assertEquals("Test Template", result.getName());
        assertNull(result.getSourceTemplate());
        assertNull(result.getSourceMeso());
        assertNull(result.getPrevTemplate());
    }

    @Test
    void toEntity_WithNullFields_ShouldHandleGracefully() {
        // Given
        MesoTemplatePayload payloadWithNulls = new MesoTemplatePayload(
                null, null, null, null, null, null, null, null, null, null, null, null, null
        );

        // When
        MesoTemplate result = mapper.toEntity(payloadWithNulls);

        // Then
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getTemplateKey());
        assertNull(result.getName());
        assertNull(result.getEmphasis());
        assertNull(result.getSex());
        assertNull(result.getUserId());
        assertNull(result.getFrequency());
        assertNull(result.getSourceTemplate());
        assertNull(result.getSourceMeso());
        assertNull(result.getPrevTemplate());
    }

    @Test
    void toPayload_WithValidEntity_ShouldReturnPayload() {
        // When
        MesoTemplatePayload result = mapper.toPayload(sampleEntity);

        // Then
        assertNotNull(result);
        assertEquals(sampleEntity.getId(), result.id());
        assertEquals(sampleEntity.getTemplateKey(), result.key());
        assertEquals(sampleEntity.getName(), result.name());
        assertEquals(sampleEntity.getEmphasis(), result.emphasis());
        assertEquals(sampleEntity.getSex(), result.sex());
        assertEquals(sampleEntity.getUserId(), result.userId());
        assertEquals(sampleEntity.getFrequency(), result.frequency());
        assertEquals(sampleEntity.getCreatedAt(), result.createdAt());
        assertEquals(sampleEntity.getUpdatedAt(), result.updatedAt());
        assertEquals(sampleEntity.getDeletedAt(), result.deletedAt());

        // Verify relationship IDs are extracted
        assertEquals(2L, result.sourceTemplateId());
        assertEquals(3L, result.sourceMesoId());
        assertEquals(4L, result.prevTemplateId());
    }

    @Test
    void toPayload_WithNullEntity_ShouldReturnNull() {
        // When
        MesoTemplatePayload result = mapper.toPayload(null);

        // Then
        assertNull(result);
    }

    @Test
    void toPayload_WithNullRelationships_ShouldHandleGracefully() {
        // Given
        MesoTemplate entityWithNullRelationships = MesoTemplate.builder()
                .id(1L)
                .templateKey("test-key")
                .name("Test Template")
                .emphasis("strength")
                .sex("male")
                .userId(100L)
                .frequency(4)
                .sourceTemplate(null)
                .sourceMeso(null)
                .prevTemplate(null)
                .createdAt(now)
                .updatedAt(now)
                .deletedAt(null)
                .build();

        // When
        MesoTemplatePayload result = mapper.toPayload(entityWithNullRelationships);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("test-key", result.key());
        assertEquals("Test Template", result.name());
        assertNull(result.sourceTemplateId());
        assertNull(result.sourceMesoId());
        assertNull(result.prevTemplateId());
    }

    @Test
    void updateEntity_ShouldBeNoOp() {
        // Given
        MesoTemplate existingEntity = MesoTemplate.builder()
                .id(1L)
                .templateKey("old-key")
                .name("Old Name")
                .build();

        MesoTemplatePayload updatePayload = new MesoTemplatePayload(
                1L, "new-key", "New Name", "strength", "male", 100L,
                null, null, null, now, now, null, 4
        );

        // When
        mapper.updateEntity(existingEntity, updatePayload);

        // Then
        // Since MesoTemplate is immutable, updateEntity should be a no-op
        // The entity should remain unchanged
        assertEquals("old-key", existingEntity.getTemplateKey());
        assertEquals("Old Name", existingEntity.getName());
    }

    @Test
    void updateEntity_WithNullPayload_ShouldNotCrash() {
        // Given
        MesoTemplate existingEntity = MesoTemplate.builder().id(1L).build();

        // When & Then
        assertDoesNotThrow(() -> mapper.updateEntity(existingEntity, null));
    }

    @Test
    void updateEntity_WithNullEntity_ShouldNotCrash() {
        // When & Then
        assertDoesNotThrow(() -> mapper.updateEntity(null, samplePayload));
    }

    @Test
    void mergeEntity_WithValidData_ShouldCreateNewEntityWithUpdatedFields() {
        // Given
        MesoTemplate existingEntity = MesoTemplate.builder()
                .id(1L)
                .templateKey("old-key")
                .name("Old Name")
                .emphasis("endurance")
                .sex("female")
                .userId(50L)
                .frequency(3)
                .sourceTemplate(MesoTemplate.builder().id(10L).build())
                .sourceMeso(Mesocycle.builder().id(20L).build())
                .prevTemplate(MesoTemplate.builder().id(30L).build())
                .createdAt(now.minusSeconds(3600))
                .updatedAt(now.minusSeconds(1800))
                .deletedAt(null)
                .build();

        MesoTemplatePayload updatePayload = new MesoTemplatePayload(
                1L, "new-key", "New Name", "strength", "male", 100L,
                2L, 3L, 4L, now.minusSeconds(3600), now, null, 4
        );

        // When
        MesoTemplate result = mapper.mergeEntity(existingEntity, updatePayload);

        // Then
        assertNotNull(result);
        assertNotSame(existingEntity, result); // Should be a new instance
        assertEquals(1L, result.getId());
        assertEquals("new-key", result.getTemplateKey());
        assertEquals("New Name", result.getName());
        assertEquals("strength", result.getEmphasis());
        assertEquals("male", result.getSex());
        assertEquals(100L, result.getUserId());
        assertEquals(4, result.getFrequency());
        assertEquals(now.minusSeconds(3600), result.getCreatedAt()); // Preserved from existing
        assertNotNull(result.getUpdatedAt());
        assertTrue(result.getUpdatedAt().isAfter(now.minusSeconds(10))); // Should be current time

        // Verify relationships are updated
        assertNotNull(result.getSourceTemplate());
        assertEquals(2L, result.getSourceTemplate().getId());
        assertNotNull(result.getSourceMeso());
        assertEquals(3L, result.getSourceMeso().getId());
        assertNotNull(result.getPrevTemplate());
        assertEquals(4L, result.getPrevTemplate().getId());
    }

    @Test
    void mergeEntity_WithNullPayload_ShouldReturnNull() {
        // Given
        MesoTemplate existingEntity = MesoTemplate.builder().id(1L).build();

        // When
        MesoTemplate result = mapper.mergeEntity(existingEntity, null);

        // Then
        assertNull(result);
    }

    @Test
    void mergeEntity_WithNullEntity_ShouldReturnNull() {
        // When
        MesoTemplate result = mapper.mergeEntity(null, samplePayload);

        // Then
        assertNull(result);
    }

    @Test
    void mergeEntity_WithNullFieldsInPayload_ShouldPreserveExistingValues() {
        // Given
        MesoTemplate existingSourceTemplate = MesoTemplate.builder().id(10L).build();
        Mesocycle existingSourceMeso = Mesocycle.builder().id(20L).build();
        MesoTemplate existingPrevTemplate = MesoTemplate.builder().id(30L).build();

        MesoTemplate existingEntity = MesoTemplate.builder()
                .id(1L)
                .templateKey("old-key")
                .name("Old Name")
                .emphasis("endurance")
                .sex("female")
                .userId(50L)
                .frequency(3)
                .sourceTemplate(existingSourceTemplate)
                .sourceMeso(existingSourceMeso)
                .prevTemplate(existingPrevTemplate)
                .createdAt(now.minusSeconds(3600))
                .deletedAt(null)
                .build();

        MesoTemplatePayload updatePayload = new MesoTemplatePayload(
                1L, null, "New Name", null, null, null,
                null, null, null, now.minusSeconds(3600), now, null, null
        );

        // When
        MesoTemplate result = mapper.mergeEntity(existingEntity, updatePayload);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("old-key", result.getTemplateKey()); // Preserved from existing
        assertEquals("New Name", result.getName()); // Updated from payload
        assertEquals("endurance", result.getEmphasis()); // Preserved from existing
        assertEquals("female", result.getSex()); // Preserved from existing
        assertEquals(50L, result.getUserId()); // Preserved from existing
        assertEquals(3, result.getFrequency()); // Preserved from existing
        assertEquals(now.minusSeconds(3600), result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        // Verify relationships are preserved from existing
        assertNotNull(result.getSourceTemplate());
        assertEquals(10L, result.getSourceTemplate().getId());
        assertSame(existingSourceTemplate, result.getSourceTemplate());
        assertNotNull(result.getSourceMeso());
        assertEquals(20L, result.getSourceMeso().getId());
        assertSame(existingSourceMeso, result.getSourceMeso());
        assertNotNull(result.getPrevTemplate());
        assertEquals(30L, result.getPrevTemplate().getId());
        assertSame(existingPrevTemplate, result.getPrevTemplate());
    }

    @Test
    void mergeEntity_WithDeletedAtField_ShouldHandleCorrectly() {
        // Given
        MesoTemplate existingEntity = MesoTemplate.builder()
                .id(1L)
                .templateKey("test-key")
                .name("Test Template")
                .createdAt(now.minusSeconds(3600))
                .deletedAt(null)
                .build();

        Instant deletionTime = now.minusSeconds(300);
        MesoTemplatePayload updatePayload = new MesoTemplatePayload(
                1L, "test-key", "Test Template", "strength", "male", 100L,
                null, null, null, now.minusSeconds(3600), now, deletionTime, 4
        );

        // When
        MesoTemplate result = mapper.mergeEntity(existingEntity, updatePayload);

        // Then
        assertNotNull(result);
        assertEquals(deletionTime, result.getDeletedAt());
    }
}
