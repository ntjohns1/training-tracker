package com.noslen.training_tracker.mapper.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.MesocyclePayload;
import com.noslen.training_tracker.enums.Unit;
import com.noslen.training_tracker.model.mesocycle.MesoTemplate;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import com.noslen.training_tracker.util.EnumConverter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Mesocycle entities and MesocyclePayload DTOs
 */
@Component
public class MesocycleMapper {

    private final MesoNoteMapper mesoNoteMapper;

    public MesocycleMapper(MesoNoteMapper mesoNoteMapper) {
        this.mesoNoteMapper = mesoNoteMapper;
    }

    /**
     * Converts MesocyclePayload DTO to Mesocycle entity
     */
    public Mesocycle toEntity(MesocyclePayload payload) {
        if (payload == null) {
            return null;
        }

        return Mesocycle.builder()
                .id(payload.id())
                .mesocycleKey(payload.key())
                .userId(payload.userId())
                .name(payload.name())
                .days(payload.days())
                .unit(stringToUnit(payload.unit()))
                .sourceTemplate(payload.sourceTemplateId() != null ? 
                    MesoTemplate.builder().id(payload.sourceTemplateId()).build() : null)
                .sourceMeso(payload.sourceMesoId() != null ? 
                    Mesocycle.builder().id(payload.sourceMesoId()).build() : null)
                .microRirs(payload.microRirs())
                .createdAt(payload.createdAt())
                .updatedAt(payload.updatedAt())
                .finishedAt(payload.finishedAt())
                .deletedAt(payload.deletedAt())
                .firstMicroCompletedAt(payload.firstMicroCompletedAt())
                .firstWorkoutCompletedAt(payload.firstWorkoutCompletedAt())
                .firstExerciseCompletedAt(payload.firstExerciseCompletedAt())
                .firstSetCompletedAt(payload.firstSetCompletedAt())
                .lastMicroFinishedAt(payload.lastMicroFinishedAt())
                .lastSetCompletedAt(payload.lastSetCompletedAt())
                .lastSetSkippedAt(payload.lastSetSkippedAt())
                .lastWorkoutCompletedAt(payload.lastWorkoutCompletedAt())
                .lastWorkoutFinishedAt(payload.lastWorkoutFinishedAt())
                .lastWorkoutSkippedAt(payload.lastWorkoutSkippedAt())
                .lastWorkoutPartialedAt(payload.lastWorkoutPartialedAt())
                .weeks(Collections.emptyList()) // Will be populated separately if needed
                .notes(payload.notes() != null ? 
                    payload.notes().stream()
                        .map(mesoNoteMapper::toEntity)
                        .collect(Collectors.toList()) : Collections.emptyList())
                .status(null) // Not included in DTO
                .generatedFrom(null) // Not included in DTO
                .progressions(Collections.emptyMap()) // Will be populated separately if needed
                .build();
    }

    /**
     * Converts Mesocycle entity to MesocyclePayload DTO
     */
    public MesocyclePayload toPayload(Mesocycle entity) {
        if (entity == null) {
            return null;
        }

        return new MesocyclePayload(
                entity.getId(),
                entity.getMesocycleKey(),
                entity.getUserId(),
                entity.getName(),
                entity.getDays(),
                unitToString(entity.getUnit()),
                entity.getSourceTemplateId(), // Uses the helper method
                entity.getSourceMesoId(), // Uses the helper method
                entity.getMicroRirs(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getFinishedAt(),
                entity.getDeletedAt(),
                entity.getFirstMicroCompletedAt(),
                entity.getFirstWorkoutCompletedAt(),
                entity.getFirstExerciseCompletedAt(),
                entity.getFirstSetCompletedAt(),
                entity.getLastMicroFinishedAt(),
                entity.getLastSetCompletedAt(),
                entity.getLastSetSkippedAt(),
                entity.getLastWorkoutCompletedAt(),
                entity.getLastWorkoutFinishedAt(),
                entity.getLastWorkoutSkippedAt(),
                entity.getLastWorkoutPartialedAt(),
                entity.getWeeks() != null ? entity.getWeeks().size() : null, // Convert List<Day> to count
                entity.getNotes() != null ? 
                    entity.getNotes().stream()
                        .map(mesoNoteMapper::toPayload)
                        .collect(Collectors.toList()) : Collections.emptyList()
        );
    }

    /**
     * Updates mutable fields of an existing Mesocycle entity with values from MesocyclePayload.
     * Since Mesocycle is immutable (uses @Builder), this method is a no-op.
     * Use mergeEntity() instead for creating updated entities.
     */
    public void updateEntity(Mesocycle existingEntity, MesocyclePayload payload) {
        // No-op since Mesocycle is immutable
        // Use mergeEntity() to create a new entity with updated values
    }

    /**
     * Creates a new Mesocycle entity by merging an existing entity with values from MesocyclePayload.
     * This is used for updates since the entity is immutable.
     */
    public Mesocycle mergeEntity(Mesocycle existingEntity, MesocyclePayload payload) {
        if (existingEntity == null || payload == null) {
            return null;
        }

        return Mesocycle.builder()
                .id(existingEntity.getId()) // Preserve existing ID
                .mesocycleKey(payload.key() != null ? payload.key() : existingEntity.getMesocycleKey())
                .userId(payload.userId() != null ? payload.userId() : existingEntity.getUserId())
                .name(payload.name() != null ? payload.name() : existingEntity.getName())
                .days(payload.days() != null ? payload.days() : existingEntity.getDays())
                .unit(stringToUnit(payload.unit() != null ? payload.unit() :
                                           unitToString(existingEntity.getUnit())))
                .sourceTemplate(payload.sourceTemplateId() != null ? 
                    MesoTemplate.builder().id(payload.sourceTemplateId()).build() : existingEntity.getSourceTemplate())
                .sourceMeso(payload.sourceMesoId() != null ? 
                    Mesocycle.builder().id(payload.sourceMesoId()).build() : existingEntity.getSourceMeso())
                .microRirs(payload.microRirs() != null ? payload.microRirs() : existingEntity.getMicroRirs())
                .createdAt(existingEntity.getCreatedAt()) // Preserve original creation time
                .updatedAt(Instant.now()) // Set current time for update
                .finishedAt(payload.finishedAt() != null ? payload.finishedAt() : existingEntity.getFinishedAt())
                .deletedAt(payload.deletedAt() != null ? payload.deletedAt() : existingEntity.getDeletedAt())
                .firstMicroCompletedAt(payload.firstMicroCompletedAt() != null ? 
                    payload.firstMicroCompletedAt() : existingEntity.getFirstMicroCompletedAt())
                .firstWorkoutCompletedAt(payload.firstWorkoutCompletedAt() != null ? 
                    payload.firstWorkoutCompletedAt() : existingEntity.getFirstWorkoutCompletedAt())
                .firstExerciseCompletedAt(payload.firstExerciseCompletedAt() != null ? 
                    payload.firstExerciseCompletedAt() : existingEntity.getFirstExerciseCompletedAt())
                .firstSetCompletedAt(payload.firstSetCompletedAt() != null ? 
                    payload.firstSetCompletedAt() : existingEntity.getFirstSetCompletedAt())
                .lastMicroFinishedAt(payload.lastMicroFinishedAt() != null ? 
                    payload.lastMicroFinishedAt() : existingEntity.getLastMicroFinishedAt())
                .lastSetCompletedAt(payload.lastSetCompletedAt() != null ? 
                    payload.lastSetCompletedAt() : existingEntity.getLastSetCompletedAt())
                .lastSetSkippedAt(payload.lastSetSkippedAt() != null ? 
                    payload.lastSetSkippedAt() : existingEntity.getLastSetSkippedAt())
                .lastWorkoutCompletedAt(payload.lastWorkoutCompletedAt() != null ? 
                    payload.lastWorkoutCompletedAt() : existingEntity.getLastWorkoutCompletedAt())
                .lastWorkoutFinishedAt(payload.lastWorkoutFinishedAt() != null ? 
                    payload.lastWorkoutFinishedAt() : existingEntity.getLastWorkoutFinishedAt())
                .lastWorkoutSkippedAt(payload.lastWorkoutSkippedAt() != null ? 
                    payload.lastWorkoutSkippedAt() : existingEntity.getLastWorkoutSkippedAt())
                .lastWorkoutPartialedAt(payload.lastWorkoutPartialedAt() != null ? 
                    payload.lastWorkoutPartialedAt() : existingEntity.getLastWorkoutPartialedAt())
                .weeks(existingEntity.getWeeks()) // Preserve existing weeks
                .notes(payload.notes() != null ? 
                    payload.notes().stream()
                        .map(mesoNoteMapper::toEntity)
                        .collect(Collectors.toList()) : existingEntity.getNotes())
                .status(existingEntity.getStatus()) // Preserve existing status
                .generatedFrom(existingEntity.getGeneratedFrom()) // Preserve existing generatedFrom
                .progressions(existingEntity.getProgressions()) // Preserve existing progressions
                .build();
    }

    public Unit stringToUnit(String unitString) {
        if (unitString == null) {
            return null;
        }
        try {
            return EnumConverter.stringToEnum(Unit.class, unitString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid unit string: " + unitString);
        }
    }

    public String unitToString(Unit unit) {
        if (unit == null) {
            return null;
        }
        return EnumConverter.enumToSerializedValue(unit);
    }
}
