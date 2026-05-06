package by.bsuir.labworks.service;

import by.bsuir.labworks.dto.AsyncTaskStatusDto;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AsyncConfirmationService {

  private final AsyncTaskRegistryService asyncTaskRegistryService;
  private final AsyncConfirmationWorkerService asyncConfirmationWorkerService;
  private final AsyncTaskCounterService asyncTaskCounterService;

  public AsyncConfirmationService(AsyncTaskRegistryService asyncTaskRegistryService,
      AsyncConfirmationWorkerService asyncConfirmationWorkerService,
      AsyncTaskCounterService asyncTaskCounterService) {
    this.asyncTaskRegistryService = asyncTaskRegistryService;
    this.asyncConfirmationWorkerService = asyncConfirmationWorkerService;
    this.asyncTaskCounterService = asyncTaskCounterService;
  }

  public String startConfirmation(Long bookingId) {
    String taskId = UUID.randomUUID().toString();
    asyncTaskRegistryService.putPending(taskId);
    asyncTaskCounterService.incrementSubmitted();
    asyncConfirmationWorkerService.runConfirmation(taskId, bookingId);
    return taskId;
  }

  public Optional<AsyncTaskStatusDto> getStatus(String taskId) {
    return asyncTaskRegistryService.getStatus(taskId);
  }
}