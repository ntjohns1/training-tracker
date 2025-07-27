package com.noslen.training_tracker.dto.mesocycle;

import com.noslen.training_tracker.dto.day.DayResponse;

import java.util.List;

public record Week(List<DayResponse> days) {
}
