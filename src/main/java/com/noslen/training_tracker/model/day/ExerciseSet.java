package com.noslen.training_tracker.model.day;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exercise_sets")
public class ExerciseSet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "day_exercise_id")
    private Long dayExerciseId;
    
    private Integer position;
    
    @Column(name = "set_type")
    private String setType;
    
    private Double weight;
    private Integer reps;
    private Integer rir;
    private Double intensity;
    
    @Column(name = "created_at")
    private Instant createdAt;
    
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    @ManyToOne
    @JoinColumn(name = "day_exercise_id", insertable = false, updatable = false)
    @JsonBackReference
    private DayExercise dayExercise;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExerciseSet that = (ExerciseSet) o;

        if (!id.equals(that.id)) return false;
        if (!Objects.equals(dayExerciseId,
                            that.dayExerciseId))
            return false;
        if (!Objects.equals(position,
                            that.position)) return false;
        if (!Objects.equals(setType,
                            that.setType)) return false;
        if (!Objects.equals(weight,
                            that.weight)) return false;
        if (!Objects.equals(reps,
                            that.reps)) return false;
        if (!Objects.equals(rir,
                            that.rir)) return false;
        if (!Objects.equals(intensity,
                            that.intensity)) return false;
        if (!Objects.equals(createdAt,
                            that.createdAt)) return false;
        return Objects.equals(updatedAt,
                            that.updatedAt);
        // Removed dayExercise comparison to break circular reference
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (dayExerciseId != null ? dayExerciseId.hashCode() : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        result = 31 * result + (setType != null ? setType.hashCode() : 0);
        result = 31 * result + (weight != null ? weight.hashCode() : 0);
        result = 31 * result + (reps != null ? reps.hashCode() : 0);
        result = 31 * result + (rir != null ? rir.hashCode() : 0);
        result = 31 * result + (intensity != null ? intensity.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        // Removed dayExercise from hashCode calculation to break circular reference
        return result;
    }
}
