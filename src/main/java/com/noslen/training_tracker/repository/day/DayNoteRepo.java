package com.noslen.training_tracker.repository.day;
import org.springframework.data.jpa.repository.JpaRepository;
import com.noslen.training_tracker.model.day.DayNote;

public interface DayNoteRepo extends JpaRepository<DayNote, Long> {

}
