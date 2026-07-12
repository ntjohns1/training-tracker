package com.noslen.training_tracker.service.day;

import java.util.List;

import com.noslen.training_tracker.dto.day.request.CreateDayRequest;
import com.noslen.training_tracker.dto.day.request.FinishDayRequest;
import com.noslen.training_tracker.dto.day.response.DayResponse;

public interface DayService {

//    TODO: update method signature to accept DayRequest instead of DayResponse
    DayResponse createDay(CreateDayRequest dayRequest);
//    TODO: update method signature to accept DayRequest instead of DayResponse
    DayResponse updateDay(Long id, DayResponse dayResponse);
    DayResponse getDay(Long id);
    void deleteDay(Long id);
    List<DayResponse> getDaysByMesocycleId(Long mesocycleId);
    DayResponse getNextDayWithSameMuscleGroup(Long dayId, Long muscleGroupId);
    
    /**
     * Completes a day and triggers progression calculations for the next week.
     * This method should be called when a user finishes their workout and provides feedback.
     * Publishes a DayCompletedEvent (after commit) that drives next-week programming.
     *
     * @param dayId            The ID of the day being completed
     * @param finishDayRequest The finish-day payload carrying the workout feedback
     * @return The updated DayResponse with completed status
     */
    DayResponse completeDay(Long dayId, FinishDayRequest finishDayRequest);
}
