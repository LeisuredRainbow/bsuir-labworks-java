package by.bsuir.labworks.controllers;

import by.bsuir.labworks.dto.BookingRequestDto;
import by.bsuir.labworks.dto.BookingResponseDto;
import by.bsuir.labworks.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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
@Validated
@Tag(name = "Bookings", description = "Operations with bookings")
public class BookingController {
  private final BookingService bookingService;

  @GetMapping
  @Operation(summary = "Get all bookings")
  public List<BookingResponseDto> getAllBookings() {
    return bookingService.getAllBookings();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get booking by id")
  public BookingResponseDto getBookingById(
      @Parameter(description = "Booking id", example = "1")
      @PathVariable @Positive(message = "ID must be positive") Long id) {
    return bookingService.getBookingById(id);
  }

  @GetMapping("/by-client")
  @Operation(summary = "Get bookings by client id")
  public List<BookingResponseDto> getBookingsByClientId(
      @Parameter(description = "Client id", example = "1")
      @RequestParam @Positive(message = "Client ID must be positive") Long clientId) {
    return bookingService.getBookingsByClientId(clientId);
  }

  @GetMapping("/by-tour")
  @Operation(summary = "Get bookings by tour id")
  public List<BookingResponseDto> getBookingsByTourId(
      @Parameter(description = "Tour id", example = "1")
      @RequestParam @Positive(message = "Tour ID must be positive") Long tourId) {
    return bookingService.getBookingsByTourId(tourId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create a new booking")
  public BookingResponseDto createBooking(
      @RequestBody @Valid @Parameter(description = "Booking data") BookingRequestDto bookingDto) {
    return bookingService.createBooking(bookingDto);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update existing booking")
  public BookingResponseDto updateBooking(
      @Parameter(description = "Booking id", example = "1")
      @PathVariable @Positive(message = "ID must be positive") Long id,
      @RequestBody @Valid BookingRequestDto bookingDto) {
    return bookingService.updateBooking(id, bookingDto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete booking")
  public void deleteBooking(
      @Parameter(description = "Booking id", example = "1")
      @PathVariable @Positive(message = "ID must be positive") Long id) {
    bookingService.deleteBooking(id);
  }

  @GetMapping("/search/by-client-last-name/jpql")
  @Operation(summary = "Search bookings by client last name (JPQL)")
  public Page<BookingResponseDto> searchByClientLastNameJpql(
      @Parameter(description = "Client last name", example = "Vader")
      @RequestParam @NotBlank(message = "Last name must not be blank") String lastName,
      @PageableDefault(size = 10) Pageable pageable) {
    return bookingService.searchBookingsByClientLastNameJpql(lastName, pageable);
  }

  @GetMapping("/search/by-client-last-name/native")
  @Operation(summary = "Search bookings by client last name (Native SQL)")
  public Page<BookingResponseDto> searchByClientLastNameNative(
      @Parameter(description = "Client last name", example = "Vader")
      @RequestParam @NotBlank(message = "Last name must not be blank") String lastName,
      @PageableDefault(size = 10) Pageable pageable) {
    return bookingService.searchBookingsByClientLastNameNative(lastName, pageable);
  }
}