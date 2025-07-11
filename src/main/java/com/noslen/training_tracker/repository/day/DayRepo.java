package com.noslen.training_tracker.repository.day;

import com.noslen.training_tracker.model.day.Day;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DayRepo extends JpaRepository<Day, Long> {
    
}
