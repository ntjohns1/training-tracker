package com.noslen.training_tracker.repository.mesocycle;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noslen.training_tracker.model.mesocycle.Mesocycle;

import java.util.List;

public interface MesocycleRepo extends JpaRepository<Mesocycle, Long> {

    /**
     * Find all mesocycles for a specific user
     * @param userId the user ID
     * @return list of mesocycles
     */
    List<Mesocycle> findByUserId(Long userId);

    /**
     * Find all active (non-deleted) mesocycles
     * @return list of active mesocycles
     */
    List<Mesocycle> findByDeletedAtIsNull();

    /**
     * Find all active mesocycles for a specific user
     * @param userId the user ID
     * @return list of active mesocycles for the user
     */
    List<Mesocycle> findByUserIdAndDeletedAtIsNull(Long userId);
}
