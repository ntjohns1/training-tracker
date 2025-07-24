package com.noslen.training_tracker.model.day;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.noslen.training_tracker.model.exercise.Exercise;
import com.noslen.training_tracker.enums.Status;

import lombok.*;

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
    
    @ManyToOne
    @JsonBackReference(value = "dayexercise-day")
    @JoinColumn(name = "day_id")
    private Day day;
    
    @ManyToOne
    @JsonBackReference(value = "dayexercise-exercise")
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;
    
    private Integer position;
    
    @Column(name = "joint_pain")
    private Integer jointPain;

    @Setter
    @Column(name = "created_at")
    private Instant createdAt;
    
    @Setter
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    @Column(name = "source_day_exercise_id")
    private Long sourceDayExerciseId;
    
    @ManyToOne
    @JsonBackReference(value = "dayexercise-musclegroup")
    @JoinColumn(name = "muscle_group_id")
    private DayMuscleGroup muscleGroup;
    
    private Status status;
    
    @OneToMany(mappedBy = "dayExercise", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "exerciseset-dayexercise")
    @Builder.Default
    private List<ExerciseSet> sets = new ArrayList<>();

    // Setter methods for MapStruct mapping
    public void setDay(Day day) {
        this.day = day;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public void setMuscleGroup(DayMuscleGroup muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    /**
     * Convenience method to get the exercise ID from the Exercise object
     * @return the ID of the associated exercise
     */
    @JsonProperty("exerciseId")
    public Long getExerciseId() {
        return exercise != null ? exercise.getId() : null;
    }
    
    /**
     * Convenience method to get the day ID from the Day object
     * @return the ID of the associated day
     */
    @JsonProperty("dayId")
    public Long getDayId() {
        return day != null ? day.getId() : null;
    }/**

     * Convenience method to get the muscle group ID from the Day object
     * @return the ID of the associated muscle group
     */
    @JsonProperty("muscleGroupId")
    public Long getMuscleGroupId() {
        return muscleGroup != null ? muscleGroup.getId() : null;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DayExercise that = (DayExercise) o;

        if (!Objects.equals(id,
                            that.id)) return false;
        if (!Objects.equals(day,
                            that.day)) return false;
        if (!Objects.equals(exercise,
                            that.exercise)) return false;
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
        if (!Objects.equals(muscleGroup,
                            that.muscleGroup))
            return false;
        return !Objects.equals(status,
                            that.status);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (day != null ? day.hashCode() : 0);
        result = 31 * result + (exercise != null ? exercise.hashCode() : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        result = 31 * result + (jointPain != null ? jointPain.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        result = 31 * result + (sourceDayExerciseId != null ? sourceDayExerciseId.hashCode() : 0);
        result = 31 * result + (muscleGroup != null ? muscleGroup.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}
