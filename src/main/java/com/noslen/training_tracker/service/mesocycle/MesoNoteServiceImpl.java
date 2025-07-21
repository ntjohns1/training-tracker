package com.noslen.training_tracker.service.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.MesoNotePayload;
import com.noslen.training_tracker.mapper.mesocycle.MesoNoteMapper;
import com.noslen.training_tracker.model.mesocycle.MesoNote;
import com.noslen.training_tracker.repository.mesocycle.MesoNoteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for MesoNote operations
 */
@Service
@Transactional
public class MesoNoteServiceImpl implements MesoNoteService {

    private final MesoNoteRepo mesoNoteRepo;
    private final MesoNoteMapper mesoNoteMapper;

    @Autowired
    public MesoNoteServiceImpl(MesoNoteRepo mesoNoteRepo, MesoNoteMapper mesoNoteMapper) {
        this.mesoNoteRepo = mesoNoteRepo;
        this.mesoNoteMapper = mesoNoteMapper;
    }

    @Override
    public MesoNotePayload createMesoNote(MesoNotePayload mesoNotePayload) {
        // Convert payload to entity
        MesoNote mesoNote = mesoNoteMapper.toEntity(mesoNotePayload);
        
        // Set timestamps
        Instant now = Instant.now();
        mesoNote = MesoNote.builder()
                .id(mesoNote.getId())
                .mesocycle(mesoNote.getMesocycle())
                .userId(mesoNote.getUserId())
                .noteId(mesoNote.getNoteId())
                .text(mesoNote.getText())
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Save entity
        MesoNote savedMesoNote = mesoNoteRepo.save(mesoNote);
        
        // Convert back to payload and return
        return mesoNoteMapper.toPayload(savedMesoNote);
    }

    @Override
    @Transactional(readOnly = true)
    public MesoNotePayload getMesoNote(Long id) {
        MesoNote mesoNote = mesoNoteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("MesoNote not found with id: " + id));
        
        return mesoNoteMapper.toPayload(mesoNote);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesoNotePayload> getMesoNotesByMesoId(Long mesoId) {
        List<MesoNote> mesoNotes = mesoNoteRepo.findByMesocycle_Id(mesoId);
        
        return mesoNotes.stream()
                .map(mesoNoteMapper::toPayload)
                .collect(Collectors.toList());
    }

    @Override
    public MesoNotePayload updateMesoNote(Long id, MesoNotePayload mesoNotePayload) {
        // Find existing entity
        MesoNote existingMesoNote = mesoNoteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("MesoNote not found with id: " + id));

        // Since MesoNote is immutable, create a new entity with updated values
        MesoNote updatedMesoNote = mesoNoteMapper.mergeEntity(existingMesoNote, mesoNotePayload);
        
        // Save the updated entity
        MesoNote savedMesoNote = mesoNoteRepo.save(updatedMesoNote);
        
        // Convert back to payload and return
        return mesoNoteMapper.toPayload(savedMesoNote);
    }

    @Override
    public void deleteMesoNote(Long id) {
        if (!mesoNoteRepo.existsById(id)) {
            throw new RuntimeException("MesoNote not found with id: " + id);
        }
        
        mesoNoteRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesoNotePayload> getAllMesoNotes() {
        List<MesoNote> mesoNotes = mesoNoteRepo.findAll();
        
        return mesoNotes.stream()
                .map(mesoNoteMapper::toPayload)
                .collect(Collectors.toList());
    }
}
