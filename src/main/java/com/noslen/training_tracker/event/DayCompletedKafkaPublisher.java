package com.noslen.training_tracker.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Bridges the in-process {@link DayCompletedEvent} (published by DayService) to Kafka, but only
 * after the completing transaction commits — so a rolled-back day completion never programs the
 * next week.
 */
@Component
public class DayCompletedKafkaPublisher {

    private final KafkaTemplate<String, DayCompletedEvent> kafkaTemplate;

    public DayCompletedKafkaPublisher(KafkaTemplate<String, DayCompletedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDayCompleted(DayCompletedEvent event) {
        // Key by mesocycle so a given meso's events stay ordered within a partition.
        String key = event.mesoId() == null ? null : String.valueOf(event.mesoId());
        kafkaTemplate.send(WorkoutTopics.DAY_COMPLETED, key, event);
    }
}
