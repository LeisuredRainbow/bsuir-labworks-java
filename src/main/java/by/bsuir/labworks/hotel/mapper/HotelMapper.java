package by.bsuir.labworks.hotel.mapper;

import by.bsuir.labworks.hotel.dto.HotelRequestDto;
import by.bsuir.labworks.hotel.dto.HotelResponseDto;
import by.bsuir.labworks.hotel.entity.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HotelMapper {
  @Mapping(target = "id", ignore = true)
  Hotel toEntity(HotelRequestDto dto);

  HotelResponseDto toResponseDto(Hotel hotel);
}