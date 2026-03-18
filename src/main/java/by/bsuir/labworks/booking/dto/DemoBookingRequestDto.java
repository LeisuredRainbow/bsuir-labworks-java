package by.bsuir.labworks.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class DemoBookingRequestDto {
  @NotNull(message = "ID тура обязателен")
  private Long tourId;

  @NotNull(message = "Дата бронирования обязательна")
  @Future(message = "Дата бронирования должна быть в будущем")
  private LocalDate bookingDate;

  private String status;
}