package com.noslen.training_tracker.service.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.response.MesoTemplateResponse;

import java.util.List;

/**
 * Service interface for MesoTemplate operations
 */
public interface MesoTemplateService {

    /**
     * Create a new MesoTemplate
     */
    MesoTemplateResponse createMesoTemplate(MesoTemplateResponse mesoTemplateResponse);

    /**
     * Get MesoTemplate by ID
     */
    MesoTemplateResponse getMesoTemplate(Long id);

    /**
     * Get all MesoTemplates for a specific user
     */
    List<MesoTemplateResponse> getMesoTemplatesByUserId(Long userId);

    /**
     * Update an existing MesoTemplate
     */
    MesoTemplateResponse updateMesoTemplate(Long id, MesoTemplateResponse mesoTemplateResponse);

    /**
     * Delete a MesoTemplate (soft delete by setting deletedAt)
     */
    void deleteMesoTemplate(Long id);

    /**
     * Get all MesoTemplates
     */
    List<MesoTemplateResponse> getAllMesoTemplates();

    /**
     * Get all active MesoTemplates (not deleted)
     */
    List<MesoTemplateResponse> getAllActiveMesoTemplates();
}
