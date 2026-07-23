package com.noslen.training_tracker.event;

import com.noslen.training_tracker.dto.day.request.FinishDayRequest;
import com.noslen.training_tracker.service.progression.MesocycleProgressionService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Broker-free unit test of the Kafka consumer's delegation. The actual broker round-trip is
 * covered by the Phase F golden-master replay (Testcontainers).
 */
class DayCompletedListenerTest {

    @Test
    void forwardsFinishDayRequestToProgressionService() {
        MesocycleProgressionService progressionService = mock(MesocycleProgressionService.class);
        DayCompletedListener listener = new DayCompletedListener(progressionService);

        FinishDayRequest request = new FinishDayRequest(100L, 200L, 2, 1, Instant.now(), Instant.now(),
                180f, Instant.now(), "lb", Instant.now(), null, List.of(), List.of(), List.of(),
                "pendingConfirmation");
        DayCompletedEvent event = new DayCompletedEvent(100L, 200L, request);

        listener.onDayCompleted(event);

        verify(progressionService).processCompletedDayandProgramNext(request);
    }
}
