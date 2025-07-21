package com.noslen.training_tracker.service.day;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noslen.training_tracker.dto.day.ExerciseSetPayload;
import com.noslen.training_tracker.mapper.day.ExerciseSetMapper;
import com.noslen.training_tracker.model.day.ExerciseSet;
import com.noslen.training_tracker.repository.day.ExerciseSetRepo;

@Service
public class ExerciseSetServiceImpl implements ExerciseSetService { 
    private final ExerciseSetRepo repo;
    private final ExerciseSetMapper mapper;

    public ExerciseSetServiceImpl(ExerciseSetRepo repo, ExerciseSetMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ExerciseSetPayload createExerciseSet(ExerciseSetPayload exerciseSetPayload) {
        if (exerciseSetPayload == null) {
            throw new IllegalArgumentException("ExerciseSetPayload cannot be null");
        }

        // Convert payload to entity
        ExerciseSet exerciseSet = mapper.toEntity(exerciseSetPayload);
        
        // Set creation timestamp
        exerciseSet.setCreatedAt(Instant.now());
        
        // Save and return as DTO
        ExerciseSet savedExerciseSet = repo.save(exerciseSet);
        return mapper.toPayload(savedExerciseSet);
    }

    @Override
    @Transactional
    public ExerciseSetPayload updateExerciseSet(Long id, ExerciseSetPayload exerciseSetPayload) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (exerciseSetPayload == null) {
            throw new IllegalArgumentException("ExerciseSetPayload cannot be null");
        }

        Optional<ExerciseSet> exerciseSetOptional = repo.findById(id);
        if (exerciseSetOptional.isEmpty()) {
            throw new RuntimeException("ExerciseSet not found with id: " + id);
        }

        ExerciseSet existingExerciseSet = exerciseSetOptional.get();
        
        // Update entity with payload data
        mapper.updateEntity(existingExerciseSet, exerciseSetPayload);
        
        // Save and return as DTO
        ExerciseSet updatedExerciseSet = repo.save(existingExerciseSet);
        return mapper.toPayload(updatedExerciseSet);
    }

    @Override
    @Transactional(readOnly = true)
    public ExerciseSetPayload getExerciseSet(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<ExerciseSet> exerciseSetOptional = repo.findById(id);
        if (exerciseSetOptional.isEmpty()) {
            throw new RuntimeException("ExerciseSet not found with id: " + id);
        }

        return mapper.toPayload(exerciseSetOptional.get());
    }

    @Override
    @Transactional
    public void deleteExerciseSet(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        if (!repo.existsById(id)) {
            throw new RuntimeException("ExerciseSet not found with id: " + id);
        }

        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseSetPayload> getExerciseSetsByDayExerciseId(Long dayExerciseId) {
        if (dayExerciseId == null) {
            throw new IllegalArgumentException("DayExercise ID cannot be null");
        }

        List<ExerciseSet> exerciseSets = repo.findByDayExercise_Id(dayExerciseId);
        return mapper.toPayloadList(exerciseSets);
    }
}
