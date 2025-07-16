package com.noslen.training_tracker.service.day;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.noslen.training_tracker.model.day.DayNote;
import com.noslen.training_tracker.repository.day.DayNoteRepo;

@Service
public class DayNoteServiceImpl implements DayNoteService {

    private final DayNoteRepo repo;

    public DayNoteServiceImpl(DayNoteRepo dayNoteRepo) {
        this.repo = dayNoteRepo;
    }

    @Override
    public DayNote createDayNote(DayNote dayNote) {
        dayNote.setCreatedAt(Instant.now());
        dayNote.setUpdatedAt(Instant.now());
        return this.repo.save(dayNote);
    }

    @Override
    public DayNote updateDayNote(Long id, DayNote dayNote) {
        Optional<DayNote> dayNoteToUpdate = this.repo.findById(id);
        if (dayNoteToUpdate.isPresent()) {
            dayNoteToUpdate.get().setText(dayNote.getText());
            dayNoteToUpdate.get().setUpdatedAt(Instant.now());
            this.repo.save(dayNoteToUpdate.get());
        }
        return dayNoteToUpdate.orElse(null);
    }

    @Override
    public DayNote getDayNote(Long id) {
        Optional<DayNote> dayNote = this.repo.findById(id);
        return dayNote.orElse(null);
    }

    @Override
    public List<DayNote> getNotesByDayId(Long dayId) {
        Optional<List<DayNote>> dayNotes = Optional.ofNullable(this.repo.findByDayId(dayId));
        return dayNotes.orElse(null);
    }
}
