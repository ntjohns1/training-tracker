package com.noslen.training_tracker.repository.day;

import java.util.List;

import com.noslen.training_tracker.model.day.Day;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DayRepo extends JpaRepository<Day, Long> {
    List<Day> findByMesocycleId(Long mesocycleId);  
    
}
