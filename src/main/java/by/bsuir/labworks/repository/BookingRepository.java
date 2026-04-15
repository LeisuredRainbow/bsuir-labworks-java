package by.bsuir.labworks.repository;

import by.bsuir.labworks.entity.Booking;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

  @Query("SELECT b FROM Booking b JOIN FETCH b.client JOIN FETCH b.tour")
  List<Booking> findAll();

  @Query("SELECT b FROM Booking b JOIN FETCH b.client JOIN FETCH b.tour WHERE b.id = :id")
  Optional<Booking> findById(@Param("id") Long id);

  @Query("SELECT b FROM Booking b JOIN FETCH b.client JOIN FETCH b.tour "
      + "WHERE b.client.id = :clientId")
  List<Booking> findByClientId(@Param("clientId") Long clientId);

  @Query("SELECT b FROM Booking b JOIN FETCH b.client JOIN FETCH b.tour "
      + "WHERE b.tour.id = :tourId")
  List<Booking> findByTourId(@Param("tourId") Long tourId);

  boolean existsByTourId(Long tourId);
}