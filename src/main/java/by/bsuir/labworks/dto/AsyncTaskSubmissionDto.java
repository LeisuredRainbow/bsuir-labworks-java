package by.bsuir.labworks.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Async task submit response")
public class AsyncTaskSubmissionDto {

  @Schema(example = "4c76e2ff-b52e-4573-9d97-86827a37f91a")
  private String taskId;

  public AsyncTaskSubmissionDto() {
  }

  public AsyncTaskSubmissionDto(String taskId) {
    this.taskId = taskId;
  }

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }
}