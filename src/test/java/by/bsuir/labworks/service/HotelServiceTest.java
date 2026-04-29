package by.bsuir.labworks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import by.bsuir.labworks.dto.HotelRequestDto;
import by.bsuir.labworks.dto.HotelResponseDto;
import by.bsuir.labworks.entity.Hotel;
import by.bsuir.labworks.mapper.HotelMapper;
import by.bsuir.labworks.repository.HotelRepository;
import by.bsuir.labworks.repository.TourRepository;
import java.util.Optional;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

  @Mock
  private HotelRepository hotelRepository;

  @Mock
  private HotelMapper hotelMapper;

  @Mock
  private TourRepository tourRepository;

  private HotelService hotelService;

  @BeforeEach
  void setUp() {
    hotelService = new HotelService(hotelRepository, hotelMapper, tourRepository);
  }

  @Test
  void getAllHotelsMapsEntities() {
    Hotel hotel = new Hotel();
    HotelResponseDto response = new HotelResponseDto();
    when(hotelRepository.findAll()).thenReturn(List.of(hotel));
    when(hotelMapper.toResponseDto(hotel)).thenReturn(response);

    List<HotelResponseDto> result = hotelService.getAllHotels();

    assertThat(result).containsExactly(response);
  }

  @Test
  void getHotelByIdThrowsWhenMissing() {
    when(hotelRepository.findById(1L)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> hotelService.getHotelById(1L))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Hotel not found");
  }

  @Test
  void getHotelByIdMapsEntity() {
    Hotel hotel = new Hotel();
    HotelResponseDto response = new HotelResponseDto();
    when(hotelRepository.findById(1L)).thenReturn(java.util.Optional.of(hotel));
    when(hotelMapper.toResponseDto(hotel)).thenReturn(response);

    HotelResponseDto result = hotelService.getHotelById(1L);

    assertThat(result).isSameAs(response);
  }

  @Test
  void getHotelByAddressThrowsWhenMissing() {
    when(hotelRepository.findByAddress("addr")).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> hotelService.getHotelByAddress("addr"))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Hotel not found with address");
  }

  @Test
  void getHotelByAddressMapsEntity() {
    Hotel hotel = new Hotel();
    HotelResponseDto response = new HotelResponseDto();
    when(hotelRepository.findByAddress("addr")).thenReturn(java.util.Optional.of(hotel));
    when(hotelMapper.toResponseDto(hotel)).thenReturn(response);

    HotelResponseDto result = hotelService.getHotelByAddress("addr");

    assertThat(result).isSameAs(response);
  }

  @Test
  void createHotelRejectsDuplicateAddress() {
    HotelRequestDto dto = new HotelRequestDto();
    dto.setAddress("addr");

    when(hotelRepository.findByAddress("addr"))
        .thenReturn(java.util.Optional.of(new Hotel()));

    assertThatThrownBy(() -> hotelService.createHotel(dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("already exists");
  }

  @Test
  void createHotelSavesAndMaps() {
    HotelRequestDto dto = new HotelRequestDto();
    dto.setAddress("addr");

    when(hotelRepository.findByAddress("addr")).thenReturn(java.util.Optional.empty());
    Hotel entity = new Hotel();
    Hotel saved = new Hotel();
    saved.setId(2L);
    HotelResponseDto response = new HotelResponseDto();
    when(hotelMapper.toEntity(dto)).thenReturn(entity);
    when(hotelRepository.save(entity)).thenReturn(saved);
    when(hotelMapper.toResponseDto(saved)).thenReturn(response);

    HotelResponseDto result = hotelService.createHotel(dto);

    assertThat(result).isSameAs(response);
  }

  @Test
  void updateHotelRejectsMissing() {
    when(hotelRepository.findById(3L)).thenReturn(Optional.empty());
    HotelRequestDto requestDto = new HotelRequestDto();
    assertThatThrownBy(() -> hotelService.updateHotel(3L, requestDto))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Hotel not found");
}

  @Test
  void updateHotelRejectsDuplicateAddress() {
    Hotel existing = new Hotel();
    existing.setAddress("old");
    when(hotelRepository.findById(3L)).thenReturn(java.util.Optional.of(existing));

    HotelRequestDto dto = new HotelRequestDto();
    dto.setAddress("new");

    when(hotelRepository.findByAddress("new"))
        .thenReturn(java.util.Optional.of(new Hotel()));

    assertThatThrownBy(() -> hotelService.updateHotel(3L, dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("already exists");
  }

  @Test
  void updateHotelSavesAndMaps() {
    Hotel existing = new Hotel();
    existing.setAddress("old");
    when(hotelRepository.findById(3L)).thenReturn(Optional.of(existing));

    HotelRequestDto dto = new HotelRequestDto();
    dto.setAddress("old");

    Hotel saved = new Hotel();
    when(hotelRepository.save(existing)).thenReturn(saved);
    when(hotelMapper.toResponseDto(saved)).thenReturn(new HotelResponseDto());

    HotelResponseDto result = hotelService.updateHotel(3L, dto);
    assertThat(result).isNotNull();
    verify(hotelRepository, never()).findByAddress(any());
}

  @Test
  void deleteHotelRejectsMissing() {
    when(hotelRepository.findById(6L)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> hotelService.deleteHotel(6L))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Hotel not found");
  }

  @Test
  void deleteHotelRemovesRelationsAndDeletes() {
    Hotel hotel = new Hotel();
    when(hotelRepository.findById(6L)).thenReturn(java.util.Optional.of(hotel));

    hotelService.deleteHotel(6L);

    verify(tourRepository).removeHotelFromAllTours(6L);
    verify(hotelRepository).delete(hotel);
  }
  @Test
  void createHotelWithNullAddressSkipsDuplicateCheck() {
    HotelRequestDto dto = new HotelRequestDto();
    Hotel entity = new Hotel();
    Hotel saved = new Hotel();
    HotelResponseDto response = new HotelResponseDto();

    when(hotelMapper.toEntity(dto)).thenReturn(entity);
    when(hotelRepository.save(entity)).thenReturn(saved);
    when(hotelMapper.toResponseDto(saved)).thenReturn(response);

    HotelResponseDto result = hotelService.createHotel(dto);

    assertThat(result).isSameAs(response);
  }

  @Test
  void updateHotelWithNewUniqueAddressSaves() {
    Hotel existing = new Hotel();
    existing.setAddress("old");
    when(hotelRepository.findById(3L)).thenReturn(java.util.Optional.of(existing));

    HotelRequestDto dto = new HotelRequestDto();
    dto.setAddress("new");

    when(hotelRepository.findByAddress("new")).thenReturn(java.util.Optional.empty());
    Hotel saved = new Hotel();
    when(hotelRepository.save(existing)).thenReturn(saved);
    when(hotelMapper.toResponseDto(saved)).thenReturn(new HotelResponseDto());

    hotelService.updateHotel(3L, dto);

    verify(hotelRepository).findByAddress("new");
  }

  @Test
  void updateHotelWithNullAddressSkipsDuplicateCheck() {
    Hotel existing = new Hotel();
    existing.setAddress("old");
    when(hotelRepository.findById(3L)).thenReturn(Optional.of(existing));

    HotelRequestDto dto = new HotelRequestDto();
    dto.setAddress(null);

    Hotel saved = new Hotel();
    when(hotelRepository.save(existing)).thenReturn(saved);
    when(hotelMapper.toResponseDto(saved)).thenReturn(new HotelResponseDto());

    HotelResponseDto result = hotelService.updateHotel(3L, dto);
    assertThat(result).isNotNull();
    verify(hotelRepository, never()).findByAddress(any());
  }

}
