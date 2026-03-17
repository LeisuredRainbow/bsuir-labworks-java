package by.bsuir.labworks.booking.mapper;

import by.bsuir.labworks.booking.dto.BookingRequestDto;
import by.bsuir.labworks.booking.dto.BookingResponseDto;
import by.bsuir.labworks.booking.entity.Booking;
import by.bsuir.labworks.client.entity.Client;
import by.bsuir.labworks.tour.entity.Tour;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface BookingMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "client", source = "clientId", qualifiedByName = "clientFromId")
  @Mapping(target = "tour", source = "tourId", qualifiedByName = "tourFromId")
  Booking toEntity(BookingRequestDto dto);

  @Mapping(target = "clientId", source = "client.id")
  @Mapping(target = "tourId", source = "tour.id")
  BookingResponseDto toResponseDto(Booking booking);

  @Named("clientFromId")
  default Client clientFromId(Long id) {
    if (id == null) {
      return null;
    }
    Client client = new Client();
    client.setId(id);
    return client;
  }

  @Named("tourFromId")
  default Tour tourFromId(Long id) {
    if (id == null) {
      return null;
    } 
    Tour tour = new Tour();
    tour.setId(id);
    return tour;
  }
}