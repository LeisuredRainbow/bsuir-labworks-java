package by.bsuir.labworks.guide.mapper;

import by.bsuir.labworks.guide.dto.GuideRequestDto;
import by.bsuir.labworks.guide.dto.GuideResponseDto;
import by.bsuir.labworks.guide.entity.Guide;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GuideMapper {
  @Mapping(target = "id", ignore = true)
  Guide toEntity(GuideRequestDto dto);

  GuideResponseDto toResponseDto(Guide guide);
}