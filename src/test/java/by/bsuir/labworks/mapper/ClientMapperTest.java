package by.bsuir.labworks.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import by.bsuir.labworks.dto.ClientRequestDto;
import by.bsuir.labworks.dto.ClientResponseDto;
import by.bsuir.labworks.entity.Client;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ClientMapperTest {

  private final ClientMapper mapper = Mappers.getMapper(ClientMapper.class);

  @Test
  void toEntityShouldMapAllFields() {
    ClientRequestDto dto = new ClientRequestDto();
    dto.setFirstName("John");
    dto.setLastName("Doe");
    dto.setEmail("john@example.com");
    dto.setPhone("+1234567890");

    Client entity = mapper.toEntity(dto);

    assertEquals("John", entity.getFirstName());
    assertEquals("Doe", entity.getLastName());
    assertEquals("john@example.com", entity.getEmail());
    assertEquals("+1234567890", entity.getPhone());
    assertNull(entity.getId());
  }

  @Test
  void toResponseDtoShouldMapAllFields() {
    Client client = new Client();
    client.setId(1L);
    client.setFirstName("Jane");
    client.setLastName("Smith");
    client.setEmail("jane@example.com");
    client.setPhone("+0987654321");

    ClientResponseDto dto = mapper.toResponseDto(client);

    assertEquals(1L, dto.getId());
    assertEquals("Jane", dto.getFirstName());
    assertEquals("Smith", dto.getLastName());
    assertEquals("jane@example.com", dto.getEmail());
    assertEquals("+0987654321", dto.getPhone());
  }
}