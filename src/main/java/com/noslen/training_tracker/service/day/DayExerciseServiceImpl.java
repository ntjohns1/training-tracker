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
        
        // Update entity with payload data using MapStruct
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
        return dayExercise.map(mapper::toPayload).orElse(null);
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

        Optional<DayExercise> dayExercise = repo.findByDayIdAndExerciseId(dayId, exerciseId);
        return dayExercise.map(mapper::toPayload).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DayExercisePayload> getDayExercisesByDayId(Long dayId) {
        if (dayId == null) {
            throw new IllegalArgumentException("Day ID cannot be null");
        }

        List<DayExercise> dayExercises = repo.findByDayId(dayId);
        return mapper.toPayloadList(dayExercises);
    }
}
