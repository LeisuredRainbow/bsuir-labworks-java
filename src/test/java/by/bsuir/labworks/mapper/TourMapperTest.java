package by.bsuir.labworks.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import by.bsuir.labworks.dto.TourRequestDto;
import by.bsuir.labworks.dto.TourResponseDto;
import by.bsuir.labworks.entity.Hotel;
import by.bsuir.labworks.entity.Guide;
import by.bsuir.labworks.entity.Tour;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class TourMapperTest {

  private final TourMapper mapper = Mappers.getMapper(TourMapper.class);

  @Test
  void toEntityShouldMapBasicFields() {
    TourRequestDto dto = new TourRequestDto();
    dto.setName("Tour");
    dto.setCountry("Country");
    dto.setPrice(BigDecimal.valueOf(100));
    dto.setDurationDays(3);
    dto.setHot(true);
    dto.setDescription("Desc");

    Tour entity = mapper.toEntity(dto);

    assertEquals("Tour", entity.getName());
    assertEquals("Country", entity.getCountry());
    assertEquals(BigDecimal.valueOf(100), entity.getPrice());
    assertEquals(3, entity.getDurationDays());
    assertTrue(entity.getHot());
    assertEquals("Desc", entity.getDescription());
  }

  @Test
  void toResponseDtoShouldMapFieldsAndIds() {
    Tour tour = new Tour();
    tour.setId(10L);
    tour.setName("Resp Tour");
    tour.setCountry("Resp Country");
    tour.setPrice(BigDecimal.valueOf(200));
    tour.setDurationDays(5);
    tour.setHot(false);
    tour.setDescription("Desc2");

    Hotel hotel1 = new Hotel();
    hotel1.setId(1L);
    Hotel hotel2 = new Hotel();
    hotel2.setId(2L);
    Set<Hotel> hotels = new HashSet<>();
    hotels.add(hotel1);
    hotels.add(hotel2);
    tour.setHotels(hotels);

    Guide guide = new Guide();
    guide.setId(3L);
    Set<Guide> guides = new HashSet<>();
    guides.add(guide);
    tour.setGuides(guides);

    TourResponseDto dto = mapper.toResponseDto(tour);

    assertEquals(10L, dto.getId());
    assertEquals("Resp Tour", dto.getName());
    assertEquals("Resp Country", dto.getCountry());
    assertEquals(BigDecimal.valueOf(200), dto.getPrice());
    assertEquals(5, dto.getDurationDays());
    assertEquals(false, dto.getHot());
    assertEquals("Desc2", dto.getDescription());
    assertNotNull(dto.getHotelIds());
    assertEquals(2, dto.getHotelIds().size());
    assertNotNull(dto.getGuideIds());
    assertEquals(1, dto.getGuideIds().size());
  }
}