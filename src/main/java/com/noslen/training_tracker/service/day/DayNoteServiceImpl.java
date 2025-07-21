package com.noslen.training_tracker.service.day;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noslen.training_tracker.dto.day.DayNotePayload;
import com.noslen.training_tracker.mapper.day.DayNoteMapper;
import com.noslen.training_tracker.model.day.DayNote;
import com.noslen.training_tracker.repository.day.DayNoteRepo;

@Service
public class DayNoteServiceImpl implements DayNoteService {

    private final DayNoteRepo repo;
    private final DayNoteMapper mapper;

    public DayNoteServiceImpl(DayNoteRepo dayNoteRepo, DayNoteMapper mapper) {
        this.repo = dayNoteRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public DayNotePayload createDayNote(DayNotePayload dayNotePayload) {
        if (dayNotePayload == null) {
            throw new IllegalArgumentException("DayNotePayload cannot be null");
        }

        // Convert payload to entity
        DayNote dayNote = mapper.toEntity(dayNotePayload);
        
        // Set timestamps
        dayNote.setCreatedAt(Instant.now());
        dayNote.setUpdatedAt(Instant.now());
        
        // Save and return as DTO
        DayNote savedDayNote = repo.save(dayNote);
        return mapper.toPayload(savedDayNote);
    }

    @Override
    @Transactional
    public DayNotePayload updateDayNote(Long id, DayNotePayload dayNotePayload) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (dayNotePayload == null) {
            throw new IllegalArgumentException("DayNotePayload cannot be null");
        }

        Optional<DayNote> dayNoteOptional = repo.findById(id);
        if (dayNoteOptional.isEmpty()) {
            throw new RuntimeException("DayNote not found with id: " + id);
        }

        DayNote existingDayNote = dayNoteOptional.get();
        
        // Update entity with payload data
        mapper.updateEntity(existingDayNote, dayNotePayload);
        existingDayNote.setUpdatedAt(Instant.now());
        
        // Save and return as DTO
        DayNote updatedDayNote = repo.save(existingDayNote);
        return mapper.toPayload(updatedDayNote);
    }

    @Override
    @Transactional(readOnly = true)
    public DayNotePayload getDayNote(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<DayNote> dayNoteOptional = repo.findById(id);
        if (dayNoteOptional.isEmpty()) {
            throw new RuntimeException("DayNote not found with id: " + id);
        }

        return mapper.toPayload(dayNoteOptional.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DayNotePayload> getNotesByDayId(Long dayId) {
        if (dayId == null) {
            throw new IllegalArgumentException("Day ID cannot be null");
        }

        List<DayNote> dayNotes = repo.findByDay_Id(dayId);
        return mapper.toPayloadList(dayNotes);
    }
}
