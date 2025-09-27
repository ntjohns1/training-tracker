package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.dto.day.response.DayExerciseResponse;
import com.noslen.training_tracker.mapper.day.DayExerciseMapper;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.repository.day.DayExerciseRepo;
import com.noslen.training_tracker.security.UserContext;
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
    
    private final DayExerciseRepo repo;
    private final DayExerciseMapper mapper;
    private final UserContext userContext;

    public DayExerciseServiceImpl(DayExerciseRepo repo, DayExerciseMapper mapper, UserContext userContext) {
        this.repo = repo;
        this.mapper = mapper;
        this.userContext = userContext;
    }

    @Override
    public DayExerciseResponse createDayExercise(DayExerciseResponse dayExerciseResponse) {
        if (dayExerciseResponse == null) {
            throw new IllegalArgumentException("DayExerciseResponse cannot be null");
        }

        // Convert payload to entity
        DayExercise dayExercise = mapper.toEntity(dayExerciseResponse);
        
        // Set timestamps if not already set
        if (dayExercise.getCreatedAt() == null) {
            dayExercise.setCreatedAt(Instant.now());
        }
        if (dayExercise.getUpdatedAt() == null) {
            dayExercise.setUpdatedAt(Instant.now());
        }

        // Note: User access validation for create operations should be handled 
        // at the controller level since dayExercises are typically created as part of day/mesocycle creation

        // Save entity
        DayExercise savedEntity = repo.save(dayExercise);

        // Convert back to payload and return
        return mapper.toPayload(savedEntity);
    }

    @Override
    public DayExerciseResponse updateDayExercise(Long id, DayExerciseResponse dayExerciseResponse) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (dayExerciseResponse == null) {
            throw new IllegalArgumentException("DayExerciseResponse cannot be null");
        }

        Optional<DayExercise> existingOptional = repo.findById(id);
        if (existingOptional.isEmpty()) {
            throw new RuntimeException("DayExercise not found with id: " + id);
        }

        DayExercise existing = existingOptional.get();
        
        // Validate that the current user owns the mesocycle this day exercise belongs to
        userContext.validateUserAccess(existing.getDay().getMesocycle().getUserId());

        // Update entity with payload data using POJO mapper
        mapper.updateEntity(existing, dayExerciseResponse);
        
        // Ensure updated timestamp is set
        existing.setUpdatedAt(Instant.now());
        
        // Save updated entity
        DayExercise savedEntity = repo.save(existing);

        // Convert back to payload and return
        return mapper.toPayload(savedEntity);
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
        userContext.validateUserAccess(dayExercise.getDay().getMesocycle().getUserId());

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
        userContext.validateUserAccess(dayExercise.get().getDay().getMesocycle().getUserId());

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

        Optional<DayExercise> dayExercise = repo.findByDay_IdAndExercise_Id(dayId, exerciseId);
        if (dayExercise.isEmpty()) {
            throw new RuntimeException("DayExercise not found with dayId: " + dayId + " and exerciseId: " + exerciseId);
        }
        
        // Validate that the current user owns the mesocycle this day exercise belongs to
        userContext.validateUserAccess(dayExercise.get().getDay().getMesocycle().getUserId());

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
            userContext.validateUserAccess(dayExercises.get(0).getDay().getMesocycle().getUserId());
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
        return repo.findMaxJointPainByDayId(dayId, muscleGroupId);
    }

    /**
     * @param dayId
     * @param muscleGroupId
     * @return
     */
    @Override
    public Integer countDayExercisesByDayIdAndMuscleGroupId(Long dayId, Long muscleGroupId) {
        return repo.countByDayIdAndMuscleGroupId(dayId, muscleGroupId);
    }


}
