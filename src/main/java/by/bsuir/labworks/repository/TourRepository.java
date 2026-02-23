package by.bsuir.labworks.repository;

import by.bsuir.labworks.model.Tour;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * Репозиторий для доступа к данным туров.
 * Предоставляет стандартные CRUD операции и дополнительные методы поиска.
 */

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
  /**
   * Находит все туры по указанной стране.

   * @param country название страны
   * @return список туров
   */

  List<Tour> findByCountry(String country);
}