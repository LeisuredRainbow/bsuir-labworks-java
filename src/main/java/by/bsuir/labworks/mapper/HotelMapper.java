package by.bsuir.labworks.mapper;

import by.bsuir.labworks.dto.HotelRequestDto;
import by.bsuir.labworks.dto.HotelResponseDto;
import by.bsuir.labworks.entity.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HotelMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "tours", ignore = true)
  Hotel toEntity(HotelRequestDto dto);

  HotelResponseDto toResponseDto(Hotel hotel);
}