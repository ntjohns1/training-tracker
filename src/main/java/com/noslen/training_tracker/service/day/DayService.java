package com.noslen.training_tracker.service.day;

import java.util.List;

import com.noslen.training_tracker.dto.day.request.FinishDayRequest;
import com.noslen.training_tracker.dto.day.response.DayResponse;

public interface DayService {

    DayResponse createDay(DayResponse dayResponse);
    DayResponse updateDay(Long id, DayResponse dayResponse);
    DayResponse getDay(Long id);
    void deleteDay(Long id);
    List<DayResponse> getDaysByMesocycleId(Long mesocycleId);
    DayResponse getNextDayWithSameMuscleGroup(Long dayId, Long muscleGroupId);
    
    /**
     * Completes a day and triggers progression calculations for the next week.
     * This method should be called when a user finishes their workout and provides feedback.
     * 
     * @param dayId The ID of the day being completed
     * @return The updated DayResponse with completed status
     */
    DayResponse completeDay(Long dayId);
}
