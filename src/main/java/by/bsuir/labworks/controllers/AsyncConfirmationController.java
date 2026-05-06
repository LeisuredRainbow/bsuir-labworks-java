package by.bsuir.labworks.controllers;

import by.bsuir.labworks.dto.AsyncTaskMetricsDto;
import by.bsuir.labworks.dto.AsyncTaskStatusDto;
import by.bsuir.labworks.dto.AsyncTaskSubmissionDto;
import by.bsuir.labworks.service.AsyncConfirmationService;
import by.bsuir.labworks.service.AsyncTaskCounterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/async/confirm")
@Validated
@Tag(name = "Async Confirmation", description = "Async booking confirmation operation")
public class AsyncConfirmationController {

  private final AsyncConfirmationService asyncConfirmationService;
  private final AsyncTaskCounterService asyncTaskCounterService;

  public AsyncConfirmationController(AsyncConfirmationService asyncConfirmationService,
      AsyncTaskCounterService asyncTaskCounterService) {
    this.asyncConfirmationService = asyncConfirmationService;
    this.asyncTaskCounterService = asyncTaskCounterService;
  }

  @PostMapping
  @Operation(summary = "Start async booking confirmation")
  public ResponseEntity<AsyncTaskSubmissionDto> start(
      @Parameter(description = "Booking id", example = "1")
      @RequestParam @NotNull(message = "Booking ID must not be null")
      @Positive(message = "Booking ID must be positive") Long bookingId) {
    String taskId = asyncConfirmationService.startConfirmation(bookingId);
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(new AsyncTaskSubmissionDto(taskId));
  }

  @GetMapping("/{taskId}")
  @Operation(summary = "Get async confirmation status")
  public ResponseEntity<AsyncTaskStatusDto> status(@PathVariable String taskId) {
    return ResponseEntity.ok(asyncConfirmationService.getStatus(taskId).orElseThrow());
  }

  @GetMapping("/metrics")
  @Operation(summary = "Get async confirmation metrics")
  public ResponseEntity<AsyncTaskMetricsDto> metrics() {
    return ResponseEntity.ok(asyncTaskCounterService.getMetrics());
  }
}