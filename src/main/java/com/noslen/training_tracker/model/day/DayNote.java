package com.noslen.training_tracker.model.day;

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
import java.time.Instant;
import java.util.Objects;

@Getter
@Entity
@Table(name = "day_notes")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DayNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "day_id")
    @JsonBackReference(value = "daynote-day")
    private Day day;

    @Column(name = "note_id")
    private Long noteId;

    private Boolean pinned;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    private String text;

    @JsonProperty("dayId")
    public Long getDayId() {
        return day != null ? day.getId() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        DayNote dayNote = (DayNote) o;

        if (!Objects.equals(id,
                dayNote.id))
            return false;
        if (!Objects.equals(day,
                dayNote.day))
            return false;
        if (!Objects.equals(noteId,
                dayNote.noteId))
            return false;
        if (!Objects.equals(pinned,
                dayNote.pinned))
            return false;
        if (!Objects.equals(text,
                dayNote.text))
            return false;
        if (!Objects.equals(createdAt,
                dayNote.createdAt))
            return false;
        if (!Objects.equals(updatedAt,
                dayNote.updatedAt))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (day != null ? day.hashCode() : 0);
        result = 31 * result + (noteId != null ? noteId.hashCode() : 0);
        result = 31 * result + (pinned != null ? pinned.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        return result;
    }
}
