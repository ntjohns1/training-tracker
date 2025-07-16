package com.noslen.training_tracker.service.day;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.noslen.training_tracker.model.day.ExerciseSet;
import com.noslen.training_tracker.repository.day.ExerciseSetRepo;

@Service
public class ExerciseSetServiceImpl implements ExerciseSetService { 
    private final ExerciseSetRepo repo;

    public ExerciseSetServiceImpl(ExerciseSetRepo repo) {
        this.repo = repo;
    }

    @Override
    public ExerciseSet createExerciseSet(ExerciseSet exerciseSet) {
        return repo.save(exerciseSet);
    }

    @Override
    public ExerciseSet updateExerciseSet(Long id, ExerciseSet exerciseSet) {
        Optional<ExerciseSet> exerciseSetToUpdate = this.repo.findById(id);
        if (exerciseSetToUpdate.isPresent()) {
            this.repo.save(exerciseSetToUpdate.get());
        }
        return exerciseSetToUpdate.orElse(null);
    }

    @Override
    public ExerciseSet getExerciseSet(Long id) {
        Optional<ExerciseSet> exerciseSet = this.repo.findById(id);
        return exerciseSet.orElse(null);
    }

    @Override
    public void deleteExerciseSet(Long id) {
        this.repo.deleteById(id);
    }

    @Override
    public List<ExerciseSet> getExerciseSetsByDayExerciseId(Long dayExerciseId) {
        Optional<List<ExerciseSet>> exerciseSets = Optional.ofNullable(this.repo.findByDayExerciseId(dayExerciseId));
        return exerciseSets.orElse(null);
    }
}
