package com.noslen.training_tracker.repository.mesocycle;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noslen.training_tracker.model.mesocycle.MesoTemplate;
import java.util.List;

public interface MesoTemplateRepo extends JpaRepository<MesoTemplate, Long> {

    /**
     * Find all MesoTemplates by user ID
     */
    List<MesoTemplate> findByUserId(Long userId);

    /**
     * Find all active MesoTemplates (not deleted)
     */
    List<MesoTemplate> findByDeletedAtIsNull();

    /**
     * Find all active MesoTemplates for a specific user
     */
    List<MesoTemplate> findByUserIdAndDeletedAtIsNull(Long userId);
}
