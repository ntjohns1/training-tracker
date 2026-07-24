package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.dto.day.request.CreateDayRequest;
import com.noslen.training_tracker.dto.day.request.FinishDayRequest;
import com.noslen.training_tracker.dto.day.request.UpdateDayExerciseRequest;
import com.noslen.training_tracker.dto.day.request.UpdateDayMuscleGroupRequest;
import com.noslen.training_tracker.dto.day.request.UpdateDayRequest;
import com.noslen.training_tracker.dto.day.response.DayResponse;
import com.noslen.training_tracker.event.DayCompletedEvent;
import com.noslen.training_tracker.factory.DayFactory;
import com.noslen.training_tracker.mapper.day.DayMapper;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.repository.day.DayRepo;
import com.noslen.training_tracker.security.UserContext;
import org.springframework.context.ApplicationEventPublisher;
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

    private final DayRepo repo;
    private final DayMapper dayMapper;
    private final DayFactory dayFactory;
    private final UserContext userContext;
    private final ApplicationEventPublisher eventPublisher;
    private final DayExerciseService dayExerciseService;
    private final DayMuscleGroupService dayMuscleGroupService;

    public DayServiceImpl(DayRepo repo, DayMapper dayMapper, DayFactory dayFactory,
            UserContext userContext, ApplicationEventPublisher eventPublisher,
            DayExerciseService dayExerciseService, DayMuscleGroupService dayMuscleGroupService) {
        this.repo = repo;
        this.dayMapper = dayMapper;
        this.dayFactory = dayFactory;
        this.userContext = userContext;
        this.eventPublisher = eventPublisher;
        this.dayExerciseService = dayExerciseService;
        this.dayMuscleGroupService = dayMuscleGroupService;
    }

    @Override
    @Transactional
    public DayResponse createDay(CreateDayRequest dayRequest) {
        if (dayRequest == null) {
            throw new IllegalArgumentException("DayResponse cannot be null");
        }

        // TODO: refactor to validate mesocycle ownership at repository level
        // Use factory to create entity
        Day day = dayFactory.createFromRequest(dayRequest);

        // Save entity
        Day savedDay = repo.save(day);

        // Convert back to response DTO
        return dayMapper.toPayload(savedDay);
    }

    @Override
    @Transactional
    public DayResponse updateDay(Long dayId, UpdateDayRequest dayRequest) {
        if (dayId == null) {
            throw new IllegalArgumentException("Day ID cannot be null");
        }
        if (dayRequest == null) {
            throw new IllegalArgumentException("UpdateDayRequest cannot be null");
        }

        Optional<Day> existingOptional = repo.findById(dayId);
        if (existingOptional.isEmpty()) {
            throw new RuntimeException("Day not found with id: " + dayId);
        }

        Day existing = existingOptional.get();

        // Validate that the current user owns the mesocycle this day belongs to
        userContext.validateUserAccess(existing.getMesocycle()
                                               .getUserId());

        // Apply mutable fields directly (Day exposes setters only for these).
        if (dayRequest.finishedAt() != null) {
            existing.setFinishedAt(dayRequest.finishedAt());
        }
        existing.setUpdatedAt(dayRequest.updatedAt() != null ? dayRequest.updatedAt() : Instant.now());

        // Save updated entity
        Day savedEntity = repo.save(existing);

        // Convert back to payload and return
        return dayMapper.toPayload(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public DayResponse getDay(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Day day = repo.findById(id)
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
        Day existingDay = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Day not found with id: " + id));

        // Validate that the current user owns the mesocycle this day belongs to
        userContext.validateUserAccess(existingDay.getMesocycle()
                                               .getUserId());

        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DayResponse> getDaysByMesocycleId(Long mesocycleId) {
        if (mesocycleId == null) {
            throw new IllegalArgumentException("Mesocycle ID cannot be null");
        }

        List<Day> days = repo.findByMesocycleId(mesocycleId);

        return dayMapper.toPayloadList(days);
    }

    @Override
    public DayResponse getNextDayWithSameMuscleGroup(Long dayId, Long muscleGroupId) {
        Optional<Day> dayOpt = repo.findNextDayWithSameMuscleGroup(dayId,
                                                                   muscleGroupId);
        if (dayOpt.isEmpty()) {
            throw new RuntimeException("Next day not found for day: " + dayId + " and muscle group: " + muscleGroupId);
        }
        return dayMapper.toPayload(dayOpt.get());
    }


    @Override
    @Transactional
    public DayResponse completeDay(Long dayId, FinishDayRequest finishDayRequest) {
        if (dayId == null) {
            throw new IllegalArgumentException("Day ID cannot be null");
        }
        if (finishDayRequest == null) {
            throw new IllegalArgumentException("FinishDayRequest cannot be null");
        }

        // Find existing day and validate ownership before completion
        Day existingDay = repo.findById(dayId)
                .orElseThrow(() -> new RuntimeException("Day not found with id: " + dayId));

        // Validate that the current user owns the mesocycle this day belongs to
        userContext.validateUserAccess(existingDay.getMesocycle()
                                               .getUserId());

        // Persist the feedback carried in the finish payload so progression (which reads the
        // DayMuscleGroup/DayExercise rows) sees consistent state. jointPain per exercise;
        // pump/soreness/workload per muscle group. Values are null-guarded in the update services.
        persistFinishFeedback(finishDayRequest);

        // Mark day as completed by setting finishedAt timestamp
        existingDay.setFinishedAt(Instant.now());

        // Save updated entity
        Day savedDay = repo.save(existingDay);

        // Publish completion; DayCompletedKafkaPublisher forwards to Kafka only after this
        // transaction commits, which drives next-week progression asynchronously.
        Long mesoId = savedDay.getMesocycle() != null ? savedDay.getMesocycle().getId() : null;
        eventPublisher.publishEvent(new DayCompletedEvent(dayId, mesoId, finishDayRequest));

        // Return updated day response
        return dayMapper.toPayload(savedDay);
    }

    /** Writes the joint-pain and pump/soreness/workload feedback from a finish payload to the DB. */
    private void persistFinishFeedback(FinishDayRequest finishDayRequest) {
        if (finishDayRequest.exercises() != null) {
            for (FinishDayRequest.DayExerciseFinishRequest ex : finishDayRequest.exercises()) {
                if (ex.id() != null && ex.jointPain() != null) {
                    dayExerciseService.updateDayExercise(ex.id(),
                            new UpdateDayExerciseRequest(ex.id(), null, null, null, ex.jointPain(),
                                    null, null, null, ex.status()));
                }
            }
        }
        if (finishDayRequest.muscleGroups() != null) {
            for (FinishDayRequest.DayMuscleGroupFinishRequest dmg : finishDayRequest.muscleGroups()) {
                if (dmg.id() != null) {
                    dayMuscleGroupService.updateDayMuscleGroup(dmg.id(),
                            new UpdateDayMuscleGroupRequest(dmg.id(), null, null, dmg.pump(),
                                    dmg.soreness(), dmg.workload(), null, null, dmg.status()));
                }
            }
        }
    }

}
