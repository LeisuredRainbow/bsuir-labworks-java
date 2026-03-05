package by.bsuir.labworks.service;

import by.bsuir.labworks.dto.TourDto;
import by.bsuir.labworks.mapper.TourMapper;
import by.bsuir.labworks.model.Tour;
import by.bsuir.labworks.repository.TourRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TourService {

  private final TourRepository tourRepository;
  private final TourMapper tourMapper;

  public TourDto createTour(TourDto tourDto) {
    Tour tour = tourMapper.toEntity(tourDto);
    Tour savedTour = tourRepository.save(tour);
    return tourMapper.toDto(savedTour);
  }

  public List<TourDto> getAllTours() {
    return tourRepository.findAll().stream()
    .map(tourMapper::toDto)
    .toList();
  }

  public List<TourDto> getToursByCountry(String country) {
    return tourRepository.findByCountry(country).stream()
    .map(tourMapper::toDto)
    .toList();
  }

  public Optional<TourDto> getTourById(Long id) {
    return tourRepository.findById(id)
    .map(tourMapper::toDto);
  }
}