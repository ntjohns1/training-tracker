package com.noslen.training_tracker.factory;

import com.noslen.training_tracker.dto.day.response.DayResponse;
import com.noslen.training_tracker.mapper.day.DayMapper;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import com.noslen.training_tracker.repository.mesocycle.MesocycleRepo;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

/**
 * Factory class for creating Day entities with proper initialization
 * and relationship handling, eliminating redundant instantiation patterns.
 */
@Component
// TODO: use services instead of repos for all instance fields, possibly EntityManager for mesocycle
public class DayFactory {

    private final DayMapper dayMapper;
    private final MesocycleRepo mesocycleRepo;

    public DayFactory(DayMapper dayMapper, MesocycleRepo mesocycleRepo) {
        this.dayMapper = dayMapper;
        this.mesocycleRepo = mesocycleRepo;
    }

    /**
     * Creates a new Day entity from a DayResponse DTO with proper timestamps
     * and mesocycle relationship handling.
     *
     * @param dayResponse the DTO containing day data
     * @return a properly initialized Day entity
     * @throws RuntimeException if mesocycle is not found when mesoId is provided
     */
    public Day createFromResponse(DayResponse dayResponse) {
        if (dayResponse == null) {
            throw new IllegalArgumentException("DayResponse cannot be null");
        }

        // Convert DTO to entity
        Day day = dayMapper.toEntity(dayResponse);
        
        // Set timestamps for new entity
        Instant now = Instant.now();
        
        // Handle mesocycle relationship if mesoId is provided
        Mesocycle mesocycle = null;
        if (dayResponse.mesoId() != null) {
            Optional<Mesocycle> mesocycleOpt = mesocycleRepo.findById(dayResponse.mesoId());
            if (mesocycleOpt.isEmpty()) {
                throw new RuntimeException("Mesocycle not found with id: " + dayResponse.mesoId());
            }
            mesocycle = mesocycleOpt.get();
        }

        // Build entity with proper initialization
        return Day.builder()
                .id(day.getId())
                .mesocycle(mesocycle)
                .week(day.getWeek())
                .position(day.getPosition())
                .createdAt(now)
                .updatedAt(now)
                .bodyweight(day.getBodyweight())
                .bodyweightAt(day.getBodyweightAt())
                .unit(day.getUnit())
                .finishedAt(day.getFinishedAt())
                .label(day.getLabel())
                .build();
    }

    /**
     * Creates a Day entity for finishing (sets finishedAt timestamp).
     *
     * @param existingDay the existing Day entity to finish
     * @return a Day entity with finishedAt timestamp set
     */
    public Day createForFinish(Day existingDay) {
        if (existingDay == null) {
            throw new IllegalArgumentException("Existing Day cannot be null");
        }

        Instant now = Instant.now();
        
        return Day.builder()
                .id(existingDay.getId())
                .mesocycle(existingDay.getMesocycle())
                .week(existingDay.getWeek())
                .position(existingDay.getPosition())
                .createdAt(existingDay.getCreatedAt())
                .updatedAt(now)
                .bodyweight(existingDay.getBodyweight())
                .bodyweightAt(existingDay.getBodyweightAt())
                .unit(existingDay.getUnit())
                .finishedAt(now)
                .label(existingDay.getLabel())
                .build();
    }
}
