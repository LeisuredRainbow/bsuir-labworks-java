package by.bsuir.labworks.tour.service;

import by.bsuir.labworks.guide.entity.Guide;
import by.bsuir.labworks.guide.repository.GuideRepository;
import by.bsuir.labworks.hotel.entity.Hotel;
import by.bsuir.labworks.hotel.repository.HotelRepository;
import by.bsuir.labworks.tour.dto.TourRequestDto;
import by.bsuir.labworks.tour.dto.TourResponseDto;
import by.bsuir.labworks.tour.entity.Tour;
import by.bsuir.labworks.tour.mapper.TourMapper;
import by.bsuir.labworks.tour.repository.TourRepository;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TourService {
  private final TourRepository tourRepository;
  private final TourMapper tourMapper;
  private final HotelRepository hotelRepository;
  private final GuideRepository guideRepository;

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
    return tourMapper.toResponseDto(tour);
  }

  @Transactional
  public TourResponseDto updateTour(Long id, TourRequestDto tourDto) {
    Tour existingTour = tourRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Tour not found with id: " + id));
    tourMapper.updateEntity(tourDto, existingTour);
    setHotelAndGuideRelations(existingTour, tourDto);
    existingTour = tourRepository.save(existingTour);
    return tourMapper.toResponseDto(existingTour);
  }

  private void setHotelAndGuideRelations(Tour tour, TourRequestDto dto) {
    if (dto.getHotelIds() != null) {
      List<Hotel> hotels = hotelRepository.findAllById(dto.getHotelIds());
      if (hotels.size() != dto.getHotelIds().size()) {
        throw new NoSuchElementException("Some hotels not found");
      }
      tour.setHotels(new HashSet<>(hotels));
    }
    if (dto.getGuideIds() != null) {
      List<Guide> guides = guideRepository.findAllById(dto.getGuideIds());
      if (guides.size() != dto.getGuideIds().size()) {
        throw new NoSuchElementException("Some guides not found");
      }
      tour.setGuides(new HashSet<>(guides));
    }
  }

  @Transactional
  public void demonstrateNplusOneProblem() {
    List<Tour> tours = tourRepository.findAll();
    for (Tour tour : tours) {
      Hibernate.initialize(tour.getHotels());
      Hibernate.initialize(tour.getGuides());
    }
  }

  @Transactional
  public void demonstrateSolutionWithEntityGraph() {
    List<Tour> tours = tourRepository.findAllWithHotelsAndGuides();
    for (Tour tour : tours) {
      Hibernate.initialize(tour.getHotels());
      Hibernate.initialize(tour.getGuides());
    }
  }
}