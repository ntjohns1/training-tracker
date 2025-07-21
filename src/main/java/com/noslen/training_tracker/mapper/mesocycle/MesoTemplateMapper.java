package com.noslen.training_tracker.mapper.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.MesoTemplatePayload;
import com.noslen.training_tracker.model.mesocycle.MesoTemplate;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * POJO Mapper for converting between MesoTemplate entity and MesoTemplatePayload DTO
 */
@Component
public class MesoTemplateMapper {

    /**
     * Converts MesoTemplatePayload to MesoTemplate entity
     */
    public MesoTemplate toEntity(MesoTemplatePayload payload) {
        if (payload == null) {
            return null;
        }

        MesoTemplate.MesoTemplateBuilder builder = MesoTemplate.builder()
                .id(payload.id())
                .templateKey(payload.key()) // Note: entity uses templateKey, DTO uses key
                .name(payload.name())
                .emphasis(payload.emphasis())
                .sex(payload.sex())
                .userId(payload.userId())
                .frequency(payload.frequency())
                .createdAt(payload.createdAt())
                .updatedAt(payload.updatedAt())
                .deletedAt(payload.deletedAt());

        // Set relationship entities if IDs are provided
        if (payload.sourceTemplateId() != null) {
            builder.sourceTemplate(MesoTemplate.builder().id(payload.sourceTemplateId()).build());
        }
        
        if (payload.sourceMesoId() != null) {
            builder.sourceMeso(Mesocycle.builder().id(payload.sourceMesoId()).build());
        }
        
        if (payload.prevTemplateId() != null) {
            builder.prevTemplate(MesoTemplate.builder().id(payload.prevTemplateId()).build());
        }

        return builder.build();
    }

    /**
     * Converts MesoTemplate entity to MesoTemplatePayload
     */
    public MesoTemplatePayload toPayload(MesoTemplate entity) {
        if (entity == null) {
            return null;
        }

        Long sourceTemplateId = null;
        if (entity.getSourceTemplate() != null) {
            sourceTemplateId = entity.getSourceTemplate().getId();
        }

        Long sourceMesoId = null;
        if (entity.getSourceMeso() != null) {
            sourceMesoId = entity.getSourceMeso().getId();
        }

        Long prevTemplateId = null;
        if (entity.getPrevTemplate() != null) {
            prevTemplateId = entity.getPrevTemplate().getId();
        }

        return new MesoTemplatePayload(
                entity.getId(),
                entity.getTemplateKey(), // Note: entity uses templateKey, DTO uses key
                entity.getName(),
                entity.getEmphasis(),
                entity.getSex(),
                entity.getUserId(),
                sourceTemplateId,
                sourceMesoId,
                prevTemplateId,
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt(),
                entity.getFrequency()
        );
    }

    /**
     * Updates an existing MesoTemplate entity with data from MesoTemplatePayload
     * Note: MesoTemplate is immutable, so this method is a no-op.
     * Use mergeEntity() instead to create a new entity with updated values.
     */
    public void updateEntity(MesoTemplate existing, MesoTemplatePayload payload) {
        // MesoTemplate is immutable - no mutable fields to update
        // Use mergeEntity() to create a new entity with updated values
    }

    /**
     * Creates a new MesoTemplate entity by merging existing entity with payload data
     * This method handles immutable fields by creating a new entity instance
     */
    public MesoTemplate mergeEntity(MesoTemplate existing, MesoTemplatePayload payload) {
        if (existing == null || payload == null) {
            return null;
        }

        MesoTemplate.MesoTemplateBuilder builder = MesoTemplate.builder()
                .id(existing.getId())
                .templateKey(payload.key() != null ? payload.key() : existing.getTemplateKey())
                .name(payload.name() != null ? payload.name() : existing.getName())
                .emphasis(payload.emphasis() != null ? payload.emphasis() : existing.getEmphasis())
                .sex(payload.sex() != null ? payload.sex() : existing.getSex())
                .userId(payload.userId() != null ? payload.userId() : existing.getUserId())
                .frequency(payload.frequency() != null ? payload.frequency() : existing.getFrequency())
                .createdAt(existing.getCreatedAt()) // Preserve original creation time
                .updatedAt(Instant.now()) // Set current time for update
                .deletedAt(payload.deletedAt() != null ? payload.deletedAt() : existing.getDeletedAt());

        // Handle relationship entities
        if (payload.sourceTemplateId() != null) {
            builder.sourceTemplate(MesoTemplate.builder().id(payload.sourceTemplateId()).build());
        } else if (existing.getSourceTemplate() != null) {
            builder.sourceTemplate(existing.getSourceTemplate());
        }

        if (payload.sourceMesoId() != null) {
            builder.sourceMeso(Mesocycle.builder().id(payload.sourceMesoId()).build());
        } else if (existing.getSourceMeso() != null) {
            builder.sourceMeso(existing.getSourceMeso());
        }

        if (payload.prevTemplateId() != null) {
            builder.prevTemplate(MesoTemplate.builder().id(payload.prevTemplateId()).build());
        } else if (existing.getPrevTemplate() != null) {
            builder.prevTemplate(existing.getPrevTemplate());
        }

        return builder.build();
    }
}
