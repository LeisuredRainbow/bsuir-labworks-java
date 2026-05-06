package by.bsuir.labworks.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AsyncTaskRegistryServiceTest {

  private AsyncTaskRegistryService registry;

  @BeforeEach
  void setUp() {
    registry = new AsyncTaskRegistryService();
  }

  @Test
  void putPendingShouldStoreTask() {
    registry.putPending("task-1");
    assertThat(registry.getStatus("task-1")).isPresent();
    assertThat(registry.getStatus("task-1").get().getStatus()).isEqualTo("PENDING");
  }

  @Test
  void markRunningShouldUpdateStatus() {
    registry.putPending("task-1");
    registry.markRunning("task-1", "Running message");
    assertThat(registry.getStatus("task-1").get().getStatus()).isEqualTo("RUNNING");
    assertThat(registry.getStatus("task-1").get().getMessage()).isEqualTo("Running message");
  }

  @Test
  void markSuccessShouldUpdateStatus() {
    registry.putPending("task-2");
    registry.markSuccess("task-2", "Success message");
    assertThat(registry.getStatus("task-2").get().getStatus()).isEqualTo("SUCCESS");
    assertThat(registry.getStatus("task-2").get().getMessage()).isEqualTo("Success message");
  }

  @Test
  void markFailedShouldUpdateStatus() {
    registry.putPending("task-3");
    registry.markFailed("task-3", "Fail message");
    assertThat(registry.getStatus("task-3").get().getStatus()).isEqualTo("FAILED");
    assertThat(registry.getStatus("task-3").get().getMessage()).isEqualTo("Fail message");
  }

  @Test
  void getStatusForNonExistingTaskShouldReturnEmpty() {
    assertThat(registry.getStatus("non-existing")).isEmpty();
  }

  @Test
  void markRunningOnNonExistingShouldNotThrow() {
    registry.markRunning("non-existing", "msg");
    assertThat(registry.getStatus("non-existing")).isEmpty();
  }
}