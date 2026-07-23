package com.noslen.training_tracker.controller;

import com.noslen.training_tracker.dto.BootstrapResponse;
import com.noslen.training_tracker.security.UserContext;
import com.noslen.training_tracker.service.exercise.ExerciseService;
import com.noslen.training_tracker.service.mesocycle.MesocycleService;
import com.noslen.training_tracker.service.progression.MuscleGroupService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Single startup call returning everything the client needs to render: the user's mesocycles and
 * the reference data (exercise catalog + muscle groups).
 */
@RestController
@RequestMapping("/api/bootstrap")
public class BootstrapController {

    private final MesocycleService mesocycleService;
    private final ExerciseService exerciseService;
    private final MuscleGroupService muscleGroupService;
    private final UserContext userContext;

    public BootstrapController(MesocycleService mesocycleService, ExerciseService exerciseService,
                               MuscleGroupService muscleGroupService, UserContext userContext) {
        this.mesocycleService = mesocycleService;
        this.exerciseService = exerciseService;
        this.muscleGroupService = muscleGroupService;
        this.userContext = userContext;
    }

    @GetMapping
    public BootstrapResponse bootstrap() {
        return new BootstrapResponse(
                mesocycleService.getMesocyclesByUserId(userContext.getCurrentUserId()),
                exerciseService.getAllExercises(),
                muscleGroupService.getAllMuscleGroups());
    }
}
