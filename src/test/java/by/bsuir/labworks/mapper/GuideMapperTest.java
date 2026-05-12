package by.bsuir.labworks.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import by.bsuir.labworks.dto.GuideRequestDto;
import by.bsuir.labworks.dto.GuideResponseDto;
import by.bsuir.labworks.entity.Guide;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class GuideMapperTest {

  private final GuideMapper mapper = Mappers.getMapper(GuideMapper.class);

  @Test
  void toEntityShouldMapAllFields() {
    GuideRequestDto dto = new GuideRequestDto();
    dto.setFirstName("Alice");
    dto.setLastName("Johnson");
    dto.setEmail("alice@example.com");
    dto.setPhone("+111222333");
    dto.setExperienceYears(10);

    Guide entity = mapper.toEntity(dto);

    assertEquals("Alice", entity.getFirstName());
    assertEquals("Johnson", entity.getLastName());
    assertEquals("alice@example.com", entity.getEmail());
    assertEquals("+111222333", entity.getPhone());
    assertEquals(10, entity.getExperienceYears());
    assertNull(entity.getId());
  }

  @Test
  void toResponseDtoShouldMapAllFields() {
    Guide guide = new Guide();
    guide.setId(3L);
    guide.setFirstName("Bob");
    guide.setLastName("Brown");
    guide.setEmail("bob@example.com");
    guide.setPhone("+222333444");
    guide.setExperienceYears(15);

    GuideResponseDto dto = mapper.toResponseDto(guide);

    assertEquals(3L, dto.getId());
    assertEquals("Bob", dto.getFirstName());
    assertEquals("Brown", dto.getLastName());
    assertEquals("bob@example.com", dto.getEmail());
    assertEquals("+222333444", dto.getPhone());
    assertEquals(15, dto.getExperienceYears());
  }
}