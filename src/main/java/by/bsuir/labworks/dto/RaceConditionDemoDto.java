package by.bsuir.labworks.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Race condition demo result")
public class RaceConditionDemoDto {

  @Schema(example = "64")
  private int threads;

  @Schema(example = "10000")
  private int incrementsPerThread;

  @Schema(example = "640000")
  private int expected;

  @Schema(example = "523418")
  private int unsafeCounter;

  @Schema(example = "640000")
  private int synchronizedCounter;

  @Schema(example = "640000")
  private int atomicCounter;

  @Schema(example = "true")
  private boolean raceConditionDetected;

  public RaceConditionDemoDto() {
  }

  public RaceConditionDemoDto(
      int threads,
      int incrementsPerThread,
      int expected,
      int unsafeCounter,
      int synchronizedCounter,
      int atomicCounter,
      boolean raceConditionDetected
  ) {
    this.threads = threads;
    this.incrementsPerThread = incrementsPerThread;
    this.expected = expected;
    this.unsafeCounter = unsafeCounter;
    this.synchronizedCounter = synchronizedCounter;
    this.atomicCounter = atomicCounter;
    this.raceConditionDetected = raceConditionDetected;
  }

  public int getThreads() {
    return threads;
  }

  public void setThreads(int threads) {
    this.threads = threads;
  }

  public int getIncrementsPerThread() {
    return incrementsPerThread;
  }

  public void setIncrementsPerThread(int incrementsPerThread) {
    this.incrementsPerThread = incrementsPerThread;
  }

  public int getExpected() {
    return expected;
  }

  public void setExpected(int expected) {
    this.expected = expected;
  }

  public int getUnsafeCounter() {
    return unsafeCounter;
  }

  public void setUnsafeCounter(int unsafeCounter) {
    this.unsafeCounter = unsafeCounter;
  }

  public int getSynchronizedCounter() {
    return synchronizedCounter;
  }

  public void setSynchronizedCounter(int synchronizedCounter) {
    this.synchronizedCounter = synchronizedCounter;
  }

  public int getAtomicCounter() {
    return atomicCounter;
  }

  public void setAtomicCounter(int atomicCounter) {
    this.atomicCounter = atomicCounter;
  }

  public boolean isRaceConditionDetected() {
    return raceConditionDetected;
  }

  public void setRaceConditionDetected(boolean raceConditionDetected) {
    this.raceConditionDetected = raceConditionDetected;
  }
}