package com.noslen.training_tracker.repository.day;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.noslen.training_tracker.model.day.DayNote;

public interface DayNoteRepo extends JpaRepository<DayNote, Long> {
    List<DayNote> findByDayId(Long dayId) ;
}
