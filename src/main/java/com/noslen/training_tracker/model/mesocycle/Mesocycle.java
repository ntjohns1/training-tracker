package com.noslen.training_tracker.model.mesocycle;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.noslen.training_tracker.enums.Status;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.muscle_group.Progression;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    
    @Column(name = "meso_key")
    private String mesocycleKey;
    private Long userId;
    private String name;
    private Integer days;
    private String unit;

//    TODO: fix this relation
    @ManyToOne
    @JoinColumn(name = "source_template_id")
    private MesoTemplate sourceTemplate;

//    TODO: fix this relation
    @ManyToOne
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

    @OneToMany
    @JsonManagedReference(value = "day-mesocycle")
    @JoinColumn(name = "meso_id")
    private List<Day> weeks;

    @OneToMany
    @JsonManagedReference(value = "mesocycle-notes")
    @JoinColumn(name = "mesocycle_id")
    @JsonProperty("notes")
    private List<MesoNote> notes;

    private Status status;
    private String generatedFrom;

    @OneToMany(mappedBy = "mesocycle", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKey(name = "muscleGroupId")
    @JsonProperty("progressions")
    private Map<Long, Progression> progressions;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Mesocycle mesocycle = (Mesocycle) o;
        return Objects.equals(id,
                              mesocycle.id)
                && Objects.equals(mesocycleKey,
                                  mesocycle.mesocycleKey)
                && Objects.equals(userId,
                                  mesocycle.userId)
                && Objects.equals(name,
                                  mesocycle.name)
                && Objects.equals(days,
                                  mesocycle.days)
                && Objects.equals(unit,
                                  mesocycle.unit)
                && Objects.equals(sourceTemplate,
                                  mesocycle.sourceTemplate)
                && Objects.equals(sourceMeso,
                                  mesocycle.sourceMeso)
                && Objects.equals(microRirs,
                                  mesocycle.microRirs)
                && Objects.equals(createdAt,
                                  mesocycle.createdAt)
                && Objects.equals(updatedAt,
                                  mesocycle.updatedAt)
                && Objects.equals(finishedAt,
                                  mesocycle.finishedAt)
                && Objects.equals(deletedAt,
                                  mesocycle.deletedAt)
                && Objects.equals(firstMicroCompletedAt,
                                  mesocycle.firstMicroCompletedAt)
                && Objects.equals(firstWorkoutCompletedAt,
                                  mesocycle.firstWorkoutCompletedAt)
                && Objects.equals(firstExerciseCompletedAt,
                                  mesocycle.firstExerciseCompletedAt)
                && Objects.equals(firstSetCompletedAt,
                                  mesocycle.firstSetCompletedAt)
                && Objects.equals(lastMicroFinishedAt,
                                  mesocycle.lastMicroFinishedAt)
                && Objects.equals(lastSetCompletedAt,
                                  mesocycle.lastSetCompletedAt)
                && Objects.equals(lastSetSkippedAt,
                                  mesocycle.lastSetSkippedAt)
                && Objects.equals(lastWorkoutCompletedAt,
                                  mesocycle.lastWorkoutCompletedAt)
                && Objects.equals(lastWorkoutFinishedAt,
                                  mesocycle.lastWorkoutFinishedAt)
                && Objects.equals(lastWorkoutSkippedAt,
                                  mesocycle.lastWorkoutSkippedAt)
                && Objects.equals(lastWorkoutPartialedAt,
                                  mesocycle.lastWorkoutPartialedAt)
                && Objects.equals(weeks,
                                  mesocycle.weeks)
                && Objects.equals(notes,
                                  mesocycle.notes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(mesocycleKey);
        result = 31 * result + Objects.hashCode(userId);
        result = 31 * result + Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(days);
        result = 31 * result + Objects.hashCode(unit);
        result = 31 * result + Objects.hashCode(sourceTemplate);
        result = 31 * result + Objects.hashCode(sourceMeso);
        result = 31 * result + Objects.hashCode(microRirs);
        result = 31 * result + Objects.hashCode(createdAt);
        result = 31 * result + Objects.hashCode(updatedAt);
        result = 31 * result + Objects.hashCode(finishedAt);
        result = 31 * result + Objects.hashCode(deletedAt);
        result = 31 * result + Objects.hashCode(firstMicroCompletedAt);
        result = 31 * result + Objects.hashCode(firstWorkoutCompletedAt);
        result = 31 * result + Objects.hashCode(firstExerciseCompletedAt);
        result = 31 * result + Objects.hashCode(firstSetCompletedAt);
        result = 31 * result + Objects.hashCode(lastMicroFinishedAt);
        result = 31 * result + Objects.hashCode(lastSetCompletedAt);
        result = 31 * result + Objects.hashCode(lastSetSkippedAt);
        result = 31 * result + Objects.hashCode(lastWorkoutCompletedAt);
        result = 31 * result + Objects.hashCode(lastWorkoutFinishedAt);
        result = 31 * result + Objects.hashCode(lastWorkoutSkippedAt);
        result = 31 * result + Objects.hashCode(lastWorkoutPartialedAt);
        result = 31 * result + Objects.hashCode(weeks);
        result = 31 * result + Objects.hashCode(notes);
        return result;
    }
}
