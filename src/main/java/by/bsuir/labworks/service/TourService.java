package by.bsuir.labworks.service;

import by.bsuir.labworks.dto.TourDto;
import by.bsuir.labworks.mapper.TourMapper;
import by.bsuir.labworks.model.Tour;
import by.bsuir.labworks.repository.TourRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Lombok создаст конструктор для всех final полей
public class TourService {

  private final TourRepository tourRepository;
  private final TourMapper tourMapper;

  public List<TourDto> getAllTours() {
    return tourRepository.findAll().stream()
    .map(tourMapper::toDto)
    .collect(Collectors.toList());
  }

  // GET с @RequestParam будет вызывать этот метод
  public List<TourDto> getToursByCountry(String country) {
    return tourRepository.findByCountry(country).stream()
    .map(tourMapper::toDto)
    .collect(Collectors.toList());
  }

  // GET с @PathVariable будет вызывать этот метод
  public TourDto getTourById(Long id) {
    Tour tour = tourRepository.findById(id).orElse(null);
    return tourMapper.toDto(tour);
  }

  // Остальные CRUD методы будут добавлены позже
}