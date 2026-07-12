package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.dto.day.request.CreateDayExerciseRequest;
import com.noslen.training_tracker.dto.day.request.UpdateDayExerciseRequest;
import com.noslen.training_tracker.dto.day.response.DayExerciseResponse;
import com.noslen.training_tracker.enums.Status;
import com.noslen.training_tracker.mapper.day.DayExerciseMapper;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.model.exercise.Exercise;
import com.noslen.training_tracker.model.progression.MuscleGroup;
import com.noslen.training_tracker.repository.day.DayExerciseRepo;
import com.noslen.training_tracker.security.UserContext;
import com.noslen.training_tracker.util.EnumConverter;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for DayExercise operations.
 * Includes user data segregation through UserContext validation.
 */
@Service
@Transactional
public class DayExerciseServiceImpl implements DayExerciseService {

    private final EntityManager entityManager;
    private final DayExerciseRepo repo;
    private final DayExerciseMapper mapper;
    private final UserContext userContext;

    public DayExerciseServiceImpl(EntityManager entityManager, DayExerciseRepo repo, DayExerciseMapper mapper, UserContext userContext) {
        this.entityManager = entityManager;
        this.repo = repo;
        this.mapper = mapper;
        this.userContext = userContext;
    }

    @Override
    public DayExerciseResponse createDayExercise(CreateDayExerciseRequest dayExerciseRequest) {
        if (dayExerciseRequest == null) {
            throw new IllegalArgumentException("CreateDayExerciseRequest cannot be null");
        }

        // Resolve JPA relationships without loading the full entities
        Day day = entityManager.getReference(Day.class,
                                             dayExerciseRequest.dayId());
        Exercise exercise = entityManager.getReference(Exercise.class,
                                                       dayExerciseRequest.exerciseId());
        MuscleGroup muscleGroup = entityManager.getReference(MuscleGroup.class,
                                                             dayExerciseRequest.muscleGroupId());

        Instant now = Instant.now();
        DayExercise dayExercise = DayExercise.builder()
                .day(day)
                .exercise(exercise)
                .position(dayExerciseRequest.position())
                .jointPain(dayExerciseRequest.jointPain())
                .sourceDayExerciseId(dayExerciseRequest.sourceDayExerciseId())
                .createdAt(now)
                .updatedAt(now)
                .muscleGroup(muscleGroup)
                .build();

        // Note: User access validation for create operations should be handled
        // at the controller level since dayExercises are typically created as part of day/mesocycle creation

        // Save entity and convert back to payload
        return mapper.toPayload(repo.save(dayExercise));
    }

    @Override
    public DayExerciseResponse updateDayExercise(Long id, UpdateDayExerciseRequest dayExerciseRequest) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (dayExerciseRequest == null) {
            throw new IllegalArgumentException("UpdateDayExerciseRequest cannot be null");
        }

        Optional<DayExercise> existingOptional = repo.findById(id);
        if (existingOptional.isEmpty()) {
            throw new RuntimeException("DayExercise not found with id: " + id);
        }

        DayExercise existing = existingOptional.get();

        // Validate that the current user owns the mesocycle this day exercise belongs to
        userContext.validateUserAccess(existing.getDay()
                                               .getMesocycle()
                                               .getUserId());

        // Update relationships using references (consistent with create path)
        if (dayExerciseRequest.dayId() != null) {
            existing.setDay(entityManager.getReference(Day.class, dayExerciseRequest.dayId()));
        }
        if (dayExerciseRequest.exerciseId() != null) {
            existing.setExercise(entityManager.getReference(Exercise.class, dayExerciseRequest.exerciseId()));
        }
        if (dayExerciseRequest.muscleGroupId() != null) {
            existing.setMuscleGroup(entityManager.getReference(MuscleGroup.class, dayExerciseRequest.muscleGroupId()));
        }

        // Update mutable scalar fields when provided
        if (dayExerciseRequest.position() != null) {
            existing.setPosition(dayExerciseRequest.position());
        }
        if (dayExerciseRequest.jointPain() != null) {
            existing.setJointPain(dayExerciseRequest.jointPain());
        }
        if (dayExerciseRequest.sourceDayExerciseId() != null) {
            existing.setSourceDayExerciseId(dayExerciseRequest.sourceDayExerciseId());
        }
        if (dayExerciseRequest.status() != null) {
            existing.setStatus(EnumConverter.stringToEnum(Status.class, dayExerciseRequest.status()));
        }

        existing.setUpdatedAt(Instant.now());

        // Save updated entity and convert back to payload
        return mapper.toPayload(repo.save(existing));
    }

    @Override
    public void deleteDayExercise(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<DayExercise> dayExerciseOptional = repo.findById(id);
        if (dayExerciseOptional.isEmpty()) {
            throw new RuntimeException("DayExercise not found with id: " + id);
        }

        DayExercise dayExercise = dayExerciseOptional.get();

        // Validate that the current user owns the mesocycle this day exercise belongs to
        userContext.validateUserAccess(dayExercise.getDay()
                                               .getMesocycle()
                                               .getUserId());

        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public DayExerciseResponse getDayExercise(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<DayExercise> dayExercise = repo.findById(id);
        if (dayExercise.isEmpty()) {
            throw new RuntimeException("DayExercise not found with id: " + id);
        }

        // Validate that the current user owns the mesocycle this day exercise belongs to
        userContext.validateUserAccess(dayExercise.get()
                                               .getDay()
                                               .getMesocycle()
                                               .getUserId());

        return mapper.toPayload(dayExercise.get());
    }

    @Override
    @Transactional(readOnly = true)
    public DayExerciseResponse getDayExercise(Long dayId, Long exerciseId) {
        if (dayId == null) {
            throw new IllegalArgumentException("Day ID cannot be null");
        }
        if (exerciseId == null) {
            throw new IllegalArgumentException("Exercise ID cannot be null");
        }

        Optional<DayExercise> dayExercise = repo.findByDay_IdAndExercise_Id(dayId,
                                                                            exerciseId);
        if (dayExercise.isEmpty()) {
            throw new RuntimeException("DayExercise not found with dayId: " + dayId + " and exerciseId: " + exerciseId);
        }

        // Validate that the current user owns the mesocycle this day exercise belongs to
        userContext.validateUserAccess(dayExercise.get()
                                               .getDay()
                                               .getMesocycle()
                                               .getUserId());

        return mapper.toPayload(dayExercise.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DayExerciseResponse> getDayExercisesByDayId(Long dayId) {
        if (dayId == null) {
            throw new IllegalArgumentException("Day ID cannot be null");
        }

        List<DayExercise> dayExercises = repo.findByDay_Id(dayId);

        // Validate user access for each day exercise (they should all belong to the same day/mesocycle)
        if (!dayExercises.isEmpty()) {
            userContext.validateUserAccess(dayExercises.get(0)
                                                   .getDay()
                                                   .getMesocycle()
                                                   .getUserId());
        }

        return mapper.toPayloadList(dayExercises);
    }

    /**
     * @param dayId
     * @param muscleGroupId
     * @return
     */
    @Override
    public Integer getDayExerciseMaxJointPain(Long dayId, Long muscleGroupId) {
        return repo.findMaxJointPainByDayId(dayId,
                                            muscleGroupId);
    }

    /**
     * @param dayId
     * @param muscleGroupId
     * @return
     */
    @Override
    public Integer countDayExercisesByDayIdAndMuscleGroupId(Long dayId, Long muscleGroupId) {
        return repo.countByDayIdAndMuscleGroupId(dayId,
                                                 muscleGroupId);
    }


}
