package by.bsuir.labworks.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.async.confirmation-delay-ms=15000")
class AsyncPropertiesTest {

  @Autowired
  private AsyncProperties asyncProperties;

  @Test
  void shouldLoadDefaultDelay() {
    assertThat(asyncProperties.getConfirmationDelayMs()).isEqualTo(15000);
  }
}