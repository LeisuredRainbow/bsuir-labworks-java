package by.bsuir.labworks.dto;

import by.bsuir.labworks.entity.Booking.BookingStatus;
import java.time.LocalDate;
import lombok.Data;

@Data
public class BookingResponseDto {
  private Long id;
  private Long clientId;
  private Long tourId;
  private LocalDate bookingDate;
  private BookingStatus status;
}