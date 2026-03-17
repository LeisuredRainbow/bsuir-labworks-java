package by.bsuir.labworks.tour.mapper;

import by.bsuir.labworks.tour.dto.TourRequestDto;
import by.bsuir.labworks.tour.dto.TourResponseDto;
import by.bsuir.labworks.tour.entity.Tour;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TourMapper {

  @Mapping(target = "id", ignore = true)
  Tour toEntity(TourRequestDto dto);
    
  TourResponseDto toResponseDto(Tour tour);
}