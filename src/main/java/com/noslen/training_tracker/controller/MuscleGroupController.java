package com.noslen.training_tracker.controller;

import com.noslen.training_tracker.dto.progression.response.MuscleGroupResponse;
import com.noslen.training_tracker.service.progression.MuscleGroupService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Read access to the muscle-group reference data.
 */
@RestController
@RequestMapping("/api/muscle-groups")
public class MuscleGroupController {

    private final MuscleGroupService muscleGroupService;

    public MuscleGroupController(MuscleGroupService muscleGroupService) {
        this.muscleGroupService = muscleGroupService;
    }

    @GetMapping
    public List<MuscleGroupResponse> getMuscleGroups() {
        return muscleGroupService.getAllMuscleGroups();
    }
}
