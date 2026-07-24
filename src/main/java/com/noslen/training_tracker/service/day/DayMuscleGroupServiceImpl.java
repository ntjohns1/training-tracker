package com.noslen.training_tracker.service.day;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.noslen.training_tracker.dto.day.request.CreateDayMuscleGroupRequest;
import com.noslen.training_tracker.dto.day.request.UpdateDayMuscleGroupRequest;
import com.noslen.training_tracker.dto.day.response.DayMuscleGroupResponse;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noslen.training_tracker.enums.Status;
import com.noslen.training_tracker.mapper.day.DayMuscleGroupMapper;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.day.DayMuscleGroup;
import com.noslen.training_tracker.model.progression.MuscleGroup;
import com.noslen.training_tracker.repository.day.DayMuscleGroupRepo;
import com.noslen.training_tracker.util.EnumConverter;

@Service
@Transactional
public class DayMuscleGroupServiceImpl implements DayMuscleGroupService {

    private final EntityManager entityManager;
    private final DayMuscleGroupRepo repo;
    private final DayMuscleGroupMapper mapper;

    public DayMuscleGroupServiceImpl(EntityManager entityManager, DayMuscleGroupRepo repo, DayMuscleGroupMapper mapper) {
        this.entityManager = entityManager;
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public DayMuscleGroupResponse createDayMuscleGroup(CreateDayMuscleGroupRequest request) {
        if (request.dayId() == null || request.muscleGroupId() == null) {
            throw new IllegalArgumentException("Day ID and Muscle Group ID cannot be null");
        }

        Day day = entityManager.getReference(Day.class, request.dayId());
        MuscleGroup muscleGroup = entityManager.getReference(MuscleGroup.class, request.muscleGroupId());

        // Create new DayMuscleGroup entity
        Instant now = Instant.now();
        DayMuscleGroup dayMuscleGroup = DayMuscleGroup.builder()
                .day(day)
                .muscleGroup(muscleGroup)
                .recommendedSets(request.recommendedSets())
                .createdAt(request.createdAt() != null ? request.createdAt() : now)
                .updatedAt(request.updatedAt() != null ? request.updatedAt() : now)
                .build();

        // Save and return as DTO
        DayMuscleGroup savedDayMuscleGroup = repo.save(dayMuscleGroup);
        return mapper.toPayload(savedDayMuscleGroup);
    }

    @Override
    @Transactional
    public DayMuscleGroupResponse updateDayMuscleGroup(Long id, UpdateDayMuscleGroupRequest request) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (request == null) {
            throw new IllegalArgumentException("UpdateDayMuscleGroupRequest cannot be null");
        }

        // Find existing entity
        Optional<DayMuscleGroup> existingOpt = repo.findById(id);
        if (existingOpt.isEmpty()) {
            throw new RuntimeException("DayMuscleGroup not found with id: " + id);
        }

        DayMuscleGroup existing = existingOpt.get();

        if (request.dayId() != null) {
            existing.setDay(entityManager.getReference(Day.class, request.dayId()));
        }
        if (request.muscleGroupId() != null) {
            existing.setMuscleGroup(entityManager.getReference(MuscleGroup.class, request.muscleGroupId()));
        }
        if (request.pump() != null) {
            existing.setPump(request.pump());
        }
        if (request.soreness() != null) {
            existing.setSoreness(request.soreness());
        }
        if (request.workload() != null) {
            existing.setWorkload(request.workload());
        }
        if (request.recommendedSets() != null) {
            existing.setRecommendedSets(request.recommendedSets());
        }
        if (request.status() != null) {
            existing.setStatus(EnumConverter.stringToEnum(Status.class, request.status()));
        }

        existing.setUpdatedAt(Instant.now());

        // Save and return as DTO
        DayMuscleGroup savedDayMuscleGroup = repo.save(existing);
        return mapper.toPayload(savedDayMuscleGroup);
    }

    @Override
    @Transactional
    public void deleteDayMuscleGroup(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        if (!repo.existsById(id)) {
            throw new RuntimeException("DayMuscleGroup not found with id: " + id);
        }

        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public DayMuscleGroupResponse getDayMuscleGroup(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<DayMuscleGroup> dayMuscleGroupOpt = repo.findById(id);
        if (dayMuscleGroupOpt.isEmpty()) {
            throw new RuntimeException("DayMuscleGroup not found with id: " + id);
        }

        return mapper.toPayload(dayMuscleGroupOpt.get());
    }
//    TODO: review mapper.toPayloadList(entities) - can probably be simplified
//    TODO: review mapper.toPayload(dayMuscleGroupOpt.get()) - can probably be simplified

    @Override
    @Transactional(readOnly = true)
    public List<DayMuscleGroupResponse> getDayMuscleGroupsByDayId(Long dayId) {
        if (dayId == null) {
            throw new IllegalArgumentException("Day ID cannot be null");
        }

        List<DayMuscleGroup> entities = repo.findByDay_Id(dayId);
        return mapper.toPayloadList(entities);
    }

    /**
     * @param currentDmgId
     * @return
     */
    @Override
    public DayMuscleGroupResponse getMostRecentWithSameMuscleGroup(Long currentDmgId) {
        Optional<DayMuscleGroup> dayMuscleGroupOpt =
                repo.findMostRecentWithSameMuscleGroup(currentDmgId);

        if (dayMuscleGroupOpt.isEmpty()) {
            throw new RuntimeException("Previous DayMuscleGroup not found for: " + currentDmgId);
        }
        return mapper.toPayload(dayMuscleGroupOpt.get());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DayMuscleGroupResponse> findMostRecentWithSameMuscleGroup(Long currentDmgId) {
        return repo.findMostRecentWithSameMuscleGroup(currentDmgId).map(mapper::toPayload);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DayMuscleGroupResponse> findDayMuscleGroupForNextWeek(Long currentDmgId) {
        return repo.findNextDayMuscleGroupForNextWeek(currentDmgId).map(mapper::toPayload);
    }

    @Override
    public DayMuscleGroupResponse getDayMuscleGroupAt(Integer week, Integer position,
            Long muscleGroupId) {
        Optional<DayMuscleGroup> dayMuscleGroupOpt =
                repo.findDayMuscleGroupAt(week, position, muscleGroupId);
        if (dayMuscleGroupOpt.isEmpty()) {
            throw new RuntimeException("DayMuscleGroup not found for week: " + week + "at " +
                                               "position: " + position + "for muscle group: " + muscleGroupId);
        }
        return mapper.toPayload(dayMuscleGroupOpt.get());
    }

    /**
     * @param currentDmgId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public DayMuscleGroupResponse getNextDayMuscleGroup(Long currentDmgId) {
        Optional<DayMuscleGroup> dayMuscleGroupOpt =
                repo.findNextWithSameMuscleGroupByStatus(currentDmgId, Status.UNPROGRAMMED);

        if (dayMuscleGroupOpt.isEmpty()) {
            throw new RuntimeException("Next DayMuscleGroup not found for: " + currentDmgId);
        }
        return mapper.toPayload(dayMuscleGroupOpt.get());
    }

    @Override
    public DayMuscleGroupResponse getDayMuscleGroupForNextWeek(Long currentDmgId) {
        Optional<DayMuscleGroup> dayMuscleGroupOpt = repo.findNextDayMuscleGroupForNextWeek(currentDmgId);
        if (dayMuscleGroupOpt.isEmpty()) {
            throw new RuntimeException("Next DayMuscleGroup not found for: " + currentDmgId);
        }

        return mapper.toPayload(dayMuscleGroupOpt.get());
    }
}
