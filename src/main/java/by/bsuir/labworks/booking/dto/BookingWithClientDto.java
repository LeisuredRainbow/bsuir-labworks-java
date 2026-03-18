package by.bsuir.labworks.booking.dto;

import by.bsuir.labworks.client.dto.ClientRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingWithClientDto {
  @NotNull(message = "Данные клиента обязательны")
  @Valid
  private ClientRequestDto client;

  @NotNull(message = "Данные бронирования обязательны")
  @Valid
  private DemoBookingRequestDto booking;
}