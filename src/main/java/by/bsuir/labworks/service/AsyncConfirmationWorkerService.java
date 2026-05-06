package by.bsuir.labworks.service;

import by.bsuir.labworks.config.AsyncProperties;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncConfirmationWorkerService {

  private static final Logger LOG = LoggerFactory.getLogger(AsyncConfirmationWorkerService.class);

  private final BookingService bookingService;
  private final AsyncTaskRegistryService asyncTaskRegistryService;
  private final AsyncTaskCounterService asyncTaskCounterService;
  private final long confirmationDelayMs;

  public AsyncConfirmationWorkerService(BookingService bookingService,
      AsyncTaskRegistryService asyncTaskRegistryService,
      AsyncTaskCounterService asyncTaskCounterService,
      AsyncProperties asyncProperties) {
    this.bookingService = bookingService;
    this.asyncTaskRegistryService = asyncTaskRegistryService;
    this.asyncTaskCounterService = asyncTaskCounterService;
    this.confirmationDelayMs = asyncProperties.getConfirmationDelayMs();
  }

  @Async
  public CompletableFuture<Void> runConfirmation(String taskId, Long bookingId) {
    asyncTaskRegistryService.markRunning(taskId, "Confirmation is running");
    asyncTaskCounterService.incrementRunning();
    try {
      Thread.sleep(confirmationDelayMs);
      bookingService.confirmBooking(bookingId);
      asyncTaskRegistryService.markSuccess(taskId, "Booking confirmed successfully");
      asyncTaskCounterService.incrementSucceeded();
      return CompletableFuture.completedFuture(null);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      LOG.error("Async confirmation interrupted for taskId={} bookingId={}", taskId, bookingId, ex);
      asyncTaskRegistryService.markFailed(taskId, "Task interrupted");
      asyncTaskCounterService.incrementFailed();
      return CompletableFuture.failedFuture(ex);
    } catch (Exception ex) {
      LOG.error("Async confirmation failed for taskId={} bookingId={}", taskId, bookingId, ex);
      String failureMessage = ex.getMessage() != null ? ex.getMessage()
          : ex.getClass().getSimpleName();
      asyncTaskRegistryService.markFailed(taskId, failureMessage);
      asyncTaskCounterService.incrementFailed();
      return CompletableFuture.failedFuture(ex);
    } finally {
      asyncTaskCounterService.decrementRunning();
    }
  }
}