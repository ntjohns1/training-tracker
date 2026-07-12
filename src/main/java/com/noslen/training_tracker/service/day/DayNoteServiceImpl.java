package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.dto.day.response.DayNoteResponse;
import com.noslen.training_tracker.mapper.day.DayNoteMapper;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.day.DayNote;
import com.noslen.training_tracker.repository.day.DayNoteRepo;
import com.noslen.training_tracker.security.UserContext;
import jakarta.persistence.EntityManager;
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

    private final EntityManager entityManager;
    private final DayNoteRepo repo;
    private final DayNoteMapper mapper;
    private final UserContext userContext;

    public DayNoteServiceImpl(EntityManager entityManager, DayNoteRepo repo, DayNoteMapper mapper, UserContext userContext) {
        this.entityManager = entityManager;
        this.repo = repo;
        this.mapper = mapper;
        this.userContext = userContext;
    }

    @Override
    public DayNoteResponse createDayNote(DayNoteResponse dayNoteResponse) {
        if (dayNoteResponse == null) {
            throw new IllegalArgumentException("DayNoteResponse cannot be null");
        }

        // Note: User access validation for create operations should be handled
        // at the controller level since day notes are typically created as part of day operations

        Instant now = Instant.now();
        DayNote dayNote = new DayNote();
        dayNote.setDay(dayNoteResponse.dayId() != null
                ? entityManager.getReference(Day.class, dayNoteResponse.dayId())
                : null);
        dayNote.setNoteId(dayNoteResponse.noteId());
        dayNote.setPinned(dayNoteResponse.pinned());
        dayNote.setText(dayNoteResponse.text());
        dayNote.setCreatedAt(now);
        dayNote.setUpdatedAt(now);

        // Save entity and convert back to payload
        return mapper.toPayload(repo.save(dayNote));
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

        DayNote existing = dayNoteOptional.get();

        // Validate that the current user owns the mesocycle this day note belongs to
        userContext.validateUserAccess(existing.getDay().getMesocycle().getUserId());

        if (dayNoteResponse.dayId() != null) {
            existing.setDay(entityManager.getReference(Day.class, dayNoteResponse.dayId()));
        }
        if (dayNoteResponse.noteId() != null) {
            existing.setNoteId(dayNoteResponse.noteId());
        }
        if (dayNoteResponse.pinned() != null) {
            existing.setPinned(dayNoteResponse.pinned());
        }
        if (dayNoteResponse.text() != null) {
            existing.setText(dayNoteResponse.text());
        }

        existing.setUpdatedAt(Instant.now());

        // Save updated entity and convert back to payload
        return mapper.toPayload(repo.save(existing));
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
