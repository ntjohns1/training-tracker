package com.noslen.training_tracker.service.day;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noslen.training_tracker.dto.day.DayPayload;
import com.noslen.training_tracker.mapper.day.DayMapper;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import com.noslen.training_tracker.repository.day.DayRepo;
import com.noslen.training_tracker.repository.mesocycle.MesocycleRepo;

@Service
public class DayServiceImpl implements DayService {
    
    private final DayRepo repo;
    private final MesocycleRepo mesocycleRepo;
    private final DayMapper mapper;

    public DayServiceImpl(DayRepo repo, MesocycleRepo mesocycleRepo, DayMapper mapper) {
        this.repo = repo;
        this.mesocycleRepo = mesocycleRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public DayPayload createDay(DayPayload dayPayload) {
        if (dayPayload == null) {
            throw new IllegalArgumentException("DayPayload cannot be null");
        }

        // Convert payload to entity
        Day day = mapper.toEntity(dayPayload);
        
        // Handle mesocycle relationship if mesoId is provided
        if (dayPayload.mesoId() != null) {
            Optional<Mesocycle> mesocycleOpt = mesocycleRepo.findById(dayPayload.mesoId());
            if (mesocycleOpt.isEmpty()) {
                throw new RuntimeException("Mesocycle not found with id: " + dayPayload.mesoId());
            }
            // Rebuild entity with mesocycle relationship
            day = Day.builder()
                    .id(day.getId())
                    .mesocycle(mesocycleOpt.get())
                    .week(day.getWeek())
                    .position(day.getPosition())
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .bodyweight(day.getBodyweight())
                    .bodyweightAt(day.getBodyweightAt())
                    .unit(day.getUnit())
                    .finishedAt(day.getFinishedAt())
                    .label(day.getLabel())
                    .build();
        } else {
            // Set timestamps for new entity
            day.setCreatedAt(Instant.now());
            day.setUpdatedAt(Instant.now());
        }

        // Save and return as DTO
        Day savedDay = repo.save(day);
        return mapper.toPayload(savedDay);
    }

    @Override
    @Transactional
    public DayPayload updateDay(Long id, DayPayload dayPayload) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (dayPayload == null) {
            throw new IllegalArgumentException("DayPayload cannot be null");
        }

        // Find existing entity
        Optional<Day> existingOpt = repo.findById(id);
        if (existingOpt.isEmpty()) {
            throw new RuntimeException("Day not found with id: " + id);
        }

        Day existingDay = existingOpt.get();
        
        // Update entity with payload data using merge since most fields are immutable
        Day updatedDay = mapper.mergeEntity(existingDay, dayPayload);
        updatedDay.setUpdatedAt(Instant.now());
        
        // Save and return as DTO
        Day savedDay = repo.save(updatedDay);
        return mapper.toPayload(savedDay);
    }

    @Override
    @Transactional(readOnly = true)
    public DayPayload getDay(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<Day> dayOpt = repo.findById(id);
        if (dayOpt.isEmpty()) {
            throw new RuntimeException("Day not found with id: " + id);
        }

        return mapper.toPayload(dayOpt.get());
    }

    @Override
    @Transactional
    public void deleteDay(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        if (!repo.existsById(id)) {
            throw new RuntimeException("Day not found with id: " + id);
        }

        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DayPayload> getDaysByMesocycleId(Long mesocycleId) {
        if (mesocycleId == null) {
            throw new IllegalArgumentException("Mesocycle ID cannot be null");
        }

        List<Day> days = repo.findByMesocycleId(mesocycleId);
        return mapper.toPayloadList(days);
    }
}
