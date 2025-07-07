package com.noslen.training_tracker.model.muscle_group;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
@Entity
@Table(name = "progressions")
public class Progression {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long muscleGroupId;
    private String mgProgressionType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Progression that = (Progression) o;

        if (!Objects.equals(id,
                            that.id)) return false;
        if (!Objects.equals(muscleGroupId,
                            that.muscleGroupId)) return false;
        return Objects.equals(mgProgressionType,
                              that.mgProgressionType);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (muscleGroupId != null ? muscleGroupId.hashCode() : 0);
        result = 31 * result + (mgProgressionType != null ? mgProgressionType.hashCode() : 0);
        return result;
    }
}
