package by.bsuir.labworks.booking.dto;

import by.bsuir.labworks.booking.entity.Booking.BookingStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;

@Data
public class BookingRequestDto {
  private Long clientId;

  private String firstName;
  private String lastName;
  
  @NotBlank(message = "Email обязателен")
  @Email(message = "Некорректный формат email")
  @Size(max = 320, message = "Email не может превышать 320 символов")
  private String email;
  
  @Pattern(regexp = "^\\+\\d{7,15}$",
      message = "Номер телефона должен начинаться с '+' и содержать от 7 до 15 цифр")
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