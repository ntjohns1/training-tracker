package com.noslen.training_tracker.model.mesocycle;

import java.time.Instant;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

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
@Table(name = "meso_notes")
public class MesoNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JsonBackReference(value = "mesocycle-notes")
    @JoinColumn(name = "mesocycle_id")
    private Mesocycle mesocycle;
    // @JsonManagedReference(value = "meso-note-user")
    // @JsonProperty("user_id")
    private Long userId;
    // @JsonManagedReference(value = "meso-note-note")
    // @JsonProperty("note_id")    
    private Long noteId;
    private Instant createdAt;
    private Instant updatedAt;
    private String text;
    
    @JsonProperty("mesoId")
    public Long getMesoId() {
        return mesocycle != null ? mesocycle.getId() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MesoNote mesoNote = (MesoNote) o;

        if (!Objects.equals(id,
                            mesoNote.id)) return false;
        if (!Objects.equals(mesocycle,
                            mesoNote.mesocycle)) return false;
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
        result = 31 * result + (mesocycle != null ? mesocycle.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (noteId != null ? noteId.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }
}
