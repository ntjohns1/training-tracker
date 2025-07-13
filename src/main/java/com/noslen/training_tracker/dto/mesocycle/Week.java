package com.noslen.training_tracker.dto.mesocycle;

import com.noslen.training_tracker.dto.day.DayData;

import java.util.List;

public record Week(List<DayData> days) {
}
