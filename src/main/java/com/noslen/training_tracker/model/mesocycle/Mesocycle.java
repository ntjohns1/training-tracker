package com.noslen.training_tracker.model.mesocycle;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mesocycles")
public class Mesocycle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String key;
    private Long userId;
    private String name;
    private Integer days;
    private String unit;

    @ManyToOne
    @JoinColumn(name = "source_template_id")
    private MesoTemplate sourceTemplate;

    @ManyToOne
    @JsonBackReference(value = "mesocycle-source-meso")
    @JoinColumn(name = "source_meso_id")
    private Mesocycle sourceMeso;
    
    @JsonProperty("sourceTemplateId")
    public Long getSourceTemplateId() {
        return sourceTemplate != null ? sourceTemplate.getId() : null;
    }
    
    @JsonProperty("sourceMesoId")
    public Long getSourceMesoId() {
        return sourceMeso != null ? sourceMeso.getId() : null;
    }

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
    private Integer weeks;

    @OneToMany
    @JsonManagedReference(value = "mesocycle-notes")
    @JoinColumn(name = "mesocycle_id")
    @JsonProperty("notes")
    private List<MesoNote> notes;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mesocycle mesocycle = (Mesocycle) o;

        if (!Objects.equals(id,
                            mesocycle.id)) return false;
        if (!Objects.equals(key,
                            mesocycle.key)) return false;
        if (!Objects.equals(userId,
                            mesocycle.userId)) return false;
        if (!Objects.equals(name,
                            mesocycle.name)) return false;
        if (!Objects.equals(days,
                            mesocycle.days)) return false;
        if (!Objects.equals(unit,
                            mesocycle.unit)) return false;
        if (!Objects.equals(sourceTemplate,
                            mesocycle.sourceTemplate))
            return false;
        if (!Objects.equals(sourceMeso,
                            mesocycle.sourceMeso))
            return false;
        if (!Objects.equals(microRirs,
                            mesocycle.microRirs)) return false;
        if (!Objects.equals(createdAt,
                            mesocycle.createdAt)) return false;
        if (!Objects.equals(updatedAt,
                            mesocycle.updatedAt)) return false;
        if (!Objects.equals(finishedAt,
                            mesocycle.finishedAt)) return false;
        if (!Objects.equals(deletedAt,
                            mesocycle.deletedAt)) return false;
        if (!Objects.equals(firstMicroCompletedAt,
                            mesocycle.firstMicroCompletedAt))
            return false;
        if (!Objects.equals(firstWorkoutCompletedAt,
                            mesocycle.firstWorkoutCompletedAt))
            return false;
        if (!Objects.equals(firstExerciseCompletedAt,
                            mesocycle.firstExerciseCompletedAt))
            return false;
        if (!Objects.equals(firstSetCompletedAt,
                            mesocycle.firstSetCompletedAt))
            return false;
        if (!Objects.equals(lastMicroFinishedAt,
                            mesocycle.lastMicroFinishedAt))
            return false;
        if (!Objects.equals(lastSetCompletedAt,
                            mesocycle.lastSetCompletedAt))
            return false;
        if (!Objects.equals(lastSetSkippedAt,
                            mesocycle.lastSetSkippedAt))
            return false;
        if (!Objects.equals(lastWorkoutCompletedAt,
                            mesocycle.lastWorkoutCompletedAt))
            return false;
        if (!Objects.equals(lastWorkoutFinishedAt,
                            mesocycle.lastWorkoutFinishedAt))
            return false;
        if (!Objects.equals(lastWorkoutSkippedAt,
                            mesocycle.lastWorkoutSkippedAt))
            return false;
        if (!Objects.equals(lastWorkoutPartialedAt,
                            mesocycle.lastWorkoutPartialedAt))
            return false;
        if (!Objects.equals(weeks,
                            mesocycle.weeks)) return false;
        return Objects.equals(notes,
                              mesocycle.notes);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (days != null ? days.hashCode() : 0);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        result = 31 * result + (sourceTemplate != null ? sourceTemplate.hashCode() : 0);
        result = 31 * result + (sourceMeso != null ? sourceMeso.hashCode() : 0);
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
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        return result;
    }
}
