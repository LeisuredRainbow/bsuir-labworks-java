package by.bsuir.labworks.service;

import static org.assertj.core.api.Assertions.assertThat;

import by.bsuir.labworks.dto.AsyncTaskMetricsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AsyncTaskCounterServiceTest {

  private AsyncTaskCounterService counterService;

  @BeforeEach
  void setUp() {
    counterService = new AsyncTaskCounterService();
  }

  @Test
  void shouldIncrementSubmitted() {
    counterService.incrementSubmitted();
    counterService.incrementSubmitted();
    AsyncTaskMetricsDto metrics = counterService.getMetrics();
    assertThat(metrics.getSubmitted()).isEqualTo(2);
  }

  @Test
  void shouldIncrementRunningAndDecrement() {
    counterService.incrementRunning();
    counterService.incrementRunning();
    AsyncTaskMetricsDto metrics = counterService.getMetrics();
    assertThat(metrics.getRunning()).isEqualTo(2);
    counterService.decrementRunning();
    metrics = counterService.getMetrics();
    assertThat(metrics.getRunning()).isEqualTo(1);
    counterService.decrementRunning();
    metrics = counterService.getMetrics();
    assertThat(metrics.getRunning()).isZero();
    counterService.decrementRunning();
    metrics = counterService.getMetrics();
    assertThat(metrics.getRunning()).isZero();
  }

  @Test
  void shouldIncrementSucceededAndFailed() {
    counterService.incrementSucceeded();
    counterService.incrementFailed();
    AsyncTaskMetricsDto metrics = counterService.getMetrics();
    assertThat(metrics.getSucceeded()).isEqualTo(1);
    assertThat(metrics.getFailed()).isEqualTo(1);
  }

  @Test
  void initialMetricsShouldBeZero() {
    AsyncTaskMetricsDto metrics = counterService.getMetrics();
    assertThat(metrics.getSubmitted()).isZero();
    assertThat(metrics.getRunning()).isZero();
    assertThat(metrics.getSucceeded()).isZero();
    assertThat(metrics.getFailed()).isZero();
  }
}