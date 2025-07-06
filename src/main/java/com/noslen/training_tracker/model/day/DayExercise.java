package com.noslen.training_tracker.model.day;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "day_exercises")
public class DayExercise {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "day_id")
    private Long dayId;
    
    @Column(name = "exercise_id")
    private Long exerciseId;
    
    private Integer position;
    
    @Column(name = "joint_pain")
    private Integer jointPain;
    
    @Column(name = "created_at")
    private Instant createdAt;
    
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    @Column(name = "source_day_exercise_id")
    private Long sourceDayExerciseId;
    
    @Column(name = "muscle_group_id")
    private Long muscleGroupId;
    
    private String status;
    
    @OneToMany(mappedBy = "dayExercise", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<ExerciseSet> sets = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "day_id", insertable = false, updatable = false)
    @JsonBackReference
    private Day day;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DayExercise that = (DayExercise) o;

        if (!Objects.equals(id,
                            that.id)) return false;
        if (!Objects.equals(dayId,
                            that.dayId)) return false;
        if (!Objects.equals(exerciseId,
                            that.exerciseId)) return false;
        if (!Objects.equals(position,
                            that.position)) return false;
        if (!Objects.equals(jointPain,
                            that.jointPain)) return false;
        if (!Objects.equals(createdAt,
                            that.createdAt)) return false;
        if (!Objects.equals(updatedAt,
                            that.updatedAt)) return false;
        if (!Objects.equals(sourceDayExerciseId,
                            that.sourceDayExerciseId))
            return false;
        if (!Objects.equals(muscleGroupId,
                            that.muscleGroupId))
            return false;
        if (!Objects.equals(status,
                            that.status)) return false;
        if (!Objects.equals(sets,
                            that.sets)) return false;
        return Objects.equals(day,
                              that.day);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (dayId != null ? dayId.hashCode() : 0);
        result = 31 * result + (exerciseId != null ? exerciseId.hashCode() : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        result = 31 * result + (jointPain != null ? jointPain.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        result = 31 * result + (sourceDayExerciseId != null ? sourceDayExerciseId.hashCode() : 0);
        result = 31 * result + (muscleGroupId != null ? muscleGroupId.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (sets != null ? sets.hashCode() : 0);
        result = 31 * result + (day != null ? day.hashCode() : 0);
        return result;
    }
}
