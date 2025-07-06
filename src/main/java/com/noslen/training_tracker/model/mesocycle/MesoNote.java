package com.noslen.training_tracker.model.mesocycle;

import java.time.Instant;

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
}
