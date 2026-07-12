package com.noslen.training_tracker.service.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.response.MesocycleResponse;
import com.noslen.training_tracker.factory.MesocycleFactory;
import com.noslen.training_tracker.mapper.mesocycle.MesocycleMapper;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import com.noslen.training_tracker.repository.mesocycle.MesocycleRepo;
import com.noslen.training_tracker.security.RequireUserAccess;
import com.noslen.training_tracker.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Mesocycle operations.
 * Uses MesocycleFactory for clean entity creation without redundant instantiation.
 */
@Service
@Transactional
public class MesocycleServiceImpl implements MesocycleService {

    private final MesocycleRepo mesocycleRepo;
    private final MesocycleMapper mesocycleMapper;
    private final MesocycleFactory mesocycleFactory;
    private final UserContext userContext;

    public MesocycleServiceImpl(MesocycleRepo mesocycleRepo, MesocycleMapper mesocycleMapper, MesocycleFactory mesocycleFactory, UserContext userContext) {
        this.mesocycleRepo = mesocycleRepo;
        this.mesocycleMapper = mesocycleMapper;
        this.mesocycleFactory = mesocycleFactory;
        this.userContext = userContext;
    }

    @Override
    public MesocycleResponse createMesocycle(MesocycleResponse mesocycleResponse) {
        // Use factory to create entity directly from DTO - no redundant instantiation
        Mesocycle mesocycle = mesocycleFactory.createFromResponse(mesocycleResponse);
        
        // Save entity
        Mesocycle savedMesocycle = mesocycleRepo.save(mesocycle);
        
        // Convert back to response DTO
        return mesocycleMapper.toPayload(savedMesocycle);
    }

    @Override
    @Transactional(readOnly = true)
    public MesocycleResponse getMesocycle(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Mesocycle ID cannot be null");
        }

        Mesocycle mesocycle = mesocycleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesocycle not found with id: " + id));
        
        // Validate that the current user owns this mesocycle
        userContext.validateUserAccess(mesocycle.getUserId());
        
        return mesocycleMapper.toPayload(mesocycle);
    }

    @Override
    @Transactional(readOnly = true)
    @RequireUserAccess
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
        
        // Use mapper for merge operation (existing functionality)
        Mesocycle updatedMesocycle = mesocycleMapper.mergeEntity(existingMesocycle, mesocycleResponse);
        
        // Save updated entity
        Mesocycle savedMesocycle = mesocycleRepo.save(updatedMesocycle);
        
        // Convert back to response DTO
        return mesocycleMapper.toPayload(savedMesocycle);
    }



    @Override
    public void deleteMesocycle(Long id) {
        // Find existing entity
        Mesocycle existingMesocycle = mesocycleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesocycle not found with id: " + id));
        
        // Use factory to create soft-deleted entity
        Mesocycle deletedMesocycle = mesocycleFactory.createForSoftDelete(existingMesocycle);
        
        // Save the soft-deleted entity
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
    @RequireUserAccess
    public List<MesocycleResponse> getActiveMesocyclesByUserId(Long userId) {
        List<Mesocycle> activeMesocycles = mesocycleRepo.findByUserIdAndDeletedAtIsNull(userId);
        
        return activeMesocycles.stream()
                .map(mesocycleMapper::toPayload)
                .collect(Collectors.toList());
    }

    @Override
    public MesocycleResponse finishMesocycle(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Mesocycle ID cannot be null");
        }

        // Find existing entity
        Mesocycle existingMesocycle = mesocycleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesocycle not found with id: " + id));
        
        // Validate that the current user owns this mesocycle
        userContext.validateUserAccess(existingMesocycle.getUserId());
        
        // Use factory to create finished entity
        Mesocycle finishedMesocycle = mesocycleFactory.createForFinish(existingMesocycle);
        
        // Save the finished entity
        Mesocycle savedMesocycle = mesocycleRepo.save(finishedMesocycle);
        
        // Convert back to response DTO
        return mesocycleMapper.toPayload(savedMesocycle);
    }
}
