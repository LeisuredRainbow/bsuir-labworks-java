package by.bsuir.labworks.service;

import by.bsuir.labworks.cache.TourSearchCache;
import by.bsuir.labworks.cache.TourSearchKey;
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
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TourService {
  private final TourRepository tourRepository;
  private final TourMapper tourMapper;
  private final HotelRepository hotelRepository;
  private final GuideRepository guideRepository;
  private final BookingRepository bookingRepository;
  private final TourSearchCache tourSearchCache;

  public List<TourResponseDto> getAllTours() {
    return tourRepository.findAll().stream()
        .map(tourMapper::toResponseDto)
        .toList();
  }

  public List<TourResponseDto> getToursByCountry(String country) {
    return tourRepository.findByCountry(country).stream()
        .map(tourMapper::toResponseDto)
        .toList();
  }

  public TourResponseDto getTourById(Long id) {
    Tour tour = tourRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Tour not found with id: " + id));
    return tourMapper.toResponseDto(tour);
  }

  public TourResponseDto createTour(TourRequestDto tourDto) {
    Tour tour = tourMapper.toEntity(tourDto);
    setHotelAndGuideRelations(tour, tourDto);
    tour = tourRepository.save(tour);
    tourSearchCache.invalidateAll();
    return tourMapper.toResponseDto(tour);
  }

  @Transactional
  public TourResponseDto updateTour(Long id, TourRequestDto tourDto) {
    Tour existingTour = tourRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Tour not found with id: " + id));
    tourMapper.updateEntity(tourDto, existingTour);
    setHotelAndGuideRelations(existingTour, tourDto);
    existingTour = tourRepository.save(existingTour);
    tourSearchCache.invalidateAll();
    return tourMapper.toResponseDto(existingTour);
  }

  private void setHotelAndGuideRelations(Tour tour, TourRequestDto dto) {
    if (dto.getHotelIds() != null && !dto.getHotelIds().isEmpty()) {
      List<Hotel> hotels = hotelRepository.findAllById(dto.getHotelIds());
      if (hotels.size() != dto.getHotelIds().size()) {
        List<Long> foundIds = hotels.stream().map(Hotel::getId).toList();
        List<Long> missingIds = dto.getHotelIds().stream()
            .filter(id -> !foundIds.contains(id))
            .toList();
        throw new NoSuchElementException("Hotels not found with ids: " + missingIds);
      }
      tour.setHotels(new HashSet<>(hotels));
    }
    if (dto.getGuideIds() != null && !dto.getGuideIds().isEmpty()) {
      List<Guide> guides = guideRepository.findAllById(dto.getGuideIds());
      if (guides.size() != dto.getGuideIds().size()) {
        List<Long> foundIds = guides.stream().map(Guide::getId).toList();
        List<Long> missingIds = dto.getGuideIds().stream()
            .filter(id -> !foundIds.contains(id))
            .toList();
        throw new NoSuchElementException("Guides not found with ids: " + missingIds);
      }
      tour.setGuides(new HashSet<>(guides));
    }
  }

  public List<TourResponseDto> getToursByPrice(BigDecimal price) {
    return tourRepository.findByPrice(price).stream()
        .map(tourMapper::toResponseDto)
        .toList();
  }

  public List<TourResponseDto> getToursByMinPrice(BigDecimal minPrice) {
    return tourRepository.findByPriceGreaterThanEqual(minPrice).stream()
        .map(tourMapper::toResponseDto)
        .toList();
  }

  public List<TourResponseDto> getToursByMaxPrice(BigDecimal maxPrice) {
    return tourRepository.findByPriceLessThanEqualWithGraph(maxPrice).stream()
        .map(tourMapper::toResponseDto)
        .toList();
  }

  @Transactional
  public void deleteTour(Long id) {
    if (!tourRepository.existsById(id)) {
      throw new NoSuchElementException("Tour not found with id: " + id);
    }
    if (bookingRepository.existsByTourId(id)) {
      throw new IllegalStateException("Cannot delete tour with existing bookings");
    }
    tourRepository.deleteById(id);
    tourSearchCache.invalidateAll();
  }

  public Page<TourResponseDto> searchToursByHotelNameJpql(String hotelName, Pageable pageable) {
    String sortStr = pageable.getSort().toString();
    TourSearchKey key = new TourSearchKey(hotelName, pageable.getPageNumber(),
        pageable.getPageSize(), sortStr);
    Page<TourResponseDto> cached = tourSearchCache.get(key);
    if (cached != null) {
      return cached;
    }
    Page<Tour> tours = tourRepository.findToursByHotelNameJpql(hotelName, pageable);
    Page<TourResponseDto> result = tours.map(tourMapper::toResponseDto);
    tourSearchCache.put(key, result);
    return result;
  }

  public Page<TourResponseDto> searchToursByHotelNameNative(String hotelName, Pageable pageable) {
    String sortStr = pageable.getSort().toString();
    TourSearchKey key = new TourSearchKey(hotelName, pageable.getPageNumber(),
        pageable.getPageSize(), sortStr);
    Page<TourResponseDto> cached = tourSearchCache.get(key);
    if (cached != null) {
      return cached;
    }
    Page<Tour> tours = tourRepository.findToursByHotelNameNative(hotelName, pageable);
    Page<TourResponseDto> result = tours.map(tourMapper::toResponseDto);
    tourSearchCache.put(key, result);
    return result;
  }
}