package by.bsuir.labworks.tour.mapper;

import by.bsuir.labworks.tour.dto.TourRequestDto;
import by.bsuir.labworks.tour.dto.TourResponseDto;
import by.bsuir.labworks.tour.entity.Tour;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TourMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "hotels", ignore = true)
  @Mapping(target = "guides", ignore = true)
  @Mapping(target = "bookings", ignore = true)
  Tour toEntity(TourRequestDto dto);

  @Mapping(target = "hotelIds", expression
      = "java(tour.getHotels().stream().map(hotel -> hotel.getId()).toList())")
  @Mapping(target = "guideIds", expression
      = "java(tour.getGuides().stream().map(guide -> guide.getId()).toList())")
  TourResponseDto toResponseDto(Tour tour);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "hotels", ignore = true)
  @Mapping(target = "guides", ignore = true)
  @Mapping(target = "bookings", ignore = true)
  void updateEntity(TourRequestDto dto, @MappingTarget Tour tour);
}