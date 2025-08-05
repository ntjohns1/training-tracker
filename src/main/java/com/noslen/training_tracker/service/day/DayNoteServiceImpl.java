package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.dto.day.response.DayNoteResponse;
import com.noslen.training_tracker.mapper.day.DayNoteMapper;
import com.noslen.training_tracker.model.day.DayNote;
import com.noslen.training_tracker.repository.day.DayNoteRepo;
import com.noslen.training_tracker.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for DayNote operations.
 * Includes user data segregation through UserContext validation.
 */
@Service
@Transactional
public class DayNoteServiceImpl implements DayNoteService {

    private final DayNoteRepo repo;
    private final DayNoteMapper mapper;
    private final UserContext userContext;

    public DayNoteServiceImpl(DayNoteRepo repo, DayNoteMapper mapper, UserContext userContext) {
        this.repo = repo;
        this.mapper = mapper;
        this.userContext = userContext;
    }

    @Override
    public DayNoteResponse createDayNote(DayNoteResponse dayNoteResponse) {
        if (dayNoteResponse == null) {
            throw new IllegalArgumentException("DayNoteResponse cannot be null");
        }

        // Convert payload to entity
        DayNote dayNote = mapper.toEntity(dayNoteResponse);
        
        // Note: User access validation for create operations should be handled 
        // at the controller level since day notes are typically created as part of day operations
        
        // Set timestamps
        dayNote.setCreatedAt(Instant.now());
        dayNote.setUpdatedAt(Instant.now());
        
        // Save entity
        DayNote savedDayNote = repo.save(dayNote);
        
        // Convert back to payload and return
        return mapper.toPayload(savedDayNote);
    }

    @Override
    public DayNoteResponse updateDayNote(Long id, DayNoteResponse dayNoteResponse) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (dayNoteResponse == null) {
            throw new IllegalArgumentException("DayNoteResponse cannot be null");
        }

        Optional<DayNote> dayNoteOptional = repo.findById(id);
        if (dayNoteOptional.isEmpty()) {
            throw new RuntimeException("DayNote not found with id: " + id);
        }

        DayNote existingDayNote = dayNoteOptional.get();
        
        // Validate that the current user owns the mesocycle this day note belongs to
        userContext.validateUserAccess(existingDayNote.getDay().getMesocycle().getUserId());
        
        // Update entity with payload data
        mapper.updateEntity(existingDayNote, dayNoteResponse);
        
        // Update timestamp
        existingDayNote.setUpdatedAt(Instant.now());
        
        // Save updated entity
        DayNote savedDayNote = repo.save(existingDayNote);
        
        // Convert back to payload and return
        return mapper.toPayload(savedDayNote);
    }

    @Override
    @Transactional(readOnly = true)
    public DayNoteResponse getDayNote(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<DayNote> dayNoteOptional = repo.findById(id);
        if (dayNoteOptional.isEmpty()) {
            throw new RuntimeException("DayNote not found with id: " + id);
        }

        DayNote dayNote = dayNoteOptional.get();
        
        // Validate that the current user owns the mesocycle this day note belongs to
        userContext.validateUserAccess(dayNote.getDay().getMesocycle().getUserId());
        
        return mapper.toPayload(dayNote);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DayNoteResponse> getNotesByDayId(Long dayId) {
        if (dayId == null) {
            throw new IllegalArgumentException("Day ID cannot be null");
        }

        List<DayNote> dayNotes = repo.findByDay_Id(dayId);
        
        // Validate user access for the first day note (they should all belong to the same day/mesocycle)
        if (!dayNotes.isEmpty()) {
            userContext.validateUserAccess(dayNotes.get(0).getDay().getMesocycle().getUserId());
        }
        
        return mapper.toPayloadList(dayNotes);
    }

    public void deleteDayNote(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<DayNote> dayNoteOptional = repo.findById(id);
        if (dayNoteOptional.isEmpty()) {
            throw new RuntimeException("DayNote not found with id: " + id);
        }

        DayNote dayNote = dayNoteOptional.get();
        
        // Validate that the current user owns the mesocycle this day note belongs to
        userContext.validateUserAccess(dayNote.getDay().getMesocycle().getUserId());
        
        repo.deleteById(id);
    }
}
