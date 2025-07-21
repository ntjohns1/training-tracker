package com.noslen.training_tracker.service.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.MesoTemplatePayload;
import com.noslen.training_tracker.mapper.mesocycle.MesoTemplateMapper;
import com.noslen.training_tracker.model.mesocycle.MesoTemplate;
import com.noslen.training_tracker.repository.mesocycle.MesoTemplateRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for MesoTemplate operations
 */
@Service
@Transactional
public class MesoTemplateServiceImpl implements MesoTemplateService {

    private final MesoTemplateRepo mesoTemplateRepo;
    private final MesoTemplateMapper mesoTemplateMapper;

    @Autowired
    public MesoTemplateServiceImpl(MesoTemplateRepo mesoTemplateRepo, MesoTemplateMapper mesoTemplateMapper) {
        this.mesoTemplateRepo = mesoTemplateRepo;
        this.mesoTemplateMapper = mesoTemplateMapper;
    }

    @Override
    public MesoTemplatePayload createMesoTemplate(MesoTemplatePayload mesoTemplatePayload) {
        // Convert payload to entity
        MesoTemplate mesoTemplate = mesoTemplateMapper.toEntity(mesoTemplatePayload);
        
        // Set timestamps
        Instant now = Instant.now();
        mesoTemplate = MesoTemplate.builder()
                .id(mesoTemplate.getId())
                .templateKey(mesoTemplate.getTemplateKey())
                .name(mesoTemplate.getName())
                .emphasis(mesoTemplate.getEmphasis())
                .sex(mesoTemplate.getSex())
                .userId(mesoTemplate.getUserId())
                .frequency(mesoTemplate.getFrequency())
                .sourceTemplate(mesoTemplate.getSourceTemplate())
                .sourceMeso(mesoTemplate.getSourceMeso())
                .prevTemplate(mesoTemplate.getPrevTemplate())
                .createdAt(now)
                .updatedAt(now)
                .deletedAt(null) // New templates are not deleted
                .build();

        // Save entity
        MesoTemplate savedMesoTemplate = mesoTemplateRepo.save(mesoTemplate);
        
        // Convert back to payload and return
        return mesoTemplateMapper.toPayload(savedMesoTemplate);
    }

    @Override
    @Transactional(readOnly = true)
    public MesoTemplatePayload getMesoTemplate(Long id) {
        MesoTemplate mesoTemplate = mesoTemplateRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("MesoTemplate not found with id: " + id));
        
        return mesoTemplateMapper.toPayload(mesoTemplate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesoTemplatePayload> getMesoTemplatesByUserId(Long userId) {
        List<MesoTemplate> mesoTemplates = mesoTemplateRepo.findByUserId(userId);
        
        return mesoTemplates.stream()
                .map(mesoTemplateMapper::toPayload)
                .collect(Collectors.toList());
    }

    @Override
    public MesoTemplatePayload updateMesoTemplate(Long id, MesoTemplatePayload mesoTemplatePayload) {
        // Find existing entity
        MesoTemplate existingMesoTemplate = mesoTemplateRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("MesoTemplate not found with id: " + id));

        // Since MesoTemplate is immutable, create a new entity with updated values
        MesoTemplate updatedMesoTemplate = mesoTemplateMapper.mergeEntity(existingMesoTemplate, mesoTemplatePayload);
        
        // Save the updated entity
        MesoTemplate savedMesoTemplate = mesoTemplateRepo.save(updatedMesoTemplate);
        
        // Convert back to payload and return
        return mesoTemplateMapper.toPayload(savedMesoTemplate);
    }

    @Override
    public void deleteMesoTemplate(Long id) {
        // Find existing entity
        MesoTemplate existingMesoTemplate = mesoTemplateRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("MesoTemplate not found with id: " + id));

        // Perform soft delete by setting deletedAt timestamp
        MesoTemplate deletedMesoTemplate = MesoTemplate.builder()
                .id(existingMesoTemplate.getId())
                .templateKey(existingMesoTemplate.getTemplateKey())
                .name(existingMesoTemplate.getName())
                .emphasis(existingMesoTemplate.getEmphasis())
                .sex(existingMesoTemplate.getSex())
                .userId(existingMesoTemplate.getUserId())
                .frequency(existingMesoTemplate.getFrequency())
                .sourceTemplate(existingMesoTemplate.getSourceTemplate())
                .sourceMeso(existingMesoTemplate.getSourceMeso())
                .prevTemplate(existingMesoTemplate.getPrevTemplate())
                .createdAt(existingMesoTemplate.getCreatedAt())
                .updatedAt(Instant.now())
                .deletedAt(Instant.now()) // Set deletion timestamp
                .build();
        
        mesoTemplateRepo.save(deletedMesoTemplate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesoTemplatePayload> getAllMesoTemplates() {
        List<MesoTemplate> mesoTemplates = mesoTemplateRepo.findAll();
        
        return mesoTemplates.stream()
                .map(mesoTemplateMapper::toPayload)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesoTemplatePayload> getAllActiveMesoTemplates() {
        List<MesoTemplate> activeMesoTemplates = mesoTemplateRepo.findByDeletedAtIsNull();
        
        return activeMesoTemplates.stream()
                .map(mesoTemplateMapper::toPayload)
                .collect(Collectors.toList());
    }
}
