package com.noslen.training_tracker.service.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.MesocycleResponse;

import java.util.List;

/**
 * Service interface for Mesocycle operations
 */
public interface MesocycleService {

    /**
     * Creates a new mesocycle
     * @param mesocycleResponse the mesocycle data to create
     * @return the created mesocycle as DTO
     */
    MesocycleResponse createMesocycle(MesocycleResponse mesocycleResponse);

    /**
     * Retrieves a mesocycle by its ID
     * @param id the mesocycle ID
     * @return the mesocycle as DTO
     * @throws RuntimeException if mesocycle not found
     */
    MesocycleResponse getMesocycle(Long id);

    /**
     * Retrieves all mesocycles for a specific user
     * @param userId the user ID
     * @return list of mesocycles as DTOs
     */
    List<MesocycleResponse> getMesocyclesByUserId(Long userId);

    /**
     * Updates an existing mesocycle
     * @param id the mesocycle ID to update
     * @param mesocycleResponse the updated mesocycle data
     * @return the updated mesocycle as DTO
     * @throws RuntimeException if mesocycle not found
     */
    MesocycleResponse updateMesocycle(Long id, MesocycleResponse mesocycleResponse);

    /**
     * Soft deletes a mesocycle by setting deletedAt timestamp
     * @param id the mesocycle ID to delete
     * @throws RuntimeException if mesocycle not found
     */
    void deleteMesocycle(Long id);

    /**
     * Retrieves all mesocycles
     * @return list of all mesocycles as DTOs
     */
    List<MesocycleResponse> getAllMesocycles();

    /**
     * Retrieves all active (non-deleted) mesocycles
     * @return list of active mesocycles as DTOs
     */
    List<MesocycleResponse> getAllActiveMesocycles();

    /**
     * Retrieves all active mesocycles for a specific user
     * @param userId the user ID
     * @return list of active mesocycles as DTOs
     */
    List<MesocycleResponse> getActiveMesocyclesByUserId(Long userId);

    /**
     * Finishes a mesocycle by setting finishedAt timestamp
     * @param id the mesocycle ID to finish
     * @return the finished mesocycle as DTO
     * @throws RuntimeException if mesocycle not found
     */
    MesocycleResponse finishMesocycle(Long id);
}
