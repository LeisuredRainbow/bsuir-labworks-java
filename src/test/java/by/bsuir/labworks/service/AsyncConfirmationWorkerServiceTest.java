package by.bsuir.labworks.service;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.bsuir.labworks.config.AsyncProperties;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AsyncConfirmationWorkerServiceTest {

  @Mock
  private BookingService bookingService;

  @Mock
  private AsyncTaskRegistryService asyncTaskRegistryService;

  @Mock
  private AsyncTaskCounterService asyncTaskCounterService;

  @Mock
  private AsyncProperties asyncProperties;

  private AsyncConfirmationWorkerService worker;

  @BeforeEach
  void setUp() {
    when(asyncProperties.getConfirmationDelayMs()).thenReturn(0L);
    worker = new AsyncConfirmationWorkerService(bookingService, asyncTaskRegistryService,
        asyncTaskCounterService, asyncProperties);
  }

  @Test
  void runConfirmationSuccess() throws Exception {
    String taskId = "task-1";
    Long bookingId = 7L;
    CompletableFuture<Void> future = worker.runConfirmation(taskId, bookingId);
    future.get(); // wait for completion (delay is 0)
    verify(asyncTaskRegistryService).markRunning(taskId, "Confirmation is running");
    verify(bookingService).confirmBooking(bookingId);
    verify(asyncTaskRegistryService).markSuccess(taskId, "Booking confirmed successfully");
    verify(asyncTaskCounterService).incrementSucceeded();
    verify(asyncTaskCounterService).decrementRunning();
  }

  @Test
  void runConfirmationFailure() throws Exception {
    String taskId = "task-fail";
    Long bookingId = 99L;
    doThrow(new NoSuchElementException("Booking not found"))
        .when(bookingService).confirmBooking(bookingId);
    CompletableFuture<Void> future = worker.runConfirmation(taskId, bookingId);
    try {
      future.get();
    } catch (Exception e) {
      // expected
    }
    verify(asyncTaskRegistryService).markFailed(taskId, "Booking not found");
    verify(asyncTaskCounterService).incrementFailed();
    verify(asyncTaskCounterService).decrementRunning();
  }
}