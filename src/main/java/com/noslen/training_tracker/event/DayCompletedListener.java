package com.noslen.training_tracker.event;

import com.noslen.training_tracker.service.progression.MesocycleProgressionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumes {@link DayCompletedEvent} from Kafka and programs the next week's workout.
 *
 * <p>The listener container's startup is gated by {@code app.kafka.consumer.auto-startup}
 * (default false) so environments without a broker — including the default test context — do
 * not attempt to connect. The dev profile enables it.</p>
 */
@Component
public class DayCompletedListener {

    private final MesocycleProgressionService progressionService;

    public DayCompletedListener(MesocycleProgressionService progressionService) {
        this.progressionService = progressionService;
    }

    @KafkaListener(
            topics = WorkoutTopics.DAY_COMPLETED,
            groupId = "progression",
            autoStartup = "${app.kafka.consumer.auto-startup:false}")
    public void onDayCompleted(DayCompletedEvent event) {
        // Progression is idempotent: reprocessing a day whose next week is already programmed
        // is a no-op (Kafka delivers at least once). See MesocycleProgressionServiceImpl.
        progressionService.processCompletedDayandProgramNext(event.finishDayRequest());
    }
}
