package by.bsuir.labworks.mapper;

import by.bsuir.labworks.dto.ClientRequestDto;
import by.bsuir.labworks.dto.ClientResponseDto;
import by.bsuir.labworks.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "bookings", ignore = true)
  Client toEntity(ClientRequestDto dto);

  ClientResponseDto toResponseDto(Client client);
}