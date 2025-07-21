package com.noslen.training_tracker.service.day;

import java.util.List;

import com.noslen.training_tracker.dto.day.DayPayload;

public interface DayService {

    DayPayload createDay(DayPayload dayPayload);
    DayPayload updateDay(Long id, DayPayload dayPayload);
    DayPayload getDay(Long id);
    void deleteDay(Long id);
    List<DayPayload> getDaysByMesocycleId(Long mesocycleId);

}
