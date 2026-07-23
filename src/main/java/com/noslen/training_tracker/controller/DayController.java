package com.noslen.training_tracker.controller;

import com.noslen.training_tracker.dto.day.request.FinishDayRequest;
import com.noslen.training_tracker.dto.day.request.UpdateDayRequest;
import com.noslen.training_tracker.dto.day.response.DayResponse;
import com.noslen.training_tracker.service.day.DayService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Day access and the finish-workout action. Finishing a day persists completion and publishes
 * the DayCompletedEvent that (after commit) drives next-week progression via Kafka.
 */
@RestController
@RequestMapping("/api/days")
public class DayController {

    private final DayService dayService;

    public DayController(DayService dayService) {
        this.dayService = dayService;
    }

    @GetMapping("/{id}")
    public DayResponse getDay(@PathVariable Long id) {
        return dayService.getDay(id);
    }

    @PatchMapping("/{id}")
    public DayResponse updateDay(@PathVariable Long id, @RequestBody UpdateDayRequest request) {
        return dayService.updateDay(id, request);
    }

    @PutMapping("/{id}/finish")
    public DayResponse finishDay(@PathVariable Long id, @RequestBody FinishDayRequest request) {
        return dayService.completeDay(id, request);
    }
}
