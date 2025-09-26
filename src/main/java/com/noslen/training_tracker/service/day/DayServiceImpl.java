package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.dto.day.request.FinishDayRequest;
import com.noslen.training_tracker.dto.day.request.UpdateDayMuscleGroupRequest;
import com.noslen.training_tracker.dto.day.response.DayMgFeedbackResponse;
import com.noslen.training_tracker.dto.day.response.DayMuscleGroupResponse;
import com.noslen.training_tracker.dto.day.response.DayResponse;
import com.noslen.training_tracker.factory.DayFactory;
import com.noslen.training_tracker.mapper.day.DayMapper;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.repository.day.DayRepo;
import com.noslen.training_tracker.repository.mesocycle.MesocycleRepo;
import com.noslen.training_tracker.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for Day operations.
 * Uses DayFactory for clean entity creation and UserContext for data segregation.
 */
@Service
public class DayServiceImpl implements DayService {

    private final DayRepo dayRepo;
    private final DayMapper dayMapper;
    private final DayFactory dayFactory;
    private final UserContext userContext;
    private final MesocycleRepo mesocycleRepo;
    private final DayMuscleGroupService dayMuscleGroupService;
    private final DayExerciseService dayExerciseService;

    public DayServiceImpl(DayRepo dayRepo, DayMapper dayMapper, DayFactory dayFactory,
            UserContext userContext, MesocycleRepo mesocycleRepo,
            DayMuscleGroupService dayMuscleGroupService, DayExerciseService dayExerciseService) {
        this.dayRepo = dayRepo;
        this.dayMapper = dayMapper;
        this.dayFactory = dayFactory;
        this.userContext = userContext;
        this.mesocycleRepo = mesocycleRepo;
        this.dayMuscleGroupService = dayMuscleGroupService;
        this.dayExerciseService = dayExerciseService;
    }

    @Override
    @Transactional
    public DayResponse createDay(DayResponse dayResponse) {
        if (dayResponse == null) {
            throw new IllegalArgumentException("DayResponse cannot be null");
        }

        // Validate that the current user owns the mesocycle this day belongs to
        if (dayResponse.mesoId() != null) {
            // Fetch the mesocycle and validate ownership before creating the day
            var mesocycle = mesocycleRepo.findById(dayResponse.mesoId())
                    .orElseThrow(() -> new RuntimeException("Mesocycle not found with id: " + dayResponse.mesoId()));
            userContext.validateUserAccess(mesocycle.getUserId());
        }

        // Use factory to create entity
        Day day = dayFactory.createFromResponse(dayResponse);

        // Save entity
        Day savedDay = dayRepo.save(day);

        // Convert back to response DTO
        return dayMapper.toPayload(savedDay);
    }

    @Override
    @Transactional
    public DayResponse updateDay(Long dayId, DayResponse dayResponse) {
        if (dayId == null) {
            throw new IllegalArgumentException("Day ID cannot be null");
        }
        if (dayResponse == null) {
            throw new IllegalArgumentException("DayResponse cannot be null");
        }

        Optional<Day> existingOptional = dayRepo.findById(dayId);
        if (existingOptional.isEmpty()) {
            throw new RuntimeException("Day not found with id: " + dayId);
        }

        Day existing = existingOptional.get();

        // Validate that the current user owns the mesocycle this day belongs to
        userContext.validateUserAccess(existing.getMesocycle()
                                               .getUserId());

        // Update entity with payload data using mapper
        dayMapper.updateEntity(existing,
                               dayResponse);

        // Ensure updated timestamp is set
        existing.setUpdatedAt(Instant.now());

        // Save updated entity
        Day savedEntity = dayRepo.save(existing);

        // Convert back to payload and return
        return dayMapper.toPayload(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public DayResponse getDay(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Day day = dayRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Day not found with id: " + id));

        // Validate that the current user owns the mesocycle this day belongs to
        userContext.validateUserAccess(day.getMesocycle()
                                               .getUserId());

        return dayMapper.toPayload(day);
    }

    @Override
    @Transactional
    public void deleteDay(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        // Find existing day and validate ownership before deletion
        Day existingDay = dayRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Day not found with id: " + id));

        // Validate that the current user owns the mesocycle this day belongs to
        userContext.validateUserAccess(existingDay.getMesocycle()
                                               .getUserId());

        dayRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DayResponse> getDaysByMesocycleId(Long mesocycleId) {
        if (mesocycleId == null) {
            throw new IllegalArgumentException("Mesocycle ID cannot be null");
        }

        // First validate that the current user owns the mesocycle
        // We need to check this before querying days to ensure data segregation
        var mesocycle = mesocycleRepo.findById(mesocycleId)
                .orElseThrow(() -> new RuntimeException("Mesocycle not found with id: " + mesocycleId));
        userContext.validateUserAccess(mesocycle.getUserId());

        List<Day> days = dayRepo.findByMesocycleId(mesocycleId);

        return dayMapper.toPayloadList(days);
    }

    @Override
    @Transactional
    public DayResponse completeDay(Long dayId) {
        if (dayId == null) {
            throw new IllegalArgumentException("Day ID cannot be null");
        }

        // Find existing day and validate ownership before completion
        Day existingDay = dayRepo.findById(dayId)
                .orElseThrow(() -> new RuntimeException("Day not found with id: " + dayId));

        // Validate that the current user owns the mesocycle this day belongs to
        userContext.validateUserAccess(existingDay.getMesocycle()
                                               .getUserId());

        // Mark day as completed by setting finishedAt timestamp
        existingDay.setFinishedAt(Instant.now());

        // Save updated entity
        Day savedDay = dayRepo.save(existingDay);

        // Return updated day response
        return dayMapper.toPayload(savedDay);
    }

    /**
     * Programs the next day of the mesocycle based on the completed day.
     * This method should be called when a user finishes their workout and provides feedback.
     *
     * @param finishDayRequest The completed day request with feedback
     * @return void
     */
    @Override
    public void programNextDay(FinishDayRequest finishDayRequest) {
        // calculate recommended sets for next day
        for (FinishDayRequest.DayMuscleGroupFinishRequest dayMuscleGroupFinishRequest :
                finishDayRequest.muscleGroups()) {
//          TODO: we can look up by week and position in week instead of using findNextWithSameMuscleGroupByStatus
            dayMuscleGroupService.updateRecommendedSetsForNext(dayMuscleGroupFinishRequest.id());

//            TODO: day exercise repository method to find count of exercises for next day for a given
//             day muscle group.
            // create ExerciseSets for next day based on recommended sets
            Optional<Day> dayOpt = dayRepo.findNextDayWithSameMuscleGroup(finishDayRequest.id(),
                                                             dayMuscleGroupFinishRequest.muscleGroupId());
            if (dayOpt.isEmpty()) {
                throw new RuntimeException("Next day not found with id: " + finishDayRequest.id());
            }
            // get next day
            Day nextDay = dayOpt.get();
            // get recommended sets for next day
            // get count of exercises for next day for each day muscle group
            // create ExerciseSet for each recommended set
            // save ExerciseSet

        }

    }



}
