package by.bsuir.labworks.booking.controller;

import by.bsuir.labworks.booking.dto.BookingRequestDto;
import by.bsuir.labworks.booking.dto.BookingResponseDto;
import by.bsuir.labworks.booking.service.BookingService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
  private final BookingService bookingService;

  @GetMapping
  public List<BookingResponseDto> getAllBookings() {
    return bookingService.getAllBookings();
  }

  @GetMapping("/{id}")
  public BookingResponseDto getBookingById(@PathVariable Long id) {
    return bookingService.getBookingById(id);
  }

  @GetMapping("/by-client")
  public List<BookingResponseDto> getBookingsByClientId(@RequestParam Long clientId) {
    return bookingService.getBookingsByClientId(clientId);
  }

  @GetMapping("/by-tour")
  public List<BookingResponseDto> getBookingsByTourId(@RequestParam Long tourId) {
    return bookingService.getBookingsByTourId(tourId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public BookingResponseDto createBooking(@RequestBody @Valid BookingRequestDto bookingDto) {
    return bookingService.createBooking(bookingDto);
  }

  @PutMapping("/{id}")
  public BookingResponseDto updateBooking(@PathVariable Long id,
                                          @RequestBody @Valid BookingRequestDto bookingDto) {
    return bookingService.updateBooking(id, bookingDto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteBooking(@PathVariable Long id) {
    bookingService.deleteBooking(id);
  }
}