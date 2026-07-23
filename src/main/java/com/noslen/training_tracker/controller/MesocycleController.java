package com.noslen.training_tracker.controller;

import com.noslen.training_tracker.dto.mesocycle.request.CreateMesocycleRequest;
import com.noslen.training_tracker.dto.mesocycle.request.UpdateMesocycleRequest;
import com.noslen.training_tracker.dto.mesocycle.response.CurrentMesoResponse;
import com.noslen.training_tracker.dto.mesocycle.response.MesocycleResponse;
import com.noslen.training_tracker.security.UserContext;
import com.noslen.training_tracker.service.mesocycle.MesocycleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Mesocycle lifecycle: list the current user's mesocycles, fetch one (full nested structure),
 * create from the builder, complete, and soft-delete.
 */
@RestController
@RequestMapping("/api/mesocycles")
public class MesocycleController {

    private final MesocycleService mesocycleService;
    private final UserContext userContext;

    public MesocycleController(MesocycleService mesocycleService, UserContext userContext) {
        this.mesocycleService = mesocycleService;
        this.userContext = userContext;
    }

    @GetMapping
    public List<MesocycleResponse> getMesocycles() {
        return mesocycleService.getMesocyclesByUserId(userContext.getCurrentUserId());
    }

    @GetMapping("/{id}")
    public CurrentMesoResponse getMesocycle(@PathVariable Long id) {
        return mesocycleService.getCurrentMeso(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MesocycleResponse createMesocycle(@Valid @RequestBody CreateMesocycleRequest request) {
        return mesocycleService.createMesocycle(request);
    }

    @PatchMapping("/{id}")
    public MesocycleResponse updateMesocycle(@PathVariable Long id,
                                             @RequestBody UpdateMesocycleRequest request) {
        return mesocycleService.updateMesocycle(id, request);
    }

    @PostMapping("/{id}/complete")
    public MesocycleResponse completeMesocycle(@PathVariable Long id) {
        return mesocycleService.finishMesocycle(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMesocycle(@PathVariable Long id) {
        mesocycleService.deleteMesocycle(id);
    }
}
