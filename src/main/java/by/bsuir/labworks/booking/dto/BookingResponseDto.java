package by.bsuir.labworks.booking.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class BookingResponseDto {
  private Long id;
  private Long clientId;
  private Long tourId;
  private LocalDate bookingDate;
  private String status;
}