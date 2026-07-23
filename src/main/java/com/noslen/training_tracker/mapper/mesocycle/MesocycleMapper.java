package com.noslen.training_tracker.mapper.mesocycle;

import com.noslen.training_tracker.dto.day.response.DayResponse;
import com.noslen.training_tracker.dto.mesocycle.response.CurrentMesoResponse;
import com.noslen.training_tracker.dto.mesocycle.response.MesoNoteResponse;
import com.noslen.training_tracker.dto.mesocycle.response.MesocycleResponse;
import com.noslen.training_tracker.dto.mesocycle.response.Week;
import com.noslen.training_tracker.dto.progression.response.ProgressionResponse;
import com.noslen.training_tracker.enums.Unit;
import com.noslen.training_tracker.mapper.day.DayMapper;
import com.noslen.training_tracker.mapper.progression.ProgressionMapper;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import com.noslen.training_tracker.util.EnumConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Mesocycle entities and MesocycleResponse DTOs
 */
@Component
public class MesocycleMapper {

    private final MesoNoteMapper mesoNoteMapper;
    private final DayMapper dayMapper;
    private final ProgressionMapper progressionMapper;

    public MesocycleMapper(MesoNoteMapper mesoNoteMapper, DayMapper dayMapper,
                           ProgressionMapper progressionMapper) {
        this.mesoNoteMapper = mesoNoteMapper;
        this.dayMapper = dayMapper;
        this.progressionMapper = progressionMapper;
    }

    /**
     * Converts a Mesocycle entity to the full nested CurrentMesoResponse: its flat day list is
     * grouped by week number (ordered by week, then day position) into {@link Week}s, and its
     * progressions are mapped to response DTOs. This is the payload the workout/dashboard UI
     * renders (mirrors RP's current_meso structure).
     */
    public CurrentMesoResponse toCurrentMeso(Mesocycle entity) {
        if (entity == null) {
            return null;
        }

        Map<Integer, List<DayResponse>> daysByWeek = new LinkedHashMap<>();
        if (entity.getWeeks() != null) {
            entity.getWeeks().stream()
                    .sorted(Comparator.comparing(Day::getWeek, Comparator.nullsLast(Comparator.naturalOrder()))
                            .thenComparing(Day::getPosition, Comparator.nullsLast(Comparator.naturalOrder())))
                    .forEach(day -> daysByWeek
                            .computeIfAbsent(day.getWeek(), k -> new ArrayList<>())
                            .add(dayMapper.toPayload(day)));
        }
        List<Week> weeks = daysByWeek.values().stream().map(Week::new).collect(Collectors.toList());

        Map<Long, ProgressionResponse> progressions = new LinkedHashMap<>();
        if (entity.getProgressions() != null) {
            entity.getProgressions().forEach((key, value) ->
                    progressions.put(key, progressionMapper.toPayload(value)));
        }

        Set<MesoNoteResponse> notes = entity.getNotes() != null
                ? entity.getNotes().stream().map(mesoNoteMapper::toPayload)
                    .collect(Collectors.toCollection(LinkedHashSet::new))
                : new LinkedHashSet<>();

        return new CurrentMesoResponse(
                entity.getId(),
                entity.getMesocycleKey(),
                entity.getUserId(),
                entity.getName(),
                entity.getDays(),
                unitToString(entity.getUnit()),
                entity.getSourceTemplateId(),
                entity.getSourceMesoId(),
                entity.getMicroRirs(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getFinishedAt(),
                entity.getDeletedAt(),
                entity.getFirstMicroCompletedAt(),
                entity.getFirstWorkoutCompletedAt(),
                entity.getFirstExerciseCompletedAt(),
                entity.getFirstSetCompletedAt(),
                entity.getLastMicroFinishedAt(),
                entity.getLastSetCompletedAt(),
                entity.getLastSetSkippedAt(),
                entity.getLastWorkoutCompletedAt(),
                entity.getLastWorkoutFinishedAt(),
                entity.getLastWorkoutSkippedAt(),
                entity.getLastWorkoutPartialedAt(),
                weeks,
                notes,
                EnumConverter.enumToString(entity.getStatus()),
                entity.getGeneratedFrom(),
                progressions);
    }

    /**
     * Converts Mesocycle entity to MesocycleResponse DTO
     */
    public MesocycleResponse toPayload(Mesocycle entity) {
        if (entity == null) {
            return null;
        }

        return new MesocycleResponse(
                entity.getId(),
                entity.getMesocycleKey(),
                entity.getUserId(),
                entity.getName(),
                entity.getDays(),
                unitToString(entity.getUnit()),
                entity.getSourceTemplateId(), // Uses the helper method
                entity.getSourceMesoId(), // Uses the helper method
                entity.getMicroRirs(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getFinishedAt(),
                entity.getDeletedAt(),
                entity.getFirstMicroCompletedAt(),
                entity.getFirstWorkoutCompletedAt(),
                entity.getFirstExerciseCompletedAt(),
                entity.getFirstSetCompletedAt(),
                entity.getLastMicroFinishedAt(),
                entity.getLastSetCompletedAt(),
                entity.getLastSetSkippedAt(),
                entity.getLastWorkoutCompletedAt(),
                entity.getLastWorkoutFinishedAt(),
                entity.getLastWorkoutSkippedAt(),
                entity.getLastWorkoutPartialedAt(),
                entity.getWeeks() != null ? entity.getWeeks().size() : null, // Convert List<Day> to count
                entity.getNotes() != null ? 
                    entity.getNotes().stream()
                        .map(mesoNoteMapper::toPayload)
                        .collect(Collectors.toList()) : Collections.emptyList()
        );
    }

    public String unitToString(Unit unit) {
        if (unit == null) {
            return null;
        }
        return EnumConverter.enumToSerializedValue(unit);
    }
}
