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
    private Float weight;
    private Float weightTarget;
    private Float weightTargetMin;
    private Float weightTargetMax;
    private Integer reps;
    private Integer repsTarget;
    private Float bodyweight;
    private String unit;


    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
     private Instant finishedAt;

    private String status;

    @ManyToOne
    @JoinColumn(name = "day_exercise_id", insertable = false, updatable = false)
    @JsonBackReference
    private DayExercise dayExercise;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExerciseSet that = (ExerciseSet) o;

        if (!Objects.equals(id,
                            that.id)) return false;
        if (!Objects.equals(dayExerciseId,
                            that.dayExerciseId))
            return false;
        if (!Objects.equals(position,
                            that.position)) return false;
        if (!Objects.equals(setType,
                            that.setType)) return false;
        if (!Objects.equals(weight,
                            that.weight)) return false;
        if (!Objects.equals(weightTarget,
                            that.weightTarget)) return false;
        if (!Objects.equals(weightTargetMin,
                            that.weightTargetMin))
            return false;
        if (!Objects.equals(weightTargetMax,
                            that.weightTargetMax))
            return false;
        if (!Objects.equals(reps,
                            that.reps)) return false;
        if (!Objects.equals(repsTarget,
                            that.repsTarget)) return false;
        if (!Objects.equals(bodyweight,
                            that.bodyweight)) return false;
        if (!Objects.equals(unit,
                            that.unit)) return false;
        if (!Objects.equals(createdAt,
                            that.createdAt)) return false;
        if (!Objects.equals(finishedAt,
                            that.finishedAt)) return false;
        if (!Objects.equals(status,
                            that.status)) return false;
        return Objects.equals(dayExercise,
                              that.dayExercise);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (dayExerciseId != null ? dayExerciseId.hashCode() : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        result = 31 * result + (setType != null ? setType.hashCode() : 0);
        result = 31 * result + (weight != null ? weight.hashCode() : 0);
        result = 31 * result + (weightTarget != null ? weightTarget.hashCode() : 0);
        result = 31 * result + (weightTargetMin != null ? weightTargetMin.hashCode() : 0);
        result = 31 * result + (weightTargetMax != null ? weightTargetMax.hashCode() : 0);
        result = 31 * result + (reps != null ? reps.hashCode() : 0);
        result = 31 * result + (repsTarget != null ? repsTarget.hashCode() : 0);
        result = 31 * result + (bodyweight != null ? bodyweight.hashCode() : 0);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (finishedAt != null ? finishedAt.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (dayExercise != null ? dayExercise.hashCode() : 0);
        return result;
    }
}
