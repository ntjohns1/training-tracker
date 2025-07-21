package com.noslen.training_tracker.dto.mesocycle;

import com.noslen.training_tracker.dto.day.DayPayload;

import java.util.List;

public record Week(List<DayPayload> days) {
}
