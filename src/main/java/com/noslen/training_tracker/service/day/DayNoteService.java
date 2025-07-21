package com.noslen.training_tracker.service.day;

import java.util.List;

import com.noslen.training_tracker.dto.day.DayNotePayload;

public interface DayNoteService {

    DayNotePayload createDayNote(DayNotePayload dayNotePayload);
    DayNotePayload updateDayNote(Long id, DayNotePayload dayNotePayload);
    DayNotePayload getDayNote(Long id);
    List<DayNotePayload> getNotesByDayId(Long dayId);
}
