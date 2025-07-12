package com.noslen.training_tracker.model.day;

import java.time.Instant;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.noslen.training_tracker.model.muscle_group.MuscleGroup;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "day_muscle_groups")
public class DayMuscleGroup {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "day_id")
    @JsonBackReference(value = "musclegroup-day")
    private Day day;

    @ManyToOne
    @JoinColumn(name = "muscle_group_id")
    @JsonBackReference(value = "musclegroup-musclegroup")
    private MuscleGroup muscleGroup;
    
    private Integer pump;
    private Integer soreness;
    private Integer workload;
    
    @Column(name = "recommended_sets")
    private Integer recommendedSets;
    
    private String status;
    
    @Column(name = "created_at")
    private Instant createdAt;
    
    @Column(name = "updated_at")
    private Instant updatedAt;

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

        DayMuscleGroup that = (DayMuscleGroup) o;

        if (!Objects.equals(id,
                            that.id)) return false;
        if (!Objects.equals(day,
                            that.day)) return false;
        if (!Objects.equals(muscleGroup,
                            that.muscleGroup))
            return false;
        if (!Objects.equals(pump,
                            that.pump)) return false;
        if (!Objects.equals(soreness,
                            that.soreness)) return false;
        if (!Objects.equals(workload,
                            that.workload)) return false;
        if (!Objects.equals(recommendedSets,
                            that.recommendedSets))
            return false;
        if (!Objects.equals(status,
                            that.status)) return false;
        if (!Objects.equals(createdAt,
                            that.createdAt)) return false;
        if (!Objects.equals(updatedAt,
                            that.updatedAt)) return false;
        return Objects.equals(day,
                              that.day);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (day != null ? day.hashCode() : 0);
        result = 31 * result + (muscleGroup != null ? muscleGroup.hashCode() : 0);
        result = 31 * result + (pump != null ? pump.hashCode() : 0);
        result = 31 * result + (soreness != null ? soreness.hashCode() : 0);
        result = 31 * result + (workload != null ? workload.hashCode() : 0);
        result = 31 * result + (recommendedSets != null ? recommendedSets.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        result = 31 * result + (day != null ? day.hashCode() : 0);
        return result;
    }
}
