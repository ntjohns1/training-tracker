package com.noslen.training_tracker.factory;

import com.noslen.training_tracker.dto.day.request.CreateDayRequest;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import com.noslen.training_tracker.repository.mesocycle.MesocycleRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Factory class for creating Day entities with proper initialization
 * and relationship handling, eliminating redundant instantiation patterns.
 */
@Component
// TODO: use services instead of repos for all instance fields, possibly EntityManager for mesocycle
public class DayFactory {

    @PersistenceContext
    private final EntityManager entityManager;

    public DayFactory(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    /**
     * Creates a new Day entity from a DayResponse DTO with proper timestamps
     * and mesocycle relationship handling.
     *
     * @param dayRequest@return a properly initialized Day entity
     * @throws RuntimeException if mesocycle is not found when mesoId is provided
     */
    public Day createFromRequest(@NotNull CreateDayRequest dayRequest) {
        // Convert DTO to entity
        Instant now = Instant.now();
        return Day.builder()
                .mesocycle(entityManager.getReference(Mesocycle.class,
                                                      dayRequest.mesoId()))
                .week(dayRequest.week())
                .position(dayRequest.position())
                .createdAt(now)
                .updatedAt(now)
                .label(dayRequest.label())
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
