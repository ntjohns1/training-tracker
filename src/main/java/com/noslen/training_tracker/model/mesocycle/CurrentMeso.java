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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "current_meso")
public class CurrentMeso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String key;
    private Long userId;
    private String name;
    private Integer days;
    private String unit;
    private Long sourceTemplateId;
    private Long sourceMesoId;
    private Long microRirs;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant finishedAt;
    private Instant deletedAt;
    private Instant firstMicroCompletedAt;
    private Instant firstWorkoutCompletedAt;
    private Instant firstExerciseCompletedAt;
    private Instant firstSetCompletedAt;
    private Instant lastMicroFinishedAt;
    private Instant lastSetCompletedAt;
    private Instant lastSetSkippedAt;
    private Instant lastWorkoutCompletedAt;
    private Instant lastWorkoutFinishedAt;
    private Instant lastWorkoutSkippedAt;
    private Instant lastWorkoutPartialedAt;
    private List<Day> weeks;

    }
