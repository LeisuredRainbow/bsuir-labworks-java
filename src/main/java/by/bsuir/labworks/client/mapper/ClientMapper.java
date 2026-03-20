package by.bsuir.labworks.client.mapper;

import by.bsuir.labworks.client.dto.ClientRequestDto;
import by.bsuir.labworks.client.dto.ClientResponseDto;
import by.bsuir.labworks.client.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "bookings", ignore = true)
  Client toEntity(ClientRequestDto dto);

  ClientResponseDto toResponseDto(Client client);
}