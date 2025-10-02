package com.noslen.training_tracker.service.day;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.noslen.training_tracker.dto.day.request.UpdateDayMuscleGroupRequest;
import com.noslen.training_tracker.dto.day.response.DayMuscleGroupResponse;
import com.noslen.training_tracker.service.progression.ProgressionCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noslen.training_tracker.enums.Status;
import com.noslen.training_tracker.mapper.day.DayMuscleGroupMapper;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.day.DayMuscleGroup;
import com.noslen.training_tracker.model.progression.MuscleGroup;
import com.noslen.training_tracker.repository.day.DayMuscleGroupRepo;
import com.noslen.training_tracker.repository.day.DayRepo;
import com.noslen.training_tracker.repository.progression.MuscleGroupRepo;

@Service
@Transactional
public class DayMuscleGroupServiceImpl implements DayMuscleGroupService {
    
    private final DayMuscleGroupRepo repo;
    private final DayRepo dayRepo;
    private final MuscleGroupRepo muscleGroupRepo;
    private final DayMuscleGroupMapper mapper;
    private final DayExerciseService dayExerciseService;
    private final ExerciseSetService exerciseSetService;

    public DayMuscleGroupServiceImpl(DayMuscleGroupRepo repo, DayRepo dayRepo, 
                                   MuscleGroupRepo muscleGroupRepo, DayMuscleGroupMapper mapper, DayExerciseService dayExerciseService, ExerciseSetService exerciseSetService) {
        this.repo = repo;
        this.dayRepo = dayRepo;
        this.muscleGroupRepo = muscleGroupRepo;
        this.mapper = mapper;
        this.dayExerciseService = dayExerciseService;
        this.exerciseSetService = exerciseSetService;
    }

//    TODO: review this method, reconsider using DayRepo
    @Override
    @Transactional
    public DayMuscleGroupResponse createDayMuscleGroup(Long dayId, Long muscleGroupId) {
        if (dayId == null || muscleGroupId == null) {
            throw new IllegalArgumentException("Day ID and Muscle Group ID cannot be null");
        }

        // Fetch the Day and MuscleGroup entities
        Optional<Day> dayOpt = dayRepo.findById(dayId);
        if (dayOpt.isEmpty()) {
            throw new RuntimeException("Day not found with id: " + dayId);
        }

        Optional<MuscleGroup> muscleGroupOpt = muscleGroupRepo.findById(muscleGroupId);
        if (muscleGroupOpt.isEmpty()) {
            throw new RuntimeException("MuscleGroup not found with id: " + muscleGroupId);
        }

        // Create new DayMuscleGroup entity
        DayMuscleGroup dayMuscleGroup = DayMuscleGroup.builder()
                .day(dayOpt.get())
                .muscleGroup(muscleGroupOpt.get())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
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

        DayMuscleGroup existingDayMuscleGroup = existingOpt.get();
        
        // Merge updates from request while preserving relationships
        DayMuscleGroup updatedDayMuscleGroup = mapper.mergeEntity(existingDayMuscleGroup, request);
        
        // Save and return as DTO
        DayMuscleGroup savedDayMuscleGroup = repo.save(updatedDayMuscleGroup);
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

    /**
     * @param currentDmgId
     */
    @Override
    public void updateRecommendedSetsForNext(Long currentDmgId) {
//        TODO: check if next week is deload week
        Optional<DayMuscleGroup> currentDmgOpt = repo.findById(currentDmgId);
        if (currentDmgOpt.isEmpty()) {
            throw new RuntimeException("Current DayMuscleGroup not found for: " + currentDmgId);
        }
        Optional<DayMuscleGroup> previousDmgOpt = repo.findMostRecentWithSameMuscleGroup(currentDmgId);
        if (previousDmgOpt.isEmpty()) {
            throw new RuntimeException("Previous DayMuscleGroup not found for: " + currentDmgId);
        }
        DayMuscleGroup previousDmg = previousDmgOpt.get();
        DayMuscleGroup currentDmg = currentDmgOpt.get();

        Optional<DayMuscleGroup> nextDmgOpt =
                repo.findDayMuscleGroupAt(previousDmg.getDay().getWeek() + 1,
                                          previousDmg.getDay().getPosition(),
                                          previousDmg.getMuscleGroupId());
        if (nextDmgOpt.isEmpty()) {
            throw new RuntimeException("Next DayMuscleGroup not found for: " + currentDmgId);
        }
        DayMuscleGroup nextDmg = nextDmgOpt.get();
        Integer maxJointPain =
                dayExerciseService.getDayExerciseMaxJointPain(previousDmg.getDayId(),
                                                              previousDmg.getMuscleGroupId());
        Integer totalExerciseSets =
                exerciseSetService.countExerciseSetsByMuscleGroupId(previousDmg.getDayId(),
                                                                    previousDmg.getMuscleGroupId());

        int recommendedSets = ProgressionCalculator.calculateRecommendedSets(totalExerciseSets,
                                                                             maxJointPain,
                                                                             previousDmg.getPump(),
                                                                             currentDmg.getSoreness(),
                                                                             previousDmg.getWorkload());
        nextDmg.setRecommendedSets(recommendedSets);
        nextDmg.setStatus(Status.PROGRAMMED);
        nextDmg.setUpdatedAt(Instant.now());
        repo.save(nextDmg);
    }


}
