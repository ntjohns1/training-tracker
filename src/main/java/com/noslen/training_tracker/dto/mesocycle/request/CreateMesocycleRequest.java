package com.noslen.training_tracker.dto.mesocycle.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Map;

/**
 * Request DTO for creating a new Mesocycle based on real API POST request structure.
 * This represents the actual data structure sent by clients when creating mesocycles.
 */
public record CreateMesocycleRequest(
        
        @NotBlank(message = "Mesocycle name cannot be blank")
        @Size(min = 1, max = 100, message = "Mesocycle name must be between 1 and 100 characters")
        String name,
        
        @NotNull(message = "Number of weeks is required")
        @Min(value = 4, message = "Mesocycle must have at least 4 weeks")
        @Max(value = 8, message = "Mesocycle cannot exceed 8 weeks")
        Integer weeks,
        
        @Valid
        @NotNull(message = "Days list cannot be null")
        @Size(min = 2, message = "Mesocycle must have at least 1 day pattern")
        List<DayRequest> days,
        
        @NotBlank(message = "Unit is required")
        @Pattern(regexp = "^(lbs|kgs)$", message = "Unit must be either 'lbs' or 'kgs'")
        String unit,
        
        @Valid
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
            @NotBlank(message = "Day label cannot be blank")
            @Size(max = 50, message = "Day label cannot exceed 50 characters")
            String label,
            
            @Valid
            @NotNull(message = "Exercises list cannot be null")
            @Size(min = 1, message = "Day must have at least 1 exercise")
            List<DayExerciseRequest> exercises
    ) {}
    
    /**
     * Nested DTO representing an exercise within a day creation request.
     */
    public record DayExerciseRequest(
            @NotNull(message = "Exercise ID is required")
            @Positive(message = "Exercise ID must be positive")
            Long exerciseId
    ) {}
    
    /**
     * Nested DTO representing progression configuration for a muscle group.
     */
    public record ProgressionRequest(
            @NotBlank(message = "Progression type is required")
            @Pattern(regexp = "^(regular|secondary|tertiary)$", message = "Progression type must be 'regular', 'secondary', or 'tertiary'")
            String mgProgressionType,
            
            @NotNull(message = "Muscle group ID is required")
            @Positive(message = "Muscle group ID must be positive")
            Long muscleGroupId
    ) {}
}
