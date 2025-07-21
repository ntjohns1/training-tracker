package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.dto.day.DayExercisePayload;
import com.noslen.training_tracker.mapper.day.DayExerciseMapper;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.repository.day.DayExerciseRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DayExerciseServiceImpl implements DayExerciseService {
    
    private final DayExerciseRepo repo;
    private final DayExerciseMapper mapper;

    public DayExerciseServiceImpl(DayExerciseRepo repo, DayExerciseMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public DayExercisePayload createDayExercise(DayExercisePayload dayExercisePayload) {
        if (dayExercisePayload == null) {
            throw new IllegalArgumentException("DayExercisePayload cannot be null");
        }

        // Convert payload to entity
        DayExercise dayExercise = mapper.toEntity(dayExercisePayload);
        
        // Set creation timestamp if not already set
        if (dayExercise.getCreatedAt() == null) {
            dayExercise.setCreatedAt(Instant.now());
        }
        if (dayExercise.getUpdatedAt() == null) {
            dayExercise.setUpdatedAt(Instant.now());
        }

        // Save entity
        DayExercise savedEntity = repo.save(dayExercise);

        // Convert back to payload and return
        return mapper.toPayload(savedEntity);
    }

    @Override
    public DayExercisePayload updateDayExercise(Long id, DayExercisePayload dayExercisePayload) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (dayExercisePayload == null) {
            throw new IllegalArgumentException("DayExercisePayload cannot be null");
        }

        Optional<DayExercise> existingOptional = repo.findById(id);
        if (existingOptional.isEmpty()) {
            throw new RuntimeException("DayExercise not found with id: " + id);
        }

        DayExercise existing = existingOptional.get();
        
        // Update entity with payload data using POJO mapper
        mapper.updateEntity(existing, dayExercisePayload);
        
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

        if (!repo.existsById(id)) {
            throw new RuntimeException("DayExercise not found with id: " + id);
        }

        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public DayExercisePayload getDayExercise(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<DayExercise> dayExercise = repo.findById(id);
        if (dayExercise.isEmpty()) {
            throw new RuntimeException("DayExercise not found with id: " + id);
        }
        
        return mapper.toPayload(dayExercise.get());
    }

    @Override
    @Transactional(readOnly = true)
    public DayExercisePayload getDayExercise(Long dayId, Long exerciseId) {
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
        
        return mapper.toPayload(dayExercise.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DayExercisePayload> getDayExercisesByDayId(Long dayId) {
        if (dayId == null) {
            throw new IllegalArgumentException("Day ID cannot be null");
        }

        List<DayExercise> dayExercises = repo.findByDay_Id(dayId);
        return mapper.toPayloadList(dayExercises);
    }
}
