package com.noslen.training_tracker.model.exercise;

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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "exercise_notes")
public class ExerciseNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long exerciseId;
    private Long userId;
    private Long noteId;
    private Long dayExerciseId;
    private Instant createdAt;
    private Instant updatedAt;
    private String text;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExerciseNote that = (ExerciseNote) o;

        if (!Objects.equals(id,
                            that.id)) return false;
        if (!Objects.equals(exerciseId,
                            that.exerciseId)) return false;
        if (!Objects.equals(userId,
                            that.userId)) return false;
        if (!Objects.equals(noteId,
                            that.noteId)) return false;
        if (!Objects.equals(dayExerciseId,
                            that.dayExerciseId)) return false;
        if (!Objects.equals(createdAt,
                            that.createdAt)) return false;
        if (!Objects.equals(updatedAt,
                            that.updatedAt)) return false;
        return Objects.equals(text,
                              that.text);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (exerciseId != null ? exerciseId.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (noteId != null ? noteId.hashCode() : 0);
        result = 31 * result + (dayExerciseId != null ? dayExerciseId.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }

}
