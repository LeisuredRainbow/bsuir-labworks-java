package by.bsuir.labworks.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.async")
public class AsyncProperties {

  private long confirmationDelayMs = 15000;

  public long getConfirmationDelayMs() {
    return confirmationDelayMs;
  }

  public void setConfirmationDelayMs(long confirmationDelayMs) {
    this.confirmationDelayMs = confirmationDelayMs;
  }
}