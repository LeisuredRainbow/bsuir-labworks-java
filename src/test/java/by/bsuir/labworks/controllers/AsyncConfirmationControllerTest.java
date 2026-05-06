package by.bsuir.labworks.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import by.bsuir.labworks.dto.AsyncTaskMetricsDto;
import by.bsuir.labworks.dto.AsyncTaskStatusDto;
import by.bsuir.labworks.service.AsyncConfirmationService;
import by.bsuir.labworks.service.AsyncTaskCounterService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AsyncConfirmationControllerTest {

  private MockMvc mockMvc;

  @Mock
  private AsyncConfirmationService asyncConfirmationService;

  @Mock
  private AsyncTaskCounterService asyncTaskCounterService;

  @InjectMocks
  private AsyncConfirmationController controller;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  void startShouldReturnTaskId() throws Exception {
    when(asyncConfirmationService.startConfirmation(1L)).thenReturn("task-123");
    mockMvc.perform(post("/async/confirm?bookingId=1"))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.taskId").value("task-123"));
  }

  @Test
  void statusShouldReturnTaskStatus() throws Exception {
    AsyncTaskStatusDto statusDto = new AsyncTaskStatusDto("task-123", "PENDING", "message");
    when(asyncConfirmationService.getStatus("task-123")).thenReturn(Optional.of(statusDto));
    mockMvc.perform(get("/async/confirm/task-123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("PENDING"));
  }

  @Test
  void metricsShouldReturnCounterValues() throws Exception {
    AsyncTaskMetricsDto metrics = new AsyncTaskMetricsDto(1, 2, 3, 4);
    when(asyncTaskCounterService.getMetrics()).thenReturn(metrics);
    mockMvc.perform(get("/async/confirm/metrics"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.submitted").value(1))
        .andExpect(jsonPath("$.running").value(2))
        .andExpect(jsonPath("$.succeeded").value(3))
        .andExpect(jsonPath("$.failed").value(4));
  }
}