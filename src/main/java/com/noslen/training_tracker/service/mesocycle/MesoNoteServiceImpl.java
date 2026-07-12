package com.noslen.training_tracker.service.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.response.MesoNoteResponse;
import com.noslen.training_tracker.mapper.mesocycle.MesoNoteMapper;
import com.noslen.training_tracker.model.mesocycle.MesoNote;
import com.noslen.training_tracker.repository.mesocycle.MesoNoteRepo;
import com.noslen.training_tracker.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for MesoNote operations.
 * Includes user data segregation through UserContext validation.
 */
@Service
@Transactional
public class MesoNoteServiceImpl implements MesoNoteService {

    private final MesoNoteRepo repo;
    private final MesoNoteMapper mapper;
    private final UserContext userContext;

    public MesoNoteServiceImpl(MesoNoteRepo repo, MesoNoteMapper mapper, UserContext userContext) {
        this.repo = repo;
        this.mapper = mapper;
        this.userContext = userContext;
    }

    @Override
    public MesoNoteResponse createMesoNote(MesoNoteResponse mesoNoteResponse) {
        if (mesoNoteResponse == null) {
            throw new IllegalArgumentException("MesoNoteResponse cannot be null");
        }

        // Convert payload to entity
        MesoNote mesoNote = mapper.toEntity(mesoNoteResponse);
        
        // Note: User access validation for create operations should be handled 
        // at the controller level since meso notes are typically created as part of mesocycle operations
        
        // Set timestamps
        Instant now = Instant.now();
        if (mesoNote.getCreatedAt() == null) {
            mesoNote.setCreatedAt(now);
        }
        mesoNote.setUpdatedAt(now);

        // Save entity
        MesoNote savedMesoNote = repo.save(mesoNote);
        
        // Convert back to payload and return
        return mapper.toPayload(savedMesoNote);
    }

    @Override
    @Transactional(readOnly = true)
    public MesoNoteResponse getMesoNote(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        MesoNote mesoNote = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("MesoNote not found with id: " + id));
        
        // Validate that the current user owns the mesocycle this meso note belongs to
        userContext.validateUserAccess(mesoNote.getMesocycle().getUserId());
        
        return mapper.toPayload(mesoNote);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesoNoteResponse> getMesoNotesByMesoId(Long mesoId) {
        if (mesoId == null) {
            throw new IllegalArgumentException("Mesocycle ID cannot be null");
        }

        List<MesoNote> mesoNotes = repo.findByMesocycle_Id(mesoId);
        
        // Validate user access for the first meso note (they should all belong to the same mesocycle)
        if (!mesoNotes.isEmpty()) {
            userContext.validateUserAccess(mesoNotes.get(0).getMesocycle().getUserId());
        }
        
        return mesoNotes.stream()
                .map(mapper::toPayload)
                .collect(Collectors.toList());
    }

    @Override
    public MesoNoteResponse updateMesoNote(Long id, MesoNoteResponse mesoNoteResponse) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (mesoNoteResponse == null) {
            throw new IllegalArgumentException("MesoNoteResponse cannot be null");
        }

        // Find existing entity
        MesoNote existingMesoNote = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("MesoNote not found with id: " + id));

        // Validate that the current user owns the mesocycle this meso note belongs to
        userContext.validateUserAccess(existingMesoNote.getMesocycle().getUserId());

        // Since MesoNote is immutable, create a new entity with updated values
        MesoNote updatedMesoNote = mapper.mergeEntity(existingMesoNote, mesoNoteResponse);
        
        // Save the updated entity
        MesoNote savedMesoNote = repo.save(updatedMesoNote);
        
        // Convert back to payload and return
        return mapper.toPayload(savedMesoNote);
    }

    @Override
    public void deleteMesoNote(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        MesoNote mesoNote = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("MesoNote not found with id: " + id));

        // Validate that the current user owns the mesocycle this meso note belongs to
        userContext.validateUserAccess(mesoNote.getMesocycle().getUserId());
        
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesoNoteResponse> getAllMesoNotes() {
        List<MesoNote> mesoNotes = repo.findAll();
        
        // Filter meso notes to only include those owned by the current user
        return mesoNotes.stream()
                .filter(mesoNote -> {
                    try {
                        userContext.validateUserAccess(mesoNote.getMesocycle().getUserId());
                        return true;
                    } catch (SecurityException e) {
                        return false; // Skip notes the user doesn't own
                    }
                })
                .map(mapper::toPayload)
                .collect(Collectors.toList());
    }
}
