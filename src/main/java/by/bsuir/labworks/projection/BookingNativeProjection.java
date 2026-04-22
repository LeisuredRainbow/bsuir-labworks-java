package by.bsuir.labworks.projection;

import by.bsuir.labworks.entity.Booking.BookingStatus;
import java.time.LocalDate;

public interface BookingNativeProjection {
  Long getId();
  
  LocalDate getBookingDate();
  
  Long getClientId();
  
  Long getTourId();
  
  BookingStatus getStatus();
}