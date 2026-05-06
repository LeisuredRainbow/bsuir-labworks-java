package by.bsuir.labworks.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.bsuir.labworks.dto.AsyncTaskStatusDto;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AsyncConfirmationServiceTest {

  @Mock
  private AsyncTaskRegistryService asyncTaskRegistryService;

  @Mock
  private AsyncConfirmationWorkerService asyncConfirmationWorkerService;

  @Mock
  private AsyncTaskCounterService asyncTaskCounterService;

  private AsyncConfirmationService service;

  @BeforeEach
  void setUp() {
    service = new AsyncConfirmationService(asyncTaskRegistryService,
        asyncConfirmationWorkerService, asyncTaskCounterService);
  }

  @Test
  void startConfirmationShouldRegisterAndInvokeWorker() {
    Long bookingId = 5L;
    service.startConfirmation(bookingId);
    verify(asyncTaskRegistryService).putPending(anyString());
    verify(asyncTaskCounterService).incrementSubmitted();
    verify(asyncConfirmationWorkerService).runConfirmation(anyString(), anyLong());
  }

  @Test
  void getStatusShouldDelegateToRegistry() {
    AsyncTaskStatusDto status = new AsyncTaskStatusDto("t1", "PENDING", "msg");
    when(asyncTaskRegistryService.getStatus("t1")).thenReturn(Optional.of(status));
    Optional<AsyncTaskStatusDto> result = service.getStatus("t1");
    verify(asyncTaskRegistryService).getStatus("t1");
    assert result.isPresent() && result.get().getStatus().equals("PENDING");
  }
}