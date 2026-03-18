package by.bsuir.labworks.booking.controller;

import by.bsuir.labworks.booking.dto.BookingWithClientDto;
import by.bsuir.labworks.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings/demo")
@RequiredArgsConstructor
public class BookingDemoController {
  private final BookingService bookingService;

  @PostMapping("/without-transaction")
  public String createWithoutTransaction(@RequestBody @Valid BookingWithClientDto dto) {
    try {
      bookingService.createBookingWithNewClientWithoutTransaction(dto);
    } catch (Exception e) {
      return "Error occurred: " + e.getMessage() + ". Check database for partial save.";
    }
    return "Unexpected success";
  }

  @PostMapping("/with-transaction")
  public String createWithTransaction(@RequestBody @Valid BookingWithClientDto dto) {
    try {
      bookingService.createBookingWithNewClientWithTransaction(dto);
    } catch (Exception e) {
      return "Error occurred: " + e.getMessage() + ". Transaction rolled back, no partial save.";
    }
    return "Unexpected success";
  }
}