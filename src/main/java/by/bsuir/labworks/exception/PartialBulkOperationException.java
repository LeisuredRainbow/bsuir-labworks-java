package by.bsuir.labworks.exception;

import java.util.Map;

public class PartialBulkOperationException extends RuntimeException {
  private final int savedCount;
  private final int failedCount;
  private final Map<String, String> failedOperations;

  public PartialBulkOperationException(
      String message,
      int savedCount,
      int failedCount,
      Map<String, String> failedOperations) {
    super(message);
    this.savedCount = savedCount;
    this.failedCount = failedCount;
    this.failedOperations = Map.copyOf(failedOperations);
  }

  public int getSavedCount() {
    return savedCount;
  }

  public int getFailedCount() {
    return failedCount;
  }

  public Map<String, String> getFailedOperations() {
    return failedOperations;
  }
}