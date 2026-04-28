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
    LOG.debug("Fetching tours by country: {}", country);
    return tourRepository.findByCountry(country).stream()
        .map(tourMapper::toResponseDto)
        .toList();
  }

  public TourResponseDto getTourById(Long id) {
    LOG.debug("Fetching tour by id={}", id);
    Tour tour = tourRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Tour not found with id: " + id));
    return tourMapper.toResponseDto(tour);
  }

  public TourResponseDto createTour(TourRequestDto tourDto) {
    LOG.info("Creating new tour");
    Tour tour = tourMapper.toEntity(tourDto);
    setHotelAndGuideRelations(tour, tourDto);
    tour = tourRepository.save(tour);
    LOG.info("Tour created with id={}", tour.getId());
    return tourMapper.toResponseDto(tour);
  }

  @Transactional
  public TourResponseDto updateTour(Long id, TourRequestDto tourDto) {
    LOG.info("Updating tour id={}", id);
    Tour existingTour = tourRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Tour not found with id: " + id));
    tourMapper.updateEntity(tourDto, existingTour);
    setHotelAndGuideRelations(existingTour, tourDto);
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
    LOG.debug("Fetching tours by exact price: {}", price);
    return tourRepository.findByPrice(price).stream()
        .map(tourMapper::toResponseDto)
        .toList();
  }

  public List<TourResponseDto> getToursByMinPrice(BigDecimal minPrice) {
    LOG.debug("Fetching tours with price >= {}", minPrice);
    return tourRepository.findByPriceGreaterThanEqual(minPrice).stream()
        .map(tourMapper::toResponseDto)
        .toList();
  }

  public List<TourResponseDto> getToursByMaxPrice(BigDecimal maxPrice) {
    LOG.debug("Fetching tours with price <= {}", maxPrice);
    return tourRepository.findByPriceLessThanEqualWithGraph(maxPrice).stream()
        .map(tourMapper::toResponseDto)
        .toList();
  }

  @Transactional
  public void deleteTour(Long id) {
    LOG.info("Deleting tour id={}", id);
    if (!tourRepository.existsById(id)) {
      throw new NoSuchElementException("Tour not found with id: " + id);
    }
    if (bookingRepository.existsByTourId(id)) {
      throw new IllegalStateException("Cannot delete tour with existing bookings");
    }
    tourRepository.deleteById(id);
    LOG.info("Tour deleted id={}", id);
  }
}