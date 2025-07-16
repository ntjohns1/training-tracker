package com.noslen.training_tracker.service.day;

import java.util.List;

import com.noslen.training_tracker.model.day.Day;

public interface DayService {

    Day createDay(Day day);
    Day updateDay(Long id, Day day);
    Day getDay(Long id);
    void deleteDay(Long id);
    List<Day> getDaysByMesocycleId(Long mesocycleId);

}
