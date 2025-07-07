package com.noslen.training_tracker.model.mesocycle;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import com.noslen.training_tracker.model.day.Day;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "templates")
public class MesoTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String key;
    private String name;
    private String emphasis;
    private String sex;
    private Long userId;
    private Long sourceTemplateId;
    private Long sourceMesoId;
    private Long prevTemplateId;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private Integer frequency;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MesoTemplate that = (MesoTemplate) o;

        if (!Objects.equals(id,
                            that.id)) return false;
        if (!Objects.equals(key,
                            that.key)) return false;
        if (!Objects.equals(name,
                            that.name)) return false;
        if (!Objects.equals(emphasis,
                            that.emphasis)) return false;
        if (!Objects.equals(sex,
                            that.sex)) return false;
        if (!Objects.equals(userId,
                            that.userId)) return false;
        if (!Objects.equals(sourceTemplateId,
                            that.sourceTemplateId))
            return false;
        if (!Objects.equals(sourceMesoId,
                            that.sourceMesoId)) return false;
        if (!Objects.equals(prevTemplateId,
                            that.prevTemplateId))
            return false;
        if (!Objects.equals(createdAt,
                            that.createdAt)) return false;
        if (!Objects.equals(updatedAt,
                            that.updatedAt)) return false;
        if (!Objects.equals(deletedAt,
                            that.deletedAt)) return false;
        return Objects.equals(frequency,
                              that.frequency);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (emphasis != null ? emphasis.hashCode() : 0);
        result = 31 * result + (sex != null ? sex.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (sourceTemplateId != null ? sourceTemplateId.hashCode() : 0);
        result = 31 * result + (sourceMesoId != null ? sourceMesoId.hashCode() : 0);
        result = 31 * result + (prevTemplateId != null ? prevTemplateId.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        result = 31 * result + (deletedAt != null ? deletedAt.hashCode() : 0);
        result = 31 * result + (frequency != null ? frequency.hashCode() : 0);
        return result;
    }
}
