package by.bsuir.labworks.service;

import by.bsuir.labworks.dto.AsyncTaskStatusDto;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class AsyncTaskRegistryService {

  private final ConcurrentHashMap<String, AsyncTaskStatusDto> tasks = new ConcurrentHashMap<>();

  public void putPending(String taskId) {
    tasks.put(taskId, new AsyncTaskStatusDto(taskId, "PENDING", "Task accepted"));
  }

  public Optional<AsyncTaskStatusDto> getStatus(String taskId) {
    return Optional.ofNullable(tasks.get(taskId));
  }

  public void markRunning(String taskId, String message) {
    tasks.computeIfPresent(taskId, (id, oldStatus) ->
        new AsyncTaskStatusDto(id, "RUNNING", message));
  }

  public void markSuccess(String taskId, String message) {
    tasks.computeIfPresent(taskId, (id, oldStatus) ->
        new AsyncTaskStatusDto(id, "SUCCESS", message));
  }

  public void markFailed(String taskId, String message) {
    tasks.computeIfPresent(taskId, (id, oldStatus) ->
        new AsyncTaskStatusDto(id, "FAILED", message));
  }
}