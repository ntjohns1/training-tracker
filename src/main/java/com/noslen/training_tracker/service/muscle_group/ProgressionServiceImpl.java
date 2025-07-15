package com.noslen.training_tracker.service.muscle_group;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.noslen.training_tracker.model.muscle_group.Progression;
import com.noslen.training_tracker.repository.muscle_group.ProgressionRepo;

@Service
public class ProgressionServiceImpl implements ProgressionService {

    private final ProgressionRepo repo;

    public ProgressionServiceImpl(ProgressionRepo progressionRepo) {
        this.repo = progressionRepo;
    }

    @Override
    public Progression createProgression(Progression progression) {
        return this.repo.save(progression);
    }

    @Override
    public Progression getProgression(Long progressionId) {
        Optional<Progression> progression = this.repo.findById(progressionId);
        return progression.orElse(null);
    }

    @Override
    public Progression updateProgression(Long progressionId, Progression progression) {
        Optional<Progression> progressionToUpdate = this.repo.findById(progressionId);
        if (progressionToUpdate.isPresent()) {
            progressionToUpdate.get().setMgProgressionType(progression.getMgProgressionType());
            this.repo.save(progressionToUpdate.get());
        }
        return progressionToUpdate.orElse(null);
    }

    @Override
    public void deleteProgression(Long progressionId) {
        this.repo.deleteById(progressionId);
    }
}
