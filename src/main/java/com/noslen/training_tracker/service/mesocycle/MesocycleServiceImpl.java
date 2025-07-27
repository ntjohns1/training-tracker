package com.noslen.training_tracker.service.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.MesocycleResponse;
import com.noslen.training_tracker.mapper.mesocycle.MesocycleMapper;
import com.noslen.training_tracker.mapper.mesocycle.MesoNoteMapper;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import com.noslen.training_tracker.repository.mesocycle.MesocycleRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Mesocycle operations
 */
@Service
@Transactional
public class MesocycleServiceImpl implements MesocycleService {

    private final MesocycleRepo mesocycleRepo;
    private final MesocycleMapper mesocycleMapper;
    private final MesoNoteMapper mesoNoteMapper;

    public MesocycleServiceImpl(MesocycleRepo mesocycleRepo, MesocycleMapper mesocycleMapper, MesoNoteMapper mesoNoteMapper) {
        this.mesocycleRepo = mesocycleRepo;
        this.mesocycleMapper = mesocycleMapper;
        this.mesoNoteMapper = mesoNoteMapper;
    }

    @Override
    public MesocycleResponse createMesocycle(MesocycleResponse mesocycleResponse) {
        // Convert payload to entity using mapper, then enhance with timestamps
        Mesocycle baseMesocycle = mesocycleMapper.toEntity(mesocycleResponse);
        
        // Set timestamps and initialize fields for new mesocycle
        Instant now = Instant.now();
        Mesocycle mesocycle = Mesocycle.builder()
                .id(baseMesocycle.getId())
                .mesocycleKey(baseMesocycle.getMesocycleKey())
                .userId(baseMesocycle.getUserId())
                .name(baseMesocycle.getName())
                .days(baseMesocycle.getDays())
                .unit(baseMesocycle.getUnit())
                .sourceTemplate(baseMesocycle.getSourceTemplate())
                .sourceMeso(baseMesocycle.getSourceMeso())
                .microRirs(baseMesocycle.getMicroRirs())
                .createdAt(now)
                .updatedAt(now)
                .finishedAt(null) // New mesocycles are not finished
                .deletedAt(null) // New mesocycles are not deleted
                .firstMicroCompletedAt(null)
                .firstWorkoutCompletedAt(null)
                .firstExerciseCompletedAt(null)
                .firstSetCompletedAt(null)
                .lastMicroFinishedAt(null)
                .lastSetCompletedAt(null)
                .lastSetSkippedAt(null)
                .lastWorkoutCompletedAt(null)
                .lastWorkoutFinishedAt(null)
                .lastWorkoutSkippedAt(null)
                .lastWorkoutPartialedAt(null)
                .weeks(baseMesocycle.getWeeks())
                .notes(baseMesocycle.getNotes())
                .status(null) // Will be set based on business logic
                .generatedFrom(null) // Will be set based on business logic
                .progressions(baseMesocycle.getProgressions())
                .build();

        // Save entity
        Mesocycle savedMesocycle = mesocycleRepo.save(mesocycle);
        
        // Convert back to payload and return
        return mesocycleMapper.toPayload(savedMesocycle);
    }

    @Override
    @Transactional(readOnly = true)
    public MesocycleResponse getMesocycle(Long id) {
        Mesocycle mesocycle = mesocycleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesocycle not found with id: " + id));
        
        return mesocycleMapper.toPayload(mesocycle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesocycleResponse> getMesocyclesByUserId(Long userId) {
        List<Mesocycle> mesocycles = mesocycleRepo.findByUserId(userId);
        
        return mesocycles.stream()
                .map(mesocycleMapper::toPayload)
                .collect(Collectors.toList());
    }

    @Override
    public MesocycleResponse updateMesocycle(Long id, MesocycleResponse mesocycleResponse) {
        // Find existing entity
        Mesocycle existingMesocycle = mesocycleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesocycle not found with id: " + id));

        // Since Mesocycle is immutable, create a new entity with updated values
        Mesocycle updatedMesocycle = mesocycleMapper.mergeEntity(existingMesocycle,
                                                                 mesocycleResponse);
        
        // Save the updated entity
        Mesocycle savedMesocycle = mesocycleRepo.save(updatedMesocycle);
        
        // Convert back to payload and return
        return mesocycleMapper.toPayload(savedMesocycle);
    }

    @Override
    public void deleteMesocycle(Long id) {
        // Find existing entity
        Mesocycle existingMesocycle = mesocycleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesocycle not found with id: " + id));

        // Perform soft delete by setting deletedAt timestamp
        Mesocycle deletedMesocycle = Mesocycle.builder()
                .id(existingMesocycle.getId())
                .mesocycleKey(existingMesocycle.getMesocycleKey())
                .userId(existingMesocycle.getUserId())
                .name(existingMesocycle.getName())
                .days(existingMesocycle.getDays())
                .unit(existingMesocycle.getUnit())
                .sourceTemplate(existingMesocycle.getSourceTemplate())
                .sourceMeso(existingMesocycle.getSourceMeso())
                .microRirs(existingMesocycle.getMicroRirs())
                .createdAt(existingMesocycle.getCreatedAt())
                .updatedAt(Instant.now())
                .finishedAt(existingMesocycle.getFinishedAt())
                .deletedAt(Instant.now()) // Set deletion timestamp
                .firstMicroCompletedAt(existingMesocycle.getFirstMicroCompletedAt())
                .firstWorkoutCompletedAt(existingMesocycle.getFirstWorkoutCompletedAt())
                .firstExerciseCompletedAt(existingMesocycle.getFirstExerciseCompletedAt())
                .firstSetCompletedAt(existingMesocycle.getFirstSetCompletedAt())
                .lastMicroFinishedAt(existingMesocycle.getLastMicroFinishedAt())
                .lastSetCompletedAt(existingMesocycle.getLastSetCompletedAt())
                .lastSetSkippedAt(existingMesocycle.getLastSetSkippedAt())
                .lastWorkoutCompletedAt(existingMesocycle.getLastWorkoutCompletedAt())
                .lastWorkoutFinishedAt(existingMesocycle.getLastWorkoutFinishedAt())
                .lastWorkoutSkippedAt(existingMesocycle.getLastWorkoutSkippedAt())
                .lastWorkoutPartialedAt(existingMesocycle.getLastWorkoutPartialedAt())
                .weeks(existingMesocycle.getWeeks())
                .notes(existingMesocycle.getNotes())
                .status(existingMesocycle.getStatus())
                .generatedFrom(existingMesocycle.getGeneratedFrom())
                .progressions(existingMesocycle.getProgressions())
                .build();
        
        mesocycleRepo.save(deletedMesocycle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesocycleResponse> getAllMesocycles() {
        List<Mesocycle> mesocycles = mesocycleRepo.findAll();
        
        return mesocycles.stream()
                .map(mesocycleMapper::toPayload)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesocycleResponse> getAllActiveMesocycles() {
        List<Mesocycle> activeMesocycles = mesocycleRepo.findByDeletedAtIsNull();
        
        return activeMesocycles.stream()
                .map(mesocycleMapper::toPayload)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesocycleResponse> getActiveMesocyclesByUserId(Long userId) {
        List<Mesocycle> activeMesocycles = mesocycleRepo.findByUserIdAndDeletedAtIsNull(userId);
        
        return activeMesocycles.stream()
                .map(mesocycleMapper::toPayload)
                .collect(Collectors.toList());
    }

    @Override
    public MesocycleResponse finishMesocycle(Long id) {
        // Find existing entity
        Mesocycle existingMesocycle = mesocycleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesocycle not found with id: " + id));

        // Create finished mesocycle by setting finishedAt timestamp
        Mesocycle finishedMesocycle = Mesocycle.builder()
                .id(existingMesocycle.getId())
                .mesocycleKey(existingMesocycle.getMesocycleKey())
                .userId(existingMesocycle.getUserId())
                .name(existingMesocycle.getName())
                .days(existingMesocycle.getDays())
                .unit(existingMesocycle.getUnit())
                .sourceTemplate(existingMesocycle.getSourceTemplate())
                .sourceMeso(existingMesocycle.getSourceMeso())
                .microRirs(existingMesocycle.getMicroRirs())
                .createdAt(existingMesocycle.getCreatedAt())
                .updatedAt(Instant.now())
                .finishedAt(Instant.now()) // Set finish timestamp
                .deletedAt(existingMesocycle.getDeletedAt())
                .firstMicroCompletedAt(existingMesocycle.getFirstMicroCompletedAt())
                .firstWorkoutCompletedAt(existingMesocycle.getFirstWorkoutCompletedAt())
                .firstExerciseCompletedAt(existingMesocycle.getFirstExerciseCompletedAt())
                .firstSetCompletedAt(existingMesocycle.getFirstSetCompletedAt())
                .lastMicroFinishedAt(existingMesocycle.getLastMicroFinishedAt())
                .lastSetCompletedAt(existingMesocycle.getLastSetCompletedAt())
                .lastSetSkippedAt(existingMesocycle.getLastSetSkippedAt())
                .lastWorkoutCompletedAt(existingMesocycle.getLastWorkoutCompletedAt())
                .lastWorkoutFinishedAt(existingMesocycle.getLastWorkoutFinishedAt())
                .lastWorkoutSkippedAt(existingMesocycle.getLastWorkoutSkippedAt())
                .lastWorkoutPartialedAt(existingMesocycle.getLastWorkoutPartialedAt())
                .weeks(existingMesocycle.getWeeks())
                .notes(existingMesocycle.getNotes())
                .status(existingMesocycle.getStatus())
                .generatedFrom(existingMesocycle.getGeneratedFrom())
                .progressions(existingMesocycle.getProgressions())
                .build();
        
        // Save the finished mesocycle
        Mesocycle savedMesocycle = mesocycleRepo.save(finishedMesocycle);
        
        // Convert back to payload and return
        return mesocycleMapper.toPayload(savedMesocycle);
    }
}
