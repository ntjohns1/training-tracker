package com.noslen.training_tracker.model.day;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "days")
public class Day {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "meso_id")
    private Long mesoId;
    
    private Integer week;
    private Integer position;
    
    @Column(name = "created_at")
    private Instant createdAt;
    
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    private Double bodyweight;
    
    @Column(name = "bodyweight_at")
    private Instant bodyweightAt;
    
    private String unit;
    
    @Column(name = "finished_at")
    private Instant finishedAt;
    
    private String label;
    
    @ElementCollection
    @Builder.Default
    private List<String> notes = new ArrayList<>();
    
    @OneToMany(mappedBy = "day", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<DayExercise> exercises = new ArrayList<>();
    
    @OneToMany(mappedBy = "day", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<DayMuscleGroup> muscleGroups = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Day day = (Day) o;

        if (!Objects.equals(id,
                            day.id)) return false;
        if (!Objects.equals(mesoId,
                            day.mesoId)) return false;
        if (!Objects.equals(week,
                            day.week)) return false;
        if (!Objects.equals(position,
                            day.position)) return false;
        if (!Objects.equals(createdAt,
                            day.createdAt)) return false;
        if (!Objects.equals(updatedAt,
                            day.updatedAt)) return false;
        if (!Objects.equals(bodyweight,
                            day.bodyweight)) return false;
        if (!Objects.equals(bodyweightAt,
                            day.bodyweightAt)) return false;
        if (!Objects.equals(unit,
                            day.unit)) return false;
        if (!Objects.equals(finishedAt,
                            day.finishedAt)) return false;
        if (!Objects.equals(label,
                            day.label)) return false;
        if (!Objects.equals(notes,
                            day.notes)) return false;
        if (!Objects.equals(exercises,
                            day.exercises)) return false;
        return Objects.equals(muscleGroups,
                              day.muscleGroups);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (mesoId != null ? mesoId.hashCode() : 0);
        result = 31 * result + (week != null ? week.hashCode() : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        result = 31 * result + (bodyweight != null ? bodyweight.hashCode() : 0);
        result = 31 * result + (bodyweightAt != null ? bodyweightAt.hashCode() : 0);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        result = 31 * result + (finishedAt != null ? finishedAt.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + (exercises != null ? exercises.hashCode() : 0);
        result = 31 * result + (muscleGroups != null ? muscleGroups.hashCode() : 0);
        return result;
    }
}
