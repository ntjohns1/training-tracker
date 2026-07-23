package com.noslen.training_tracker.event;

import com.noslen.training_tracker.dto.day.request.FinishDayRequest;

/**
 * Emitted when a workout day is completed. Carries the finish-day payload so the progression
 * consumer can program the next week.
 *
 * <p>MVP note: this is a "fat" event carrying the full {@link FinishDayRequest}. A future
 * hardening step is to slim it to a pointer (ids only) and have the consumer re-read persisted
 * feedback from the database (transactional-outbox style) — see the plan file.</p>
 *
 * @param dayId            the completed day
 * @param mesoId           the owning mesocycle (used as the Kafka partition key for ordering)
 * @param finishDayRequest the full finish-day feedback payload
 */
public record DayCompletedEvent(Long dayId, Long mesoId, FinishDayRequest finishDayRequest) {
}
