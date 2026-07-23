package com.noslen.training_tracker.service.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.request.CreateMesoNoteRequest;
import com.noslen.training_tracker.dto.mesocycle.request.UpdateMesoNoteRequest;
import com.noslen.training_tracker.dto.mesocycle.response.MesoNoteResponse;
import com.noslen.training_tracker.mapper.mesocycle.MesoNoteMapper;
import com.noslen.training_tracker.model.mesocycle.MesoNote;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import com.noslen.training_tracker.repository.mesocycle.MesoNoteRepo;
import com.noslen.training_tracker.security.UserContext;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for MesoNote operations.
 * Includes user data segregation through UserContext validation.
 */
@Service
@Transactional
public class MesoNoteServiceImpl implements MesoNoteService {

    private final EntityManager entityManager;
    private final MesoNoteRepo repo;
    private final MesoNoteMapper mapper;
    private final UserContext userContext;

    public MesoNoteServiceImpl(EntityManager entityManager, MesoNoteRepo repo, MesoNoteMapper mapper, UserContext userContext) {
        this.entityManager = entityManager;
        this.repo = repo;
        this.mapper = mapper;
        this.userContext = userContext;
    }

    @Override
    public MesoNoteResponse createMesoNote(CreateMesoNoteRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("CreateMesoNoteRequest cannot be null");
        }

        // Note: user access for create is handled at the controller level; meso notes are
        // created as part of mesocycle operations.
        Instant now = Instant.now();
        MesoNote mesoNote = new MesoNote();
        mesoNote.setMesocycle(request.mesoId() != null
                ? entityManager.getReference(Mesocycle.class, request.mesoId())
                : null);
        mesoNote.setNoteId(request.noteId());
        mesoNote.setText(request.text());
        mesoNote.setCreatedAt(now);
        mesoNote.setUpdatedAt(now);

        return mapper.toPayload(repo.save(mesoNote));
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
    public MesoNoteResponse updateMesoNote(Long id, UpdateMesoNoteRequest request) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (request == null) {
            throw new IllegalArgumentException("UpdateMesoNoteRequest cannot be null");
        }

        MesoNote existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("MesoNote not found with id: " + id));

        // Validate that the current user owns the mesocycle this meso note belongs to
        userContext.validateUserAccess(existing.getMesocycle().getUserId());

        if (request.text() != null) {
            existing.setText(request.text());
        }
        existing.setUpdatedAt(Instant.now());

        return mapper.toPayload(repo.save(existing));
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
