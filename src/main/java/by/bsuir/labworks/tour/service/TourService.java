package by.bsuir.labworks.tour.service;

import by.bsuir.labworks.tour.dto.TourRequestDto;
import by.bsuir.labworks.tour.dto.TourResponseDto;
import by.bsuir.labworks.tour.entity.Tour;
import by.bsuir.labworks.tour.mapper.TourMapper;
import by.bsuir.labworks.tour.repository.TourRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TourService {
  private final TourRepository tourRepository;
  private final TourMapper tourMapper;

  public List<TourResponseDto> getAllTours() {
    return tourRepository.findAll().stream()
      .map(tourMapper::toResponseDto)
      .collect(Collectors.toList());
  }

  public List<TourResponseDto> getToursByCountry(String country) {
    return tourRepository.findByCountry(country).stream()
      .map(tourMapper::toResponseDto)
      .collect(Collectors.toList());
  }

  public TourResponseDto getTourById(Long id) {
    Tour tour = tourRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Tour not found with id: " + id));
    return tourMapper.toResponseDto(tour);
  }

  public TourResponseDto createTour(TourRequestDto tourDto) {
    Tour tour = tourMapper.toEntity(tourDto);
    tour = tourRepository.save(tour);
    return tourMapper.toResponseDto(tour);
  }
}