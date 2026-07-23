package com.noslen.training_tracker.service.day;

import java.util.List;

import com.noslen.training_tracker.dto.day.request.CreateDayNoteRequest;
import com.noslen.training_tracker.dto.day.request.UpdateDayNoteRequest;
import com.noslen.training_tracker.dto.day.response.DayNoteResponse;

public interface DayNoteService {

    DayNoteResponse createDayNote(CreateDayNoteRequest request);
    DayNoteResponse updateDayNote(Long id, UpdateDayNoteRequest request);
    DayNoteResponse getDayNote(Long id);
    List<DayNoteResponse> getNotesByDayId(Long dayId);
}
