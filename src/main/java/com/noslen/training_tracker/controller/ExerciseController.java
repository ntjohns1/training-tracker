package com.noslen.training_tracker.controller;

import com.noslen.training_tracker.dto.exercise.response.ExerciseResponse;
import com.noslen.training_tracker.service.exercise.ExerciseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Read access to the exercise catalog for the meso builder / exercise picker.
 */
@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping
    public List<ExerciseResponse> getExercises(@RequestParam(required = false) Long muscleGroupId) {
        List<ExerciseResponse> exercises = exerciseService.getAllExercises();
        if (muscleGroupId == null) {
            return exercises;
        }
        return exercises.stream()
                .filter(ex -> muscleGroupId.equals(ex.muscleGroupId()))
                .toList();
    }

    @GetMapping("/{id}")
    public ExerciseResponse getExercise(@PathVariable Long id) {
        return exerciseService.getExercise(id);
    }
}
