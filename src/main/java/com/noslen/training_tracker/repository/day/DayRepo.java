package com.noslen.training_tracker.repository.day;

import java.util.List;
import java.util.Optional;

import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DayRepo extends JpaRepository<Day, Long> {
    List<Day> findByMesocycleId(Long mesocycleId);
    
    Optional<Day> findByMesocycleAndWeekAndPosition(Mesocycle mesocycle, Integer week, Integer position);
}
