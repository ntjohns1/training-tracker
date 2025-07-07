package com.noslen.training_tracker.model.mesocycle;

import java.time.Instant;
import java.util.Objects;

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
@Table(name = "meso_notes")
public class MesoNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: Join with Mesocycle
    private Long mesoId;
    private Long userId;

    // TODO: Investigate this field
    private Long noteId;
    private Instant createdAt;
    private Instant updatedAt;
    private String text;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MesoNote mesoNote = (MesoNote) o;

        if (!Objects.equals(id,
                            mesoNote.id)) return false;
        if (!Objects.equals(mesoId,
                            mesoNote.mesoId)) return false;
        if (!Objects.equals(userId,
                            mesoNote.userId)) return false;
        if (!Objects.equals(noteId,
                            mesoNote.noteId)) return false;
        if (!Objects.equals(createdAt,
                            mesoNote.createdAt)) return false;
        if (!Objects.equals(updatedAt,
                            mesoNote.updatedAt)) return false;
        return Objects.equals(text,
                              mesoNote.text);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (mesoId != null ? mesoId.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (noteId != null ? noteId.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }
}
