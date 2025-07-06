package com.noslen.training_tracker.model.exercise;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "exercises")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne
    @JoinColumn(name = "muscle_group_id")
    private Long muscleGroupId;
    private String youtubeId;
    private String exerciseType;
    // #TODO: Add user_id
    private Long userId;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private String mgSubType;
    private List<ExerciseNote> notes;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Exercise exercise = (Exercise) o;

        if (!Objects.equals(id,
                            exercise.id)) return false;
        if (!Objects.equals(name,
                            exercise.name)) return false;
        if (!Objects.equals(muscleGroupId,
                            exercise.muscleGroupId))
            return false;
        if (!Objects.equals(youtubeId,
                            exercise.youtubeId)) return false;
        if (!Objects.equals(exerciseType,
                            exercise.exerciseType))
            return false;
        if (!Objects.equals(userId,
                            exercise.userId)) return false;
        if (!Objects.equals(createdAt,
                            exercise.createdAt)) return false;
        if (!Objects.equals(updatedAt,
                            exercise.updatedAt)) return false;
        if (!Objects.equals(deletedAt,
                            exercise.deletedAt)) return false;
        if (!Objects.equals(mgSubType,
                            exercise.mgSubType)) return false;
        return Objects.equals(notes,
                              exercise.notes);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (muscleGroupId != null ? muscleGroupId.hashCode() : 0);
        result = 31 * result + (youtubeId != null ? youtubeId.hashCode() : 0);
        result = 31 * result + (exerciseType != null ? exerciseType.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        result = 31 * result + (deletedAt != null ? deletedAt.hashCode() : 0);
        result = 31 * result + (mgSubType != null ? mgSubType.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        return result;
    }
}
