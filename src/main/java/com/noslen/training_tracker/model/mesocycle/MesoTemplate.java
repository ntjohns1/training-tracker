package com.noslen.training_tracker.model.mesocycle;

import java.time.Instant;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @Column(name = "template_key")
    private String templateKey;
    private String name;
    private String emphasis;
    private String sex;
    private Long userId;

    @ManyToOne
    @JsonBackReference(value = "meso-template-source-template")
    @JsonProperty("source_template_id")
    @JoinColumn(name = "source_template_id")
    private MesoTemplate sourceTemplate;
    
    @ManyToOne
    @JsonBackReference(value = "meso-template-source-meso")
    @JsonProperty("source_meso_id")
    @JoinColumn(name = "source_meso_id")
    private Mesocycle sourceMeso;
    @ManyToOne
    @JsonBackReference(value = "meso-template-prev-template")
    @JsonProperty("prev_template_id")
    @JoinColumn(name = "prev_template_id")
    private MesoTemplate prevTemplate;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private Integer frequency;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MesoTemplate that = (MesoTemplate) o;

        if (!Objects.equals(id,
                that.id))
            return false;
        if (!Objects.equals(templateKey,
                that.templateKey))
            return false;
        if (!Objects.equals(name,
                that.name))
            return false;
        if (!Objects.equals(emphasis,
                that.emphasis))
            return false;
        if (!Objects.equals(sex,
                that.sex))
            return false;
        if (!Objects.equals(userId,
                that.userId))
            return false;
        if (!Objects.equals(sourceTemplate,
                that.sourceTemplate))
            return false;
        if (!Objects.equals(sourceMeso,
                that.sourceMeso))
            return false;
        if (!Objects.equals(prevTemplate,
                that.prevTemplate))
            return false;
        if (!Objects.equals(createdAt,
                that.createdAt))
            return false;
        if (!Objects.equals(updatedAt,
                that.updatedAt))
            return false;
        if (!Objects.equals(deletedAt,
                that.deletedAt))
            return false;
        return Objects.equals(frequency,
                that.frequency);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (templateKey != null ? templateKey.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (emphasis != null ? emphasis.hashCode() : 0);
        result = 31 * result + (sex != null ? sex.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (sourceTemplate != null ? sourceTemplate.hashCode() : 0);
        result = 31 * result + (sourceMeso != null ? sourceMeso.hashCode() : 0);
        result = 31 * result + (prevTemplate != null ? prevTemplate.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        result = 31 * result + (deletedAt != null ? deletedAt.hashCode() : 0);
        result = 31 * result + (frequency != null ? frequency.hashCode() : 0);
        return result;
    }
}
