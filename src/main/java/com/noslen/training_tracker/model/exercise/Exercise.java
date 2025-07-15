package com.noslen.training_tracker.model.exercise;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
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
@JsonInclude(JsonInclude.Include.ALWAYS)
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonProperty("id")
    private Long id;

    @Column(name = "name")
    @JsonProperty("name")
    private String name;

    @Column(name = "muscle_group_id")
    @JsonProperty("muscleGroupId")
    private Long muscleGroupId;
    
    @Column(name = "youtube_id")
    @JsonProperty("youtubeId")
    private String youtubeId;
    
    @Column(name = "exercise_type")
    @JsonProperty("exerciseType")
    private String exerciseType;
    
    @Column(name = "user_id")
    @JsonProperty("userId")
    private Long userId;
    
    @Column(name = "created_at")
    @JsonProperty("createdAt")
    private Instant createdAt;
    
    @Column(name = "updated_at")
    @JsonProperty("updatedAt")
    private Instant updatedAt;
    
    @Column(name = "deleted_at")
    @JsonProperty("deletedAt")
    private Instant deletedAt;
    
    @Column(name = "mg_sub_type")
    @JsonProperty("mgSubType")
    private String mgSubType;
    
    @OneToMany(cascade = CascadeType.ALL)
    @JsonManagedReference(value = "exercisenote-exercise")
    @JoinColumn(name = "exercise_id")
    @JsonProperty("notes")
    private List<ExerciseNote> notes;

    public void setName(String name) {
        this.name = name;
    }

    public void setMuscleGroupId(Long muscleGroupId) {
        this.muscleGroupId = muscleGroupId;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public void setMgSubType(String mgSubType) {
        this.mgSubType = mgSubType;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setNotes(List<ExerciseNote> notes) {
        this.notes = notes;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

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
