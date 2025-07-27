package com.noslen.training_tracker.dto.mesocycle.response;

import com.noslen.training_tracker.dto.day.response.DayResponse;

import java.util.List;

public record Week(List<DayResponse> days) {
}
