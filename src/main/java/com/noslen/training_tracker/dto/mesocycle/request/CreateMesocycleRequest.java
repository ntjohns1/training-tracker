package com.noslen.training_tracker.dto.mesocycle.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Request DTO for creating a new Mesocycle based on real API POST request structure.
 * This represents the actual data structure sent by clients when creating mesocycles.
 */
public record CreateMesocycleRequest(
        
        String name,
        
        Integer weeks,
        
        List<DayRequest> days,
        
        String unit,
        
        Map<String, ProgressionRequest> progressions,
        
        @JsonProperty("sourceTemplateId")
        Long sourceTemplateId,
        
        @JsonProperty("sourceMesoId")
        Long sourceMesoId
) {
    
    /**
     * Nested DTO representing a day within the mesocycle creation request.
     */
    public record DayRequest(
            String label,
            
            List<DayExerciseRequest> exercises
    ) {}
    
    /**
     * Nested DTO representing an exercise within a day creation request.
     */
    public record DayExerciseRequest(
            Long exerciseId
    ) {}
    
    /**
     * Nested DTO representing a progression configuration within the mesocycle.
     */
    public record ProgressionRequest(
            String mgProgressionType,
            
            Long muscleGroupId
    ) {}
}
