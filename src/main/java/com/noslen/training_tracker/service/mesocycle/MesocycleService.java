package com.noslen.training_tracker.service.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.MesocyclePayload;

import java.util.List;

/**
 * Service interface for Mesocycle operations
 */
public interface MesocycleService {

    /**
     * Creates a new mesocycle
     * @param mesocyclePayload the mesocycle data to create
     * @return the created mesocycle as DTO
     */
    MesocyclePayload createMesocycle(MesocyclePayload mesocyclePayload);

    /**
     * Retrieves a mesocycle by its ID
     * @param id the mesocycle ID
     * @return the mesocycle as DTO
     * @throws RuntimeException if mesocycle not found
     */
    MesocyclePayload getMesocycle(Long id);

    /**
     * Retrieves all mesocycles for a specific user
     * @param userId the user ID
     * @return list of mesocycles as DTOs
     */
    List<MesocyclePayload> getMesocyclesByUserId(Long userId);

    /**
     * Updates an existing mesocycle
     * @param id the mesocycle ID to update
     * @param mesocyclePayload the updated mesocycle data
     * @return the updated mesocycle as DTO
     * @throws RuntimeException if mesocycle not found
     */
    MesocyclePayload updateMesocycle(Long id, MesocyclePayload mesocyclePayload);

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
    List<MesocyclePayload> getAllMesocycles();

    /**
     * Retrieves all active (non-deleted) mesocycles
     * @return list of active mesocycles as DTOs
     */
    List<MesocyclePayload> getAllActiveMesocycles();

    /**
     * Retrieves all active mesocycles for a specific user
     * @param userId the user ID
     * @return list of active mesocycles as DTOs
     */
    List<MesocyclePayload> getActiveMesocyclesByUserId(Long userId);

    /**
     * Finishes a mesocycle by setting finishedAt timestamp
     * @param id the mesocycle ID to finish
     * @return the finished mesocycle as DTO
     * @throws RuntimeException if mesocycle not found
     */
    MesocyclePayload finishMesocycle(Long id);
}
