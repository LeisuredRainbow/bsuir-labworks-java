package by.bsuir.labworks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import by.bsuir.labworks.dto.RaceConditionDemoDto;
import org.junit.jupiter.api.Test;

class RaceConditionDemoServiceTest {

  private final RaceConditionDemoService service = new RaceConditionDemoService();

  @Test
  void runDemoShouldDetectRaceAndShowCorrectness() {
    RaceConditionDemoDto result = service.runDemo(64, 10000);
    assertThat(result.getExpected()).isEqualTo(640000);
    assertThat(result.getUnsafeCounter()).isLessThan(result.getExpected());
    assertThat(result.getSynchronizedCounter()).isEqualTo(result.getExpected());
    assertThat(result.getAtomicCounter()).isEqualTo(result.getExpected());
    assertThat(result.isRaceConditionDetected()).isTrue();
  }

  @Test
  void runDemoShouldRejectThreadsBelow50() {
    assertThrows(IllegalArgumentException.class, () -> service.runDemo(49, 1000));
  }

  @Test
  void runDemoShouldRejectNonPositiveIncrements() {
    assertThrows(IllegalArgumentException.class, () -> service.runDemo(50, 0));
  }
}