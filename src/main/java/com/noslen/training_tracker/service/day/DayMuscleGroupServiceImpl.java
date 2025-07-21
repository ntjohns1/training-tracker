package com.noslen.training_tracker.service.day;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noslen.training_tracker.dto.day.DayMuscleGroupPayload;
import com.noslen.training_tracker.mapper.day.DayMuscleGroupMapper;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.day.DayMuscleGroup;
import com.noslen.training_tracker.model.muscle_group.MuscleGroup;
import com.noslen.training_tracker.repository.day.DayMuscleGroupRepo;
import com.noslen.training_tracker.repository.day.DayRepo;
import com.noslen.training_tracker.repository.muscle_group.MuscleGroupRepo;

@Service
public class DayMuscleGroupServiceImpl implements DayMuscleGroupService {
    
    private final DayMuscleGroupRepo repo;
    private final DayRepo dayRepo;
    private final MuscleGroupRepo muscleGroupRepo;
    private final DayMuscleGroupMapper mapper;

    public DayMuscleGroupServiceImpl(DayMuscleGroupRepo repo, DayRepo dayRepo, 
                                   MuscleGroupRepo muscleGroupRepo, DayMuscleGroupMapper mapper) {
        this.repo = repo;
        this.dayRepo = dayRepo;
        this.muscleGroupRepo = muscleGroupRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public DayMuscleGroupPayload createDayMuscleGroup(Long dayId, Long muscleGroupId) {
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
    public DayMuscleGroupPayload updateDayMuscleGroup(Long id, DayMuscleGroupPayload dayMuscleGroupPayload) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (dayMuscleGroupPayload == null) {
            throw new IllegalArgumentException("DayMuscleGroupPayload cannot be null");
        }

        // Find existing entity
        Optional<DayMuscleGroup> existingOpt = repo.findById(id);
        if (existingOpt.isEmpty()) {
            throw new RuntimeException("DayMuscleGroup not found with id: " + id);
        }

        DayMuscleGroup existingDayMuscleGroup = existingOpt.get();
        
        // Update entity with payload data using merge since entity is mostly immutable
        DayMuscleGroup updatedDayMuscleGroup = mapper.mergeEntity(existingDayMuscleGroup, dayMuscleGroupPayload);
        updatedDayMuscleGroup = DayMuscleGroup.builder()
                .id(existingDayMuscleGroup.getId())
                .day(existingDayMuscleGroup.getDay())
                .muscleGroup(existingDayMuscleGroup.getMuscleGroup())
                .pump(dayMuscleGroupPayload.pump() != null ? dayMuscleGroupPayload.pump() : existingDayMuscleGroup.getPump())
                .soreness(dayMuscleGroupPayload.soreness() != null ? dayMuscleGroupPayload.soreness() : existingDayMuscleGroup.getSoreness())
                .workload(dayMuscleGroupPayload.workload() != null ? dayMuscleGroupPayload.workload() : existingDayMuscleGroup.getWorkload())
                .recommendedSets(dayMuscleGroupPayload.recommendedSets() != null ? dayMuscleGroupPayload.recommendedSets() : existingDayMuscleGroup.getRecommendedSets())
                .status(dayMuscleGroupPayload.status() != null ? dayMuscleGroupPayload.status() : existingDayMuscleGroup.getStatus())
                .createdAt(existingDayMuscleGroup.getCreatedAt())
                .updatedAt(Instant.now())
                .build();
        
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
    public DayMuscleGroupPayload getDayMuscleGroup(Long id) {
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
    public List<DayMuscleGroupPayload> getDayMuscleGroupsByDayId(Long dayId) {
        if (dayId == null) {
            throw new IllegalArgumentException("Day ID cannot be null");
        }

        List<DayMuscleGroup> entities = repo.findByDay_Id(dayId);
        return mapper.toPayloadList(entities);
    }
}
