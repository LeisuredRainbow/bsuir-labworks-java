package by.bsuir.labworks.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Async task status")
public class AsyncTaskStatusDto {

  @Schema(example = "4c76e2ff-b52e-4573-9d97-86827a37f91a")
  private String taskId;

  @Schema(example = "RUNNING")
  private String status;

  @Schema(example = "Task is running")
  private String message;

  public AsyncTaskStatusDto() {
  }

  public AsyncTaskStatusDto(String taskId, String status, String message) {
    this.taskId = taskId;
    this.status = status;
    this.message = message;
  }

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}