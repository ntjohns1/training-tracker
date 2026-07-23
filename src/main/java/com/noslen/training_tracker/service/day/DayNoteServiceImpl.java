package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.dto.day.request.CreateDayNoteRequest;
import com.noslen.training_tracker.dto.day.request.UpdateDayNoteRequest;
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
    public DayNoteResponse createDayNote(CreateDayNoteRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("CreateDayNoteRequest cannot be null");
        }

        // Note: User access validation for create operations should be handled
        // at the controller level since day notes are typically created as part of day operations

        Instant now = Instant.now();
        DayNote dayNote = new DayNote();
        dayNote.setDay(request.dayId() != null
                ? entityManager.getReference(Day.class, request.dayId())
                : null);
        dayNote.setNoteId(request.noteId());
        dayNote.setPinned(request.pinned());
        dayNote.setText(request.text());
        dayNote.setCreatedAt(now);
        dayNote.setUpdatedAt(now);

        // Save entity and convert back to payload
        return mapper.toPayload(repo.save(dayNote));
    }

    @Override
    public DayNoteResponse updateDayNote(Long id, UpdateDayNoteRequest request) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (request == null) {
            throw new IllegalArgumentException("UpdateDayNoteRequest cannot be null");
        }

        DayNote existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("DayNote not found with id: " + id));

        // Validate that the current user owns the mesocycle this day note belongs to
        userContext.validateUserAccess(existing.getDay().getMesocycle().getUserId());

        if (request.pinned() != null) {
            existing.setPinned(request.pinned());
        }
        if (request.text() != null) {
            existing.setText(request.text());
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
