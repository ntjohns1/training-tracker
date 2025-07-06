package com.noslen.training_tracker.model.mesocycle;

import java.time.Instant;
import java.util.List;

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
}
