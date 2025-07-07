package com.noslen.training_tracker.model.mesocycle;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import com.noslen.training_tracker.model.day.Day;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "current_meso")
public class CurrentMeso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String key;
    private Long userId;
    private String name;
    private Integer days;
    private String unit;
    private Long sourceTemplateId;
    private Long sourceMesoId;
    private Long microRirs;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant finishedAt;
    private Instant deletedAt;
    private Instant firstMicroCompletedAt;
    private Instant firstWorkoutCompletedAt;
    private Instant firstExerciseCompletedAt;
    private Instant firstSetCompletedAt;
    private Instant lastMicroFinishedAt;
    private Instant lastSetCompletedAt;
    private Instant lastSetSkippedAt;
    private Instant lastWorkoutCompletedAt;
    private Instant lastWorkoutFinishedAt;
    private Instant lastWorkoutSkippedAt;
    private Instant lastWorkoutPartialedAt;
    private List<Day> weeks;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrentMeso that = (CurrentMeso) o;

        if (!Objects.equals(id,
                            that.id)) return false;
        if (!Objects.equals(key,
                            that.key)) return false;
        if (!Objects.equals(userId,
                            that.userId)) return false;
        if (!Objects.equals(name,
                            that.name)) return false;
        if (!Objects.equals(days,
                            that.days)) return false;
        if (!Objects.equals(unit,
                            that.unit)) return false;
        if (!Objects.equals(sourceTemplateId,
                            that.sourceTemplateId))
            return false;
        if (!Objects.equals(sourceMesoId,
                            that.sourceMesoId)) return false;
        if (!Objects.equals(microRirs,
                            that.microRirs)) return false;
        if (!Objects.equals(createdAt,
                            that.createdAt)) return false;
        if (!Objects.equals(updatedAt,
                            that.updatedAt)) return false;
        if (!Objects.equals(finishedAt,
                            that.finishedAt)) return false;
        if (!Objects.equals(deletedAt,
                            that.deletedAt)) return false;
        if (!Objects.equals(firstMicroCompletedAt,
                            that.firstMicroCompletedAt))
            return false;
        if (!Objects.equals(firstWorkoutCompletedAt,
                            that.firstWorkoutCompletedAt))
            return false;
        if (!Objects.equals(firstExerciseCompletedAt,
                            that.firstExerciseCompletedAt))
            return false;
        if (!Objects.equals(firstSetCompletedAt,
                            that.firstSetCompletedAt))
            return false;
        if (!Objects.equals(lastMicroFinishedAt,
                            that.lastMicroFinishedAt))
            return false;
        if (!Objects.equals(lastSetCompletedAt,
                            that.lastSetCompletedAt))
            return false;
        if (!Objects.equals(lastSetSkippedAt,
                            that.lastSetSkippedAt))
            return false;
        if (!Objects.equals(lastWorkoutCompletedAt,
                            that.lastWorkoutCompletedAt))
            return false;
        if (!Objects.equals(lastWorkoutFinishedAt,
                            that.lastWorkoutFinishedAt))
            return false;
        if (!Objects.equals(lastWorkoutSkippedAt,
                            that.lastWorkoutSkippedAt))
            return false;
        if (!Objects.equals(lastWorkoutPartialedAt,
                            that.lastWorkoutPartialedAt))
            return false;
        return Objects.equals(weeks,
                              that.weeks);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (days != null ? days.hashCode() : 0);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        result = 31 * result + (sourceTemplateId != null ? sourceTemplateId.hashCode() : 0);
        result = 31 * result + (sourceMesoId != null ? sourceMesoId.hashCode() : 0);
        result = 31 * result + (microRirs != null ? microRirs.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        result = 31 * result + (finishedAt != null ? finishedAt.hashCode() : 0);
        result = 31 * result + (deletedAt != null ? deletedAt.hashCode() : 0);
        result = 31 * result + (firstMicroCompletedAt != null ? firstMicroCompletedAt.hashCode() : 0);
        result = 31 * result + (firstWorkoutCompletedAt != null ? firstWorkoutCompletedAt.hashCode() : 0);
        result = 31 * result + (firstExerciseCompletedAt != null ? firstExerciseCompletedAt.hashCode() : 0);
        result = 31 * result + (firstSetCompletedAt != null ? firstSetCompletedAt.hashCode() : 0);
        result = 31 * result + (lastMicroFinishedAt != null ? lastMicroFinishedAt.hashCode() : 0);
        result = 31 * result + (lastSetCompletedAt != null ? lastSetCompletedAt.hashCode() : 0);
        result = 31 * result + (lastSetSkippedAt != null ? lastSetSkippedAt.hashCode() : 0);
        result = 31 * result + (lastWorkoutCompletedAt != null ? lastWorkoutCompletedAt.hashCode() : 0);
        result = 31 * result + (lastWorkoutFinishedAt != null ? lastWorkoutFinishedAt.hashCode() : 0);
        result = 31 * result + (lastWorkoutSkippedAt != null ? lastWorkoutSkippedAt.hashCode() : 0);
        result = 31 * result + (lastWorkoutPartialedAt != null ? lastWorkoutPartialedAt.hashCode() : 0);
        result = 31 * result + (weeks != null ? weeks.hashCode() : 0);
        return result;
    }
}
