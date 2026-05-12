package by.bsuir.labworks.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import by.bsuir.labworks.entity.Client;
import by.bsuir.labworks.entity.Tour;
import by.bsuir.labworks.dto.BookingRequestDto;
import by.bsuir.labworks.dto.BookingResponseDto;
import by.bsuir.labworks.entity.Booking;
import by.bsuir.labworks.entity.Booking.BookingStatus;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class BookingMapperTest {

  private final BookingMapper mapper = Mappers.getMapper(BookingMapper.class);

  @Test
  void toEntityShouldMapFields() {
    BookingRequestDto dto = new BookingRequestDto();
    dto.setClientId(5L);
    dto.setTourId(6L);
    dto.setBookingDate(LocalDate.of(2026, 8, 20));
    dto.setStatus(BookingStatus.CONFIRMED);

    Booking entity = mapper.toEntity(dto);

    assertEquals(5L, entity.getClient().getId());
    assertEquals(6L, entity.getTour().getId());
    assertEquals(LocalDate.of(2026, 8, 20), entity.getBookingDate());
    assertEquals(BookingStatus.CONFIRMED, entity.getStatus());
  }

  @Test
  void toResponseDtoShouldMapFields() {
    Booking booking = new Booking();
    booking.setId(99L);
    Client client = new Client();
    client.setId(7L);
    Tour tour = new Tour();
    tour.setId(8L);
    booking.setClient(client);
    booking.setTour(tour);
    booking.setBookingDate(LocalDate.of(2026, 9, 1));
    booking.setStatus(BookingStatus.PENDING);

    BookingResponseDto dto = mapper.toResponseDto(booking);

    assertEquals(99L, dto.getId());
    assertEquals(7L, dto.getClientId());
    assertEquals(8L, dto.getTourId());
    assertEquals(LocalDate.of(2026, 9, 1), dto.getBookingDate());
    assertEquals(BookingStatus.PENDING, dto.getStatus());
  }
}