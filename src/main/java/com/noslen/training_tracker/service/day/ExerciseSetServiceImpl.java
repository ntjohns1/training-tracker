package com.noslen.training_tracker.service.day;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noslen.training_tracker.dto.day.ExerciseSetResponse;
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
    public ExerciseSetResponse createExerciseSet(ExerciseSetResponse exerciseSetResponse) {
        if (exerciseSetResponse == null) {
            throw new IllegalArgumentException("ExerciseSetResponse cannot be null");
        }

        // Convert payload to entity
        ExerciseSet exerciseSet = mapper.toEntity(exerciseSetResponse);
        
        // Set creation timestamp
        exerciseSet.setCreatedAt(Instant.now());
        
        // Save and return as DTO
        ExerciseSet savedExerciseSet = repo.save(exerciseSet);
        return mapper.toPayload(savedExerciseSet);
    }

    @Override
    @Transactional
    public ExerciseSetResponse updateExerciseSet(Long id, ExerciseSetResponse exerciseSetResponse) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (exerciseSetResponse == null) {
            throw new IllegalArgumentException("ExerciseSetResponse cannot be null");
        }

        Optional<ExerciseSet> exerciseSetOptional = repo.findById(id);
        if (exerciseSetOptional.isEmpty()) {
            throw new RuntimeException("ExerciseSet not found with id: " + id);
        }

        ExerciseSet existingExerciseSet = exerciseSetOptional.get();
        
        // Update entity with payload data
        mapper.updateEntity(existingExerciseSet,
                            exerciseSetResponse);
        
        // Save and return as DTO
        ExerciseSet updatedExerciseSet = repo.save(existingExerciseSet);
        return mapper.toPayload(updatedExerciseSet);
    }

    @Override
    @Transactional(readOnly = true)
    public ExerciseSetResponse getExerciseSet(Long id) {
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
    public List<ExerciseSetResponse> getExerciseSetsByDayExerciseId(Long dayExerciseId) {
        if (dayExerciseId == null) {
            throw new IllegalArgumentException("DayExercise ID cannot be null");
        }

        List<ExerciseSet> exerciseSets = repo.findByDayExercise_Id(dayExerciseId);
        return mapper.toPayloadList(exerciseSets);
    }
}
