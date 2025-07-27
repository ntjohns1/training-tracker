package com.noslen.training_tracker.factory;

import com.noslen.training_tracker.dto.mesocycle.MesocycleResponse;
import com.noslen.training_tracker.model.mesocycle.MesoTemplate;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import com.noslen.training_tracker.util.EnumConverter;
import com.noslen.training_tracker.enums.Unit;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;

/**
 * Factory for creating Mesocycle entities.
 * Handles complex entity creation logic and eliminates redundant instantiation.
 */
@Component
public class MesocycleFactory {
    
    /**
     * Creates a new Mesocycle entity from response DTO for creation operations.
     * Sets appropriate timestamps and default values for new mesocycles.
     */
    public Mesocycle createFromResponse(MesocycleResponse response) {
        if (response == null) {
            return null;
        }
        
        Instant now = Instant.now();
        
        return Mesocycle.builder()
                .id(response.id())
                .mesocycleKey(response.key())
                .userId(response.userId())
                .name(response.name())
                .days(response.days())
                .unit(stringToUnit(response.unit()))
                .sourceTemplate(response.sourceTemplateId() != null ? 
                    MesoTemplate.builder().id(response.sourceTemplateId()).build() : null)
                .sourceMeso(response.sourceMesoId() != null ? 
                    Mesocycle.builder().id(response.sourceMesoId()).build() : null)
                .microRirs(response.microRirs())
                .createdAt(now)
                .updatedAt(now)
                .finishedAt(null) // New mesocycles are not finished
                .deletedAt(null) // New mesocycles are not deleted
                .firstMicroCompletedAt(null)
                .firstWorkoutCompletedAt(null)
                .firstExerciseCompletedAt(null)
                .firstSetCompletedAt(null)
                .lastMicroFinishedAt(null)
                .lastSetCompletedAt(null)
                .lastSetSkippedAt(null)
                .lastWorkoutCompletedAt(null)
                .lastWorkoutFinishedAt(null)
                .lastWorkoutSkippedAt(null)
                .lastWorkoutPartialedAt(null)
                .weeks(Collections.emptyList()) // Will be populated separately if needed
                .notes(Collections.emptyList()) // Will be populated separately if needed
                .status(null) // Will be set based on business logic
                .generatedFrom(null) // Will be set based on business logic
                .progressions(Collections.emptyMap()) // Will be populated separately if needed
                .build();
    }
    
    /**
     * Creates a Mesocycle entity for soft delete operation.
     * Preserves all existing data but sets deletion timestamp.
     */
    public Mesocycle createForSoftDelete(Mesocycle existing) {
        if (existing == null) {
            return null;
        }
        
        return Mesocycle.builder()
                .id(existing.getId())
                .mesocycleKey(existing.getMesocycleKey())
                .userId(existing.getUserId())
                .name(existing.getName())
                .days(existing.getDays())
                .unit(existing.getUnit())
                .sourceTemplate(existing.getSourceTemplate())
                .sourceMeso(existing.getSourceMeso())
                .microRirs(existing.getMicroRirs())
                .createdAt(existing.getCreatedAt())
                .updatedAt(Instant.now())
                .finishedAt(existing.getFinishedAt())
                .deletedAt(Instant.now()) // Set deletion timestamp
                .firstMicroCompletedAt(existing.getFirstMicroCompletedAt())
                .firstWorkoutCompletedAt(existing.getFirstWorkoutCompletedAt())
                .firstExerciseCompletedAt(existing.getFirstExerciseCompletedAt())
                .firstSetCompletedAt(existing.getFirstSetCompletedAt())
                .lastMicroFinishedAt(existing.getLastMicroFinishedAt())
                .lastSetCompletedAt(existing.getLastSetCompletedAt())
                .lastSetSkippedAt(existing.getLastSetSkippedAt())
                .lastWorkoutCompletedAt(existing.getLastWorkoutCompletedAt())
                .lastWorkoutFinishedAt(existing.getLastWorkoutFinishedAt())
                .lastWorkoutSkippedAt(existing.getLastWorkoutSkippedAt())
                .lastWorkoutPartialedAt(existing.getLastWorkoutPartialedAt())
                .weeks(existing.getWeeks())
                .notes(existing.getNotes())
                .status(existing.getStatus())
                .generatedFrom(existing.getGeneratedFrom())
                .progressions(existing.getProgressions())
                .build();
    }
    
    /**
     * Creates a Mesocycle entity for finish operation.
     * Sets finishedAt timestamp while preserving other data.
     */
    public Mesocycle createForFinish(Mesocycle existing) {
        if (existing == null) {
            return null;
        }
        
        return Mesocycle.builder()
                .id(existing.getId())
                .mesocycleKey(existing.getMesocycleKey())
                .userId(existing.getUserId())
                .name(existing.getName())
                .days(existing.getDays())
                .unit(existing.getUnit())
                .sourceTemplate(existing.getSourceTemplate())
                .sourceMeso(existing.getSourceMeso())
                .microRirs(existing.getMicroRirs())
                .createdAt(existing.getCreatedAt())
                .updatedAt(Instant.now())
                .finishedAt(Instant.now()) // Set finished timestamp
                .deletedAt(existing.getDeletedAt())
                .firstMicroCompletedAt(existing.getFirstMicroCompletedAt())
                .firstWorkoutCompletedAt(existing.getFirstWorkoutCompletedAt())
                .firstExerciseCompletedAt(existing.getFirstExerciseCompletedAt())
                .firstSetCompletedAt(existing.getFirstSetCompletedAt())
                .lastMicroFinishedAt(existing.getLastMicroFinishedAt())
                .lastSetCompletedAt(existing.getLastSetCompletedAt())
                .lastSetSkippedAt(existing.getLastSetSkippedAt())
                .lastWorkoutCompletedAt(existing.getLastWorkoutCompletedAt())
                .lastWorkoutFinishedAt(existing.getLastWorkoutFinishedAt())
                .lastWorkoutSkippedAt(existing.getLastWorkoutSkippedAt())
                .lastWorkoutPartialedAt(existing.getLastWorkoutPartialedAt())
                .weeks(existing.getWeeks())
                .notes(existing.getNotes())
                .status(existing.getStatus())
                .generatedFrom(existing.getGeneratedFrom())
                .progressions(existing.getProgressions())
                .build();
    }
    
    private Unit stringToUnit(String unitString) {
        if (unitString == null) {
            return null;
        }
        try {
            return EnumConverter.stringToEnum(Unit.class, unitString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid unit string: " + unitString);
        }
    }
}
