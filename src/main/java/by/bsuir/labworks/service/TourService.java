package by.bsuir.labworks.service;

import by.bsuir.labworks.dto.TourDto;
import by.bsuir.labworks.mapper.TourMapper;
import by.bsuir.labworks.model.Tour;
import by.bsuir.labworks.repository.TourRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
/**
 * Сервис для работы с турами.
 * Содержит бизнес-логику по обработке туров.
 */

@Service
@RequiredArgsConstructor // Lombok создаст конструктор для всех final полей
public class TourService {

  private final TourRepository tourRepository;
  private final TourMapper tourMapper;

  /**
   * Создаёт новый тур.

   * @param tourDto данные для создания тура
   * @return созданный тур с присвоенным ID
   */
  
  public TourDto createTour(TourDto tourDto) {
    Tour tour = tourMapper.toEntity(tourDto);
    Tour savedTour = tourRepository.save(tour);
    return tourMapper.toDto(savedTour);
  }

  /**
   * Возвращает список всех туров.

   * @return список DTO всех туров
   */
  
  public List<TourDto> getAllTours() {
    return tourRepository.findAll().stream()
    .map(tourMapper::toDto)
    .toList();
  }

  /**
   * Возвращает список туров по указанной стране.

   * @param country название страны
   * @return список DTO туров
   */

  public List<TourDto> getToursByCountry(String country) {
    return tourRepository.findByCountry(country).stream()
    .map(tourMapper::toDto)
    .toList();
  }

  /**
   * Возвращает тур по его идентификатору.

   * @param id идентификатор тура
   * @return DTO тура или null, если тур не найден
   */

  public TourDto getTourById(Long id) {
    Tour tour = tourRepository.findById(id).orElse(null);
    return tourMapper.toDto(tour);
  }

  // Остальные CRUD методы будут добавлены позже
}