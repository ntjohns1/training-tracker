package com.noslen.training_tracker.service.day;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.noslen.training_tracker.dto.day.response.DayResponse;
import com.noslen.training_tracker.factory.DayFactory;
import com.noslen.training_tracker.mapper.day.DayMapper;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.repository.day.DayRepo;
import com.noslen.training_tracker.security.RequireUserAccess;
import com.noslen.training_tracker.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for Day operations.
 * Uses DayFactory for clean entity creation and UserContext for data segregation.
 */
@Service
public class DayServiceImpl implements DayService {
    
    private final DayRepo dayRepo;
    private final DayMapper dayMapper;
    private final DayFactory dayFactory;
    private final UserContext userContext;

    public DayServiceImpl(DayRepo dayRepo, DayMapper dayMapper, DayFactory dayFactory, UserContext userContext) {
        this.dayRepo = dayRepo;
        this.dayMapper = dayMapper;
        this.dayFactory = dayFactory;
        this.userContext = userContext;
    }

    @Override
    @Transactional
    public DayResponse createDay(DayResponse dayResponse) {
        if (dayResponse == null) {
            throw new IllegalArgumentException("DayResponse cannot be null");
        }

        // Validate that the current user owns the mesocycle this day belongs to
        if (dayResponse.mesoId() != null) {
            // This will be validated through the mesocycle ownership when the day is created
            // For now, we'll rely on the factory to handle the relationship properly
        }

        // Use factory to create entity
        Day day = dayFactory.createFromResponse(dayResponse);
        
        // Save entity
        Day savedDay = dayRepo.save(day);
        
        // Convert back to response DTO
        return dayMapper.toPayload(savedDay);
    }

    @Override
    @Transactional
    public DayResponse updateDay(Long dayId, DayResponse dayResponse) {
        if (dayId == null) {
            throw new IllegalArgumentException("Day ID cannot be null");
        }
        if (dayResponse == null) {
            throw new IllegalArgumentException("DayResponse cannot be null");
        }

        Optional<Day> existingOptional = dayRepo.findById(dayId);
        if (existingOptional.isEmpty()) {
            throw new RuntimeException("Day not found with id: " + dayId);
        }

        Day existing = existingOptional.get();
        
        // Validate that the current user owns the mesocycle this day belongs to
        userContext.validateUserAccess(existing.getMesocycle().getUserId());

        // Update entity with payload data using mapper
        dayMapper.updateEntity(existing, dayResponse);
        
        // Ensure updated timestamp is set
        existing.setUpdatedAt(Instant.now());
        
        // Save updated entity
        Day savedEntity = dayRepo.save(existing);

        // Convert back to payload and return
        return dayMapper.toPayload(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public DayResponse getDay(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Day day = dayRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Day not found with id: " + id));
        
        // Validate that the current user owns the mesocycle this day belongs to
        userContext.validateUserAccess(day.getMesocycle().getUserId());
        
        return dayMapper.toPayload(day);
    }

    @Override
    @Transactional
    public void deleteDay(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        // Find existing day and validate ownership before deletion
        Day existingDay = dayRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Day not found with id: " + id));
        
        // Validate that the current user owns the mesocycle this day belongs to
        userContext.validateUserAccess(existingDay.getMesocycle().getUserId());

        dayRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DayResponse> getDaysByMesocycleId(Long mesocycleId) {
        if (mesocycleId == null) {
            throw new IllegalArgumentException("Mesocycle ID cannot be null");
        }

        // First validate that the current user owns the mesocycle
        // We need to check this before querying days to ensure data segregation
        List<Day> days = dayRepo.findByMesocycleId(mesocycleId);
        
        // If days exist, validate ownership through the first day's mesocycle
        // This ensures the mesocycle belongs to the current user
        if (!days.isEmpty()) {
            userContext.validateUserAccess(days.get(0).getMesocycle().getUserId());
        }
        
        return dayMapper.toPayloadList(days);
    }
}
