package by.bsuir.labworks.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class AsyncPropertiesTest {

  @Test
  void shouldHaveDefaultDelay() {
    AsyncProperties properties = new AsyncProperties();
    assertEquals(15000L, properties.getConfirmationDelayMs());
  }

  @Test
  void shouldSetDelay() {
    AsyncProperties properties = new AsyncProperties();
    properties.setConfirmationDelayMs(5000L);
    assertEquals(5000L, properties.getConfirmationDelayMs());
  }
}