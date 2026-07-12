package com.noslen.training_tracker.service.day;

import java.util.List;

import com.noslen.training_tracker.dto.day.response.DayNoteResponse;

public interface DayNoteService {

    DayNoteResponse createDayNote(DayNoteResponse dayNoteResponse);
    DayNoteResponse updateDayNote(Long id, DayNoteResponse dayNoteResponse);
    DayNoteResponse getDayNote(Long id);
    List<DayNoteResponse> getNotesByDayId(Long dayId);
}
