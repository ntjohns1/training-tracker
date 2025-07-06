package com.noslen.training_tracker.model.day;

import java.time.Instant;
import java.util.Objects;

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
    
    @Column(name = "day_id")
    private Long dayId;
    
    @Column(name = "muscle_group_id")
    private Long muscleGroupId;
    
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
    
    @ManyToOne
    @JoinColumn(name = "day_id", insertable = false, updatable = false)
    @JsonBackReference
    private Day day;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DayMuscleGroup that = (DayMuscleGroup) o;

        if (!Objects.equals(id,
                            that.id)) return false;
        if (!Objects.equals(dayId,
                            that.dayId)) return false;
        if (!Objects.equals(muscleGroupId,
                            that.muscleGroupId))
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
        result = 31 * result + (dayId != null ? dayId.hashCode() : 0);
        result = 31 * result + (muscleGroupId != null ? muscleGroupId.hashCode() : 0);
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
