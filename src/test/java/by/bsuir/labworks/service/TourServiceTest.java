package by.bsuir.labworks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.bsuir.labworks.dto.TourRequestDto;
import by.bsuir.labworks.dto.TourResponseDto;
import by.bsuir.labworks.entity.Guide;
import by.bsuir.labworks.entity.Hotel;
import by.bsuir.labworks.entity.Tour;
import by.bsuir.labworks.mapper.TourMapper;
import by.bsuir.labworks.repository.BookingRepository;
import by.bsuir.labworks.repository.GuideRepository;
import by.bsuir.labworks.repository.HotelRepository;
import by.bsuir.labworks.repository.TourRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TourServiceTest {

  @Mock
  private TourRepository tourRepository;

  @Mock
  private TourMapper tourMapper;

  @Mock
  private HotelRepository hotelRepository;

  @Mock
  private GuideRepository guideRepository;

  @Mock
  private BookingRepository bookingRepository;

  private TourService tourService;

  @BeforeEach
  void setUp() {
    tourService = new TourService(tourRepository, tourMapper, hotelRepository,
        guideRepository, bookingRepository);
  }

  @Test
  void getAllToursMapsEntities() {
    Tour tour = new Tour();
    TourResponseDto response = new TourResponseDto();
    when(tourRepository.findAll()).thenReturn(List.of(tour));
    when(tourMapper.toResponseDto(tour)).thenReturn(response);

    List<TourResponseDto> result = tourService.getAllTours();

    assertThat(result).containsExactly(response);
  }

  @Test
  void getToursByCountryMapsEntities() {
    Tour tour = new Tour();
    TourResponseDto response = new TourResponseDto();
    when(tourRepository.findByCountry("Japan")).thenReturn(List.of(tour));
    when(tourMapper.toResponseDto(tour)).thenReturn(response);

    List<TourResponseDto> result = tourService.getToursByCountry("Japan");

    assertThat(result).containsExactly(response);
  }

  @Test
  void getTourByIdThrowsWhenMissing() {
    when(tourRepository.findById(1L)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> tourService.getTourById(1L))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Tour not found");
  }

  @Test
  void getTourByIdMapsEntity() {
    Tour tour = new Tour();
    TourResponseDto response = new TourResponseDto();
    when(tourRepository.findById(1L)).thenReturn(java.util.Optional.of(tour));
    when(tourMapper.toResponseDto(tour)).thenReturn(response);

    TourResponseDto result = tourService.getTourById(1L);

    assertThat(result).isSameAs(response);
  }

  @Test
  void createTourSetsRelations() {
    TourRequestDto dto = new TourRequestDto();
    dto.setHotelIds(List.of(1L));
    dto.setGuideIds(List.of(2L));

    Tour entity = new Tour();
    when(tourMapper.toEntity(dto)).thenReturn(entity);

    Hotel hotel = new Hotel();
    hotel.setId(1L);
    Guide guide = new Guide();
    guide.setId(2L);

    when(hotelRepository.findAllById(dto.getHotelIds())).thenReturn(List.of(hotel));
    when(guideRepository.findAllById(dto.getGuideIds())).thenReturn(List.of(guide));

    Tour saved = new Tour();
    when(tourRepository.save(entity)).thenReturn(saved);
    when(tourMapper.toResponseDto(saved)).thenReturn(new TourResponseDto());

    tourService.createTour(dto);

    ArgumentCaptor<Tour> captor = ArgumentCaptor.forClass(Tour.class);
    verify(tourRepository).save(captor.capture());
    assertThat(captor.getValue().getHotels()).containsExactly(hotel);
    assertThat(captor.getValue().getGuides()).containsExactly(guide);
  }

  @Test
  void createTourRejectsMissingHotels() {
    TourRequestDto dto = new TourRequestDto();
    dto.setHotelIds(List.of(1L, 2L));

    when(tourMapper.toEntity(dto)).thenReturn(new Tour());
    Hotel hotel = new Hotel();
    hotel.setId(1L);
    when(hotelRepository.findAllById(dto.getHotelIds())).thenReturn(List.of(hotel));

    assertThatThrownBy(() -> tourService.createTour(dto))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Hotels not found");
  }

  @Test
  void createTourRejectsMissingGuides() {
    TourRequestDto dto = new TourRequestDto();
    dto.setHotelIds(List.of(1L));
    dto.setGuideIds(List.of(2L, 3L));

    when(tourMapper.toEntity(dto)).thenReturn(new Tour());
    Hotel hotel = new Hotel();
    hotel.setId(1L);
    when(hotelRepository.findAllById(dto.getHotelIds())).thenReturn(List.of(hotel));
    Guide guide = new Guide();
    guide.setId(2L);
    when(guideRepository.findAllById(dto.getGuideIds())).thenReturn(List.of(guide));

    assertThatThrownBy(() -> tourService.createTour(dto))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Guides not found");
  }

  @Test
  void updateTourClearsRelationsWhenEmpty() {
    TourRequestDto dto = new TourRequestDto();
    dto.setHotelIds(List.of());
    dto.setGuideIds(null);

    Tour existing = new Tour();
    existing.getHotels().add(new Hotel());
    existing.getGuides().add(new Guide());
    when(tourRepository.findById(5L)).thenReturn(java.util.Optional.of(existing));

    Tour saved = new Tour();
    when(tourRepository.save(existing)).thenReturn(saved);
    when(tourMapper.toResponseDto(saved)).thenReturn(new TourResponseDto());

    tourService.updateTour(5L, dto);

    assertThat(existing.getHotels()).isEmpty();
    assertThat(existing.getGuides()).isEmpty();
  }

  @Test
  void updateTourClearsRelationsWhenIdsAreNullAndEmpty() {
    TourRequestDto dto = new TourRequestDto();
    dto.setHotelIds(null);
    dto.setGuideIds(List.of());

    Tour existing = new Tour();
    existing.getHotels().add(new Hotel());
    existing.getGuides().add(new Guide());
    when(tourRepository.findById(6L)).thenReturn(java.util.Optional.of(existing));

    Tour saved = new Tour();
    when(tourRepository.save(existing)).thenReturn(saved);
    when(tourMapper.toResponseDto(saved)).thenReturn(new TourResponseDto());

    tourService.updateTour(6L, dto);

    assertThat(existing.getHotels()).isEmpty();
    assertThat(existing.getGuides()).isEmpty();
  }

  @Test
  void updateTourThrowsWhenMissing() {
    when(tourRepository.findById(5L)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> tourService.updateTour(5L, new TourRequestDto()))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Tour not found");
  }

  @Test
  void updateTourUsesRepositories() {
    TourRequestDto dto = new TourRequestDto();
    dto.setHotelIds(List.of(1L));
    dto.setGuideIds(List.of(2L));

    Tour existing = new Tour();
    when(tourRepository.findById(5L)).thenReturn(java.util.Optional.of(existing));

    Hotel hotel = new Hotel();
    hotel.setId(1L);
    Guide guide = new Guide();
    guide.setId(2L);

    when(hotelRepository.findAllById(dto.getHotelIds())).thenReturn(List.of(hotel));
    when(guideRepository.findAllById(dto.getGuideIds())).thenReturn(List.of(guide));

    Tour saved = new Tour();
    when(tourRepository.save(existing)).thenReturn(saved);
    when(tourMapper.toResponseDto(saved)).thenReturn(new TourResponseDto());

    tourService.updateTour(5L, dto);

    verify(tourMapper).updateEntity(dto, existing);
    verify(hotelRepository).findAllById(dto.getHotelIds());
    verify(guideRepository).findAllById(dto.getGuideIds());
  }

  @Test
  void getToursByPriceMapsEntities() {
    Tour tour = new Tour();
    TourResponseDto response = new TourResponseDto();
    BigDecimal price = new BigDecimal("10.00");
    when(tourRepository.findByPrice(price)).thenReturn(List.of(tour));
    when(tourMapper.toResponseDto(tour)).thenReturn(response);

    List<TourResponseDto> result = tourService.getToursByPrice(price);

    assertThat(result).containsExactly(response);
  }

  @Test
  void getToursByMinPriceMapsEntities() {
    Tour tour = new Tour();
    TourResponseDto response = new TourResponseDto();
    BigDecimal price = new BigDecimal("10.00");
    when(tourRepository.findByPriceGreaterThanEqual(price)).thenReturn(List.of(tour));
    when(tourMapper.toResponseDto(tour)).thenReturn(response);

    List<TourResponseDto> result = tourService.getToursByMinPrice(price);

    assertThat(result).containsExactly(response);
  }

  @Test
  void getToursByMaxPriceMapsEntities() {
    Tour tour = new Tour();
    TourResponseDto response = new TourResponseDto();
    BigDecimal price = new BigDecimal("10.00");
    when(tourRepository.findByPriceLessThanEqualWithGraph(price)).thenReturn(List.of(tour));
    when(tourMapper.toResponseDto(tour)).thenReturn(response);

    List<TourResponseDto> result = tourService.getToursByMaxPrice(price);

    assertThat(result).containsExactly(response);
  }

  @Test
  void deleteTourRejectsMissing() {
    when(tourRepository.existsById(9L)).thenReturn(false);

    assertThatThrownBy(() -> tourService.deleteTour(9L))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Tour not found");
  }

  @Test
  void deleteTourRejectsWhenBookingsExist() {
    when(tourRepository.existsById(9L)).thenReturn(true);
    when(bookingRepository.existsByTourId(9L)).thenReturn(true);

    assertThatThrownBy(() -> tourService.deleteTour(9L))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("existing bookings");

    verify(tourRepository, never()).deleteById(any());
  }

  @Test
  void deleteTourDeletesWhenAllowed() {
    when(tourRepository.existsById(9L)).thenReturn(true);
    when(bookingRepository.existsByTourId(9L)).thenReturn(false);

    tourService.deleteTour(9L);

    verify(tourRepository).deleteById(9L);
  }
}
