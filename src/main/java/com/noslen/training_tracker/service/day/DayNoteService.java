package com.noslen.training_tracker.service.day;

import java.util.List;

import com.noslen.training_tracker.model.day.DayNote;

public interface DayNoteService {

    DayNote createDayNote(DayNote dayNote);
    DayNote updateDayNote(Long id, DayNote dayNote);
    DayNote getDayNote(Long id);
    List<DayNote> getNotesByDayId(Long dayId);
}
