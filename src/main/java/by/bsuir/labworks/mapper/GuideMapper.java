package by.bsuir.labworks.mapper;

import by.bsuir.labworks.dto.GuideRequestDto;
import by.bsuir.labworks.dto.GuideResponseDto;
import by.bsuir.labworks.entity.Guide;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GuideMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "tours", ignore = true)
  Guide toEntity(GuideRequestDto dto);

  GuideResponseDto toResponseDto(Guide guide);
}