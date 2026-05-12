package by.bsuir.labworks.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import by.bsuir.labworks.dto.HotelRequestDto;
import by.bsuir.labworks.dto.HotelResponseDto;
import by.bsuir.labworks.entity.Hotel;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class HotelMapperTest {

  private final HotelMapper mapper = Mappers.getMapper(HotelMapper.class);

  @Test
  void toEntityShouldMapAllFields() {
    HotelRequestDto dto = new HotelRequestDto();
    dto.setName("Test Hotel");
    dto.setAddress("Test Address");
    dto.setStars(4);

    Hotel entity = mapper.toEntity(dto);

    assertEquals("Test Hotel", entity.getName());
    assertEquals("Test Address", entity.getAddress());
    assertEquals(4, entity.getStars());
    assertNull(entity.getId());
  }

  @Test
  void toResponseDtoShouldMapAllFields() {
    Hotel hotel = new Hotel();
    hotel.setId(2L);
    hotel.setName("Response Hotel");
    hotel.setAddress("Response Address");
    hotel.setStars(5);

    HotelResponseDto dto = mapper.toResponseDto(hotel);

    assertEquals(2L, dto.getId());
    assertEquals("Response Hotel", dto.getName());
    assertEquals("Response Address", dto.getAddress());
    assertEquals(5, dto.getStars());
  }
}