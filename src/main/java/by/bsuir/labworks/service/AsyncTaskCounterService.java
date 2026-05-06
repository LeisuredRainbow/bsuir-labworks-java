package by.bsuir.labworks.service;

import by.bsuir.labworks.dto.AsyncTaskMetricsDto;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class AsyncTaskCounterService {

  private final AtomicLong submitted = new AtomicLong();
  private final AtomicLong running = new AtomicLong();
  private final AtomicLong succeeded = new AtomicLong();
  private final AtomicLong failed = new AtomicLong();

  public void incrementSubmitted() {
    submitted.incrementAndGet();
  }

  public void incrementRunning() {
    running.incrementAndGet();
  }

  public void decrementRunning() {
    long current;
    long next;
    do {
      current = running.get();
      if (current == 0) {
        return;
      }
      next = current - 1;
    } while (!running.compareAndSet(current, next));
  }

  public void incrementSucceeded() {
    succeeded.incrementAndGet();
  }

  public void incrementFailed() {
    failed.incrementAndGet();
  }

  public AsyncTaskMetricsDto getMetrics() {
    return new AsyncTaskMetricsDto(
        submitted.get(),
        running.get(),
        succeeded.get(),
        failed.get()
    );
  }
}