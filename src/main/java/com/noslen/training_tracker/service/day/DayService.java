package com.noslen.training_tracker.service.day;

import java.util.List;

import com.noslen.training_tracker.dto.day.DayResponse;

public interface DayService {

    DayResponse createDay(DayResponse dayResponse);
    DayResponse updateDay(Long id, DayResponse dayResponse);
    DayResponse getDay(Long id);
    void deleteDay(Long id);
    List<DayResponse> getDaysByMesocycleId(Long mesocycleId);

}
