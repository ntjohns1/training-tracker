package com.noslen.training_tracker.service.day;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.noslen.training_tracker.dto.day.response.DayResponse;
import com.noslen.training_tracker.factory.DayFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noslen.training_tracker.mapper.day.DayMapper;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.repository.day.DayRepo;

@Service
public class DayServiceImpl implements DayService {
    
    private final DayRepo dayRepo;
    private final DayMapper dayMapper;
    private final DayFactory dayFactory;

    public DayServiceImpl(DayRepo dayRepo, DayMapper dayMapper, DayFactory dayFactory) {
        this.dayRepo = dayRepo;
        this.dayMapper = dayMapper;
        this.dayFactory = dayFactory;
    }

    @Override
    @Transactional
    public DayResponse createDay(DayResponse dayResponse) {
        if (dayResponse == null) {
            throw new IllegalArgumentException("DayResponse cannot be null");
        }

        // Use factory to create properly initialized entity
        Day day = dayFactory.createFromResponse(dayResponse);
        
        // Save and return as DTO
        Day savedDay = dayRepo.save(day);
        return dayMapper.toPayload(savedDay);
    }

    @Override
    @Transactional
    public DayResponse updateDay(Long id, DayResponse dayResponse) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (dayResponse == null) {
            throw new IllegalArgumentException("DayResponse cannot be null");
        }

        // Find existing entity
        Optional<Day> existingOpt = dayRepo.findById(id);
        if (existingOpt.isEmpty()) {
            throw new RuntimeException("Day not found with id: " + id);
        }

        Day existingDay = existingOpt.get();
        
        // Update entity with payload data using merge since most fields are immutable
        Day updatedDay = dayMapper.mergeEntity(existingDay, dayResponse);
        updatedDay.setUpdatedAt(Instant.now());
        
        // Save and return as DTO
        Day savedDay = dayRepo.save(updatedDay);
        return dayMapper.toPayload(savedDay);
    }

    @Override
    @Transactional(readOnly = true)
    public DayResponse getDay(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<Day> dayOpt = dayRepo.findById(id);
        if (dayOpt.isEmpty()) {
            throw new RuntimeException("Day not found with id: " + id);
        }

        return dayMapper.toPayload(dayOpt.get());
    }

    @Override
    @Transactional
    public void deleteDay(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        if (!dayRepo.existsById(id)) {
            throw new RuntimeException("Day not found with id: " + id);
        }

        dayRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DayResponse> getDaysByMesocycleId(Long mesocycleId) {
        if (mesocycleId == null) {
            throw new IllegalArgumentException("Mesocycle ID cannot be null");
        }

        List<Day> days = dayRepo.findByMesocycleId(mesocycleId);
        return dayMapper.toPayloadList(days);
    }
}
