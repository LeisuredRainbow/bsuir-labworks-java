package by.bsuir.labworks.service;

import by.bsuir.labworks.dto.RaceConditionDemoDto;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

@Service
public class RaceConditionDemoService {

  public RaceConditionDemoDto runDemo(int threads, int incrementsPerThread) {
    if (threads < 50) {
      throw new IllegalArgumentException("Threads must be >= 50 for race condition demo");
    }
    if (incrementsPerThread <= 0) {
      throw new IllegalArgumentException("incrementsPerThread must be > 0");
    }

    UnsafeCounter unsafeCounter = new UnsafeCounter();
    SynchronizedCounter synchronizedCounter = new SynchronizedCounter();
    AtomicInteger atomicCounter = new AtomicInteger();

    CountDownLatch startedLatch = new CountDownLatch(1);
    CountDownLatch completedLatch = new CountDownLatch(threads);

    try (ExecutorService executorService = Executors.newFixedThreadPool(threads)) {
      for (int threadNumber = 0; threadNumber < threads; threadNumber++) {
        executorService.execute(() -> {
          try {
            startedLatch.await();
            for (int incrementNumber = 0; incrementNumber < incrementsPerThread; incrementNumber++) {
              unsafeCounter.increment();
              synchronizedCounter.increment();
              atomicCounter.incrementAndGet();
            }
          } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
          } finally {
            completedLatch.countDown();
          }
        });
      }

      startedLatch.countDown();
      try {
        boolean completed = completedLatch.await(60, TimeUnit.SECONDS);
        if (!completed) {
          throw new IllegalStateException("Race condition demo timed out");
        }
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException("Race condition demo interrupted", ex);
      }
    }

    int expected = threads * incrementsPerThread;
    int unsafe = unsafeCounter.get();
    int synchronizedValue = synchronizedCounter.get();
    int atomic = atomicCounter.get();

    return new RaceConditionDemoDto(
        threads,
        incrementsPerThread,
        expected,
        unsafe,
        synchronizedValue,
        atomic,
        unsafe < expected && synchronizedValue == expected && atomic == expected
    );
  }

  private static class UnsafeCounter {
    private int value;

    void increment() {
      value++;
    }

    int get() {
      return value;
    }
  }

  private static class SynchronizedCounter {
    private int value;

    synchronized void increment() {
      value++;
    }

    synchronized int get() {
      return value;
    }
  }
}