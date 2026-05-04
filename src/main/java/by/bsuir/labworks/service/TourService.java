package by.bsuir.labworks.service;

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
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TourService {
  private static final Logger LOG = LoggerFactory.getLogger(TourService.class);

  private final TourRepository tourRepository;
  private final TourMapper tourMapper;
  private final HotelRepository hotelRepository;
  private final GuideRepository guideRepository;
  private final BookingRepository bookingRepository;

  public List<TourResponseDto> getAllTours() {
    LOG.debug("Fetching all tours");
    return tourRepository.findAll().stream()
        .map(tourMapper::toResponseDto)
        .toList();
  }

  public List<TourResponseDto> getToursByCountry(String country) {
    String safeCountry = Optional.ofNullable(country)
        .orElseThrow(() -> new IllegalArgumentException("Country cannot be null"));
    LOG.debug("Fetching tours by country: {}", safeCountry);
    return tourRepository.findByCountry(safeCountry).stream()
        .map(tourMapper::toResponseDto)
        .toList();
  }

  public TourResponseDto getTourById(Long id) {
    Long safeId = Optional.ofNullable(id)
        .orElseThrow(() -> new IllegalArgumentException("ID cannot be null"));
    LOG.debug("Fetching tour by id={}", safeId);
    Tour tour = tourRepository.findById(safeId)
        .orElseThrow(() -> new NoSuchElementException("Tour not found with id: " + safeId));
    return tourMapper.toResponseDto(tour);
  }

  public TourResponseDto createTour(TourRequestDto tourDto) {
    TourRequestDto safeDto = Optional.ofNullable(tourDto)
        .orElseThrow(() -> new IllegalArgumentException("Tour data cannot be null"));
    LOG.info("Creating new tour");
    Tour tour = tourMapper.toEntity(safeDto);
    setHotelAndGuideRelations(tour, safeDto);
    tour = tourRepository.save(tour);
    LOG.info("Tour created with id={}", tour.getId());
    return tourMapper.toResponseDto(tour);
  }

  @Transactional
  public TourResponseDto updateTour(Long id, TourRequestDto tourDto) {
    Long safeId = Optional.ofNullable(id)
        .orElseThrow(() -> new IllegalArgumentException("ID cannot be null"));
    TourRequestDto safeDto = Optional.ofNullable(tourDto)
        .orElseThrow(() -> new IllegalArgumentException("Tour data cannot be null"));
    LOG.info("Updating tour id={}", safeId);
    Tour existingTour = tourRepository.findById(safeId)
        .orElseThrow(() -> new NoSuchElementException("Tour not found with id: " + safeId));
    tourMapper.updateEntity(safeDto, existingTour);
    setHotelAndGuideRelations(existingTour, safeDto);
    existingTour = tourRepository.save(existingTour);
    LOG.info("Tour updated id={}", existingTour.getId());
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
      LOG.debug("Set {} hotels for tour", hotels.size());
    } else {
      tour.setHotels(new HashSet<>());
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
      LOG.debug("Set {} guides for tour", guides.size());
    } else {
      tour.setGuides(new HashSet<>());
    }
  }

  public List<TourResponseDto> getToursByPrice(BigDecimal price) {
    BigDecimal safePrice = Optional.ofNullable(price)
        .orElseThrow(() -> new IllegalArgumentException("Price cannot be null"));
    LOG.debug("Fetching tours by exact price: {}", safePrice);
    return tourRepository.findByPrice(safePrice).stream()
        .map(tourMapper::toResponseDto)
        .toList();
  }

  public List<TourResponseDto> getToursByMinPrice(BigDecimal minPrice) {
    BigDecimal safeMinPrice = Optional.ofNullable(minPrice)
        .orElseThrow(() -> new IllegalArgumentException("Minimum price cannot be null"));
    LOG.debug("Fetching tours with price >= {}", safeMinPrice);
    return tourRepository.findByPriceGreaterThanEqual(safeMinPrice).stream()
        .map(tourMapper::toResponseDto)
        .toList();
  }

  public List<TourResponseDto> getToursByMaxPrice(BigDecimal maxPrice) {
    BigDecimal safeMaxPrice = Optional.ofNullable(maxPrice)
        .orElseThrow(() -> new IllegalArgumentException("Maximum price cannot be null"));
    LOG.debug("Fetching tours with price <= {}", safeMaxPrice);
    return tourRepository.findByPriceLessThanEqualWithGraph(safeMaxPrice).stream()
        .map(tourMapper::toResponseDto)
        .toList();
  }

  @Transactional
  public void deleteTour(Long id) {
    Long safeId = Optional.ofNullable(id)
        .orElseThrow(() -> new IllegalArgumentException("ID cannot be null"));
    LOG.info("Deleting tour id={}", safeId);
    if (!tourRepository.existsById(safeId)) {
      throw new NoSuchElementException("Tour not found with id: " + safeId);
    }
    if (bookingRepository.existsByTourId(safeId)) {
      throw new IllegalStateException("Cannot delete tour with existing bookings");
    }
    tourRepository.deleteById(safeId);
    LOG.info("Tour deleted id={}", safeId);
  }
}