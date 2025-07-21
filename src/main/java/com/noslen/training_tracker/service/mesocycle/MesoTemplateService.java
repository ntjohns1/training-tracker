package com.noslen.training_tracker.service.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.MesoTemplatePayload;

import java.util.List;

/**
 * Service interface for MesoTemplate operations
 */
public interface MesoTemplateService {

    /**
     * Create a new MesoTemplate
     */
    MesoTemplatePayload createMesoTemplate(MesoTemplatePayload mesoTemplatePayload);

    /**
     * Get MesoTemplate by ID
     */
    MesoTemplatePayload getMesoTemplate(Long id);

    /**
     * Get all MesoTemplates for a specific user
     */
    List<MesoTemplatePayload> getMesoTemplatesByUserId(Long userId);

    /**
     * Update an existing MesoTemplate
     */
    MesoTemplatePayload updateMesoTemplate(Long id, MesoTemplatePayload mesoTemplatePayload);

    /**
     * Delete a MesoTemplate (soft delete by setting deletedAt)
     */
    void deleteMesoTemplate(Long id);

    /**
     * Get all MesoTemplates
     */
    List<MesoTemplatePayload> getAllMesoTemplates();

    /**
     * Get all active MesoTemplates (not deleted)
     */
    List<MesoTemplatePayload> getAllActiveMesoTemplates();
}
