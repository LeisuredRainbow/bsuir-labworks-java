package by.bsuir.labworks.tour.repository;

import by.bsuir.labworks.tour.entity.Tour;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
  List<Tour> findByCountry(String country);

  @EntityGraph(attributePaths = {"hotels", "bookings", "guides"})
  List<Tour> findByPrice(BigDecimal price);

  @Query("SELECT t FROM Tour t")
  @EntityGraph(attributePaths = {"hotels", "guides"})
  List<Tour> findAllWithHotelsAndGuides();
}