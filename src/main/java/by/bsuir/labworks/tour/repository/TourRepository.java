package by.bsuir.labworks.tour.repository;

import by.bsuir.labworks.tour.entity.Tour;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
  List<Tour> findByCountry(String country);

  @EntityGraph(attributePaths = {"hotels", "guides"})
  List<Tour> findByPrice(BigDecimal price);

  List<Tour> findByPriceGreaterThanEqual(BigDecimal minPrice);

  @Query("SELECT t FROM Tour t WHERE t.price <= :maxPrice")
  @EntityGraph(attributePaths = {"hotels", "guides"})
  List<Tour> findByPriceLessThanEqualWithGraph(@Param("maxPrice") BigDecimal maxPrice);

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM tour_hotels WHERE hotel_id = :hotelId", nativeQuery = true)
  void removeHotelFromAllTours(@Param("hotelId") Long hotelId);

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM tour_guides WHERE guide_id = :guideId", nativeQuery = true)
  void removeGuideFromAllTours(@Param("guideId") Long guideId);
}