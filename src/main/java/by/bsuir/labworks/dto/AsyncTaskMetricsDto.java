package by.bsuir.labworks.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Async task counters")
public class AsyncTaskMetricsDto {

  @Schema(example = "10")
  private long submitted;

  @Schema(example = "2")
  private long running;

  @Schema(example = "7")
  private long succeeded;

  @Schema(example = "1")
  private long failed;

  public AsyncTaskMetricsDto() {
  }

  public AsyncTaskMetricsDto(long submitted, long running, long succeeded, long failed) {
    this.submitted = submitted;
    this.running = running;
    this.succeeded = succeeded;
    this.failed = failed;
  }

  public long getSubmitted() {
    return submitted;
  }

  public void setSubmitted(long submitted) {
    this.submitted = submitted;
  }

  public long getRunning() {
    return running;
  }

  public void setRunning(long running) {
    this.running = running;
  }

  public long getSucceeded() {
    return succeeded;
  }

  public void setSucceeded(long succeeded) {
    this.succeeded = succeeded;
  }

  public long getFailed() {
    return failed;
  }

  public void setFailed(long failed) {
    this.failed = failed;
  }
}