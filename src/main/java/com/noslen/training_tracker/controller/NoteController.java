package com.noslen.training_tracker.controller;

import com.noslen.training_tracker.dto.day.request.CreateDayNoteRequest;
import com.noslen.training_tracker.dto.day.request.UpdateDayNoteRequest;
import com.noslen.training_tracker.dto.day.response.DayNoteResponse;
import com.noslen.training_tracker.dto.exercise.request.CreateExerciseNoteRequest;
import com.noslen.training_tracker.dto.exercise.request.UpdateExerciseNoteRequest;
import com.noslen.training_tracker.dto.exercise.response.ExerciseNoteResponse;
import com.noslen.training_tracker.dto.mesocycle.request.CreateMesoNoteRequest;
import com.noslen.training_tracker.dto.mesocycle.request.UpdateMesoNoteRequest;
import com.noslen.training_tracker.dto.mesocycle.response.MesoNoteResponse;
import com.noslen.training_tracker.service.day.DayNoteService;
import com.noslen.training_tracker.service.exercise.ExerciseNoteService;
import com.noslen.training_tracker.service.mesocycle.MesoNoteService;
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
 * Notes on mesocycles, days, and exercises. Create requests carry their parent id in the body.
 */
@RestController
@RequestMapping("/api")
public class NoteController {

    private final MesoNoteService mesoNoteService;
    private final DayNoteService dayNoteService;
    private final ExerciseNoteService exerciseNoteService;

    public NoteController(MesoNoteService mesoNoteService, DayNoteService dayNoteService,
                          ExerciseNoteService exerciseNoteService) {
        this.mesoNoteService = mesoNoteService;
        this.dayNoteService = dayNoteService;
        this.exerciseNoteService = exerciseNoteService;
    }

    // ---- Mesocycle notes ----

    @GetMapping("/mesocycles/{mesoId}/notes")
    public List<MesoNoteResponse> getMesoNotes(@PathVariable Long mesoId) {
        return mesoNoteService.getMesoNotesByMesoId(mesoId);
    }

    @PostMapping("/meso-notes")
    @ResponseStatus(HttpStatus.CREATED)
    public MesoNoteResponse createMesoNote(@RequestBody CreateMesoNoteRequest request) {
        return mesoNoteService.createMesoNote(request);
    }

    @PatchMapping("/meso-notes/{id}")
    public MesoNoteResponse updateMesoNote(@PathVariable Long id, @RequestBody UpdateMesoNoteRequest request) {
        return mesoNoteService.updateMesoNote(id, request);
    }

    @DeleteMapping("/meso-notes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMesoNote(@PathVariable Long id) {
        mesoNoteService.deleteMesoNote(id);
    }

    // ---- Day notes ----

    @GetMapping("/days/{dayId}/notes")
    public List<DayNoteResponse> getDayNotes(@PathVariable Long dayId) {
        return dayNoteService.getNotesByDayId(dayId);
    }

    @PostMapping("/day-notes")
    @ResponseStatus(HttpStatus.CREATED)
    public DayNoteResponse createDayNote(@RequestBody CreateDayNoteRequest request) {
        return dayNoteService.createDayNote(request);
    }

    @PatchMapping("/day-notes/{id}")
    public DayNoteResponse updateDayNote(@PathVariable Long id, @RequestBody UpdateDayNoteRequest request) {
        return dayNoteService.updateDayNote(id, request);
    }

    // ---- Exercise notes ----

    @PostMapping("/exercise-notes")
    @ResponseStatus(HttpStatus.CREATED)
    public ExerciseNoteResponse createExerciseNote(@RequestBody CreateExerciseNoteRequest request) {
        return exerciseNoteService.createExerciseNote(request);
    }

    @PatchMapping("/exercise-notes/{id}")
    public ExerciseNoteResponse updateExerciseNote(@PathVariable Long id, @RequestBody UpdateExerciseNoteRequest request) {
        return exerciseNoteService.updateExerciseNote(id, request);
    }

    @DeleteMapping("/exercise-notes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExerciseNote(@PathVariable Long id) {
        exerciseNoteService.deleteExerciseNote(id);
    }
}
