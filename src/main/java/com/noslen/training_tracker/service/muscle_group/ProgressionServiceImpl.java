package com.noslen.training_tracker.service.muscle_group;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noslen.training_tracker.dto.muscle_group.ProgressionPayload;
import com.noslen.training_tracker.mapper.muscle_group.ProgressionMapper;
import com.noslen.training_tracker.model.muscle_group.Progression;
import com.noslen.training_tracker.repository.muscle_group.ProgressionRepo;

@Service
@Transactional
public class ProgressionServiceImpl implements ProgressionService {

    private final ProgressionRepo repo;
    private final ProgressionMapper mapper;

    public ProgressionServiceImpl(ProgressionRepo progressionRepo, ProgressionMapper mapper) {
        this.repo = progressionRepo;
        this.mapper = mapper;
    }

    @Override
    public ProgressionPayload createProgression(ProgressionPayload progressionPayload) {
        if (progressionPayload == null) {
            throw new IllegalArgumentException("ProgressionPayload cannot be null");
        }

        // Convert payload to entity
        Progression progression = mapper.toEntity(progressionPayload);

        // Save entity
        Progression savedEntity = repo.save(progression);

        // Convert back to payload and return
        return mapper.toPayload(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgressionPayload getProgression(Long progressionId) {
        if (progressionId == null) {
            throw new IllegalArgumentException("Progression ID cannot be null");
        }

        Optional<Progression> progression = repo.findById(progressionId);
        if (progression.isEmpty()) {
            throw new RuntimeException("Progression not found with id: " + progressionId);
        }
        return mapper.toPayload(progression.get());
    }

    @Override
    public ProgressionPayload updateProgression(Long progressionId, ProgressionPayload progressionPayload) {
        if (progressionId == null) {
            throw new IllegalArgumentException("Progression ID cannot be null");
        }
        if (progressionPayload == null) {
            throw new IllegalArgumentException("ProgressionPayload cannot be null");
        }

        Optional<Progression> existingOptional = repo.findById(progressionId);
        if (existingOptional.isEmpty()) {
            throw new RuntimeException("Progression not found with id: " + progressionId);
        }

        Progression existing = existingOptional.get();
        
        // Update entity with payload data using mapper
        mapper.updateEntity(existing, progressionPayload);
        
        // Save updated entity
        Progression savedEntity = repo.save(existing);

        // Convert back to payload and return
        return mapper.toPayload(savedEntity);
    }

    @Override
    public void deleteProgression(Long progressionId) {
        if (progressionId == null) {
            throw new IllegalArgumentException("Progression ID cannot be null");
        }

        if (!repo.existsById(progressionId)) {
            throw new RuntimeException("Progression not found with id: " + progressionId);
        }

        repo.deleteById(progressionId);
    }
}
