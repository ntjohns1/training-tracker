package com.noslen.training_tracker.controller;

import com.noslen.training_tracker.dto.day.request.CreateDayExerciseRequest;
import com.noslen.training_tracker.dto.day.request.UpdateDayExerciseRequest;
import com.noslen.training_tracker.dto.day.response.DayExerciseResponse;
import com.noslen.training_tracker.service.day.DayExerciseService;
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
 * Add, swap (update), and remove exercises on a day. The parent day id travels in the request body
 * ({@code dayId}).
 */
@RestController
@RequestMapping("/api/day-exercises")
public class DayExerciseController {

    private final DayExerciseService dayExerciseService;

    public DayExerciseController(DayExerciseService dayExerciseService) {
        this.dayExerciseService = dayExerciseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DayExerciseResponse createDayExercise(@Valid @RequestBody CreateDayExerciseRequest request) {
        return dayExerciseService.createDayExercise(request);
    }

    @PatchMapping("/{id}")
    public DayExerciseResponse updateDayExercise(@PathVariable Long id,
                                                 @RequestBody UpdateDayExerciseRequest request) {
        return dayExerciseService.updateDayExercise(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDayExercise(@PathVariable Long id) {
        dayExerciseService.deleteDayExercise(id);
    }
}
