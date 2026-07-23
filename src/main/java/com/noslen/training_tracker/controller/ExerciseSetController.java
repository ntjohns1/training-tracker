package com.noslen.training_tracker.controller;

import com.noslen.training_tracker.dto.day.request.CreateExerciseSetRequest;
import com.noslen.training_tracker.dto.day.request.UpdateExerciseSetRequest;
import com.noslen.training_tracker.dto.day.response.ExerciseSetResponse;
import com.noslen.training_tracker.service.day.ExerciseSetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exercise-set logging during a workout: add a set, update logged weight/reps, remove a set.
 * The parent day-exercise id travels in the create request body ({@code dayExerciseId}).
 */
@RestController
@RequestMapping("/api/sets")
public class ExerciseSetController {

    private final ExerciseSetService exerciseSetService;

    public ExerciseSetController(ExerciseSetService exerciseSetService) {
        this.exerciseSetService = exerciseSetService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExerciseSetResponse createExerciseSet(@Valid @RequestBody CreateExerciseSetRequest request) {
        return exerciseSetService.createExerciseSet(request);
    }

    @PatchMapping("/{id}")
    public ExerciseSetResponse updateExerciseSet(@PathVariable Long id,
                                                 @RequestBody UpdateExerciseSetRequest request) {
        return exerciseSetService.updateExerciseSet(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExerciseSet(@PathVariable Long id) {
        exerciseSetService.deleteExerciseSet(id);
    }
}
