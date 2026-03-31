package by.bsuir.labworks.booking.dto;

import by.bsuir.labworks.booking.entity.Booking.BookingStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Data;

@Data
public class BookingRequestDto {
  private Long clientId;

  private String firstName;
  private String lastName;
  private String email;
  @Pattern(regexp = "^\\+375[\\s-]?\\(\\d{2}\\)[\\s-]?\\d{3}[\\s-]?\\d{2}[\\s-]?\\d{2}$",
         message = "Некорректный формат телефона (ожидается +375(xx)xxx-xx-xx)")
  private String phone;

  @NotNull(message = "ID тура обязателен")
  private Long tourId;

  @NotNull(message = "Дата бронирования обязательна")
  @Future(message = "Дата бронирования должна быть в будущем")
  private LocalDate bookingDate;

  @NotNull(message = "Статус обязателен")
  private BookingStatus status;

  public boolean isNewClient() {
    return clientId == null && firstName != null && lastName != null && email != null;
  }

  public boolean isValid() {
    return (clientId != null) || isNewClient();
  }
}