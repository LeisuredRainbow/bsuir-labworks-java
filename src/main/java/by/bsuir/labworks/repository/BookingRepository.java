package by.bsuir.labworks.repository;

import by.bsuir.labworks.entity.Booking;
import by.bsuir.labworks.projection.BookingNativeProjection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  @Query(value = "SELECT DISTINCT b FROM Booking b "
      + "JOIN FETCH b.client c "
      + "JOIN FETCH b.tour t "
      + "WHERE LOWER(c.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))",
      countQuery = "SELECT COUNT(b) FROM Booking b JOIN b.client c "
          + "WHERE LOWER(c.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
  Page<Booking> findBookingsByClientLastNameJpql(@Param("lastName") String lastName,
                                                 Pageable pageable);

  @Query(value = "SELECT b.id as id, "
      + "b.booking_date as bookingDate, "
      + "b.client_id as clientId, "
      + "b.tour_id as tourId, "
      + "b.status as status "
      + "FROM bookings b "
      + "JOIN clients c ON b.client_id = c.id "
      + "WHERE LOWER(c.last_name) LIKE LOWER(CONCAT('%', :lastName, '%'))",
      countQuery = "SELECT COUNT(*) FROM bookings b "
          + "JOIN clients c ON b.client_id = c.id "
          + "WHERE LOWER(c.last_name) LIKE LOWER(CONCAT('%', :lastName, '%'))",
      nativeQuery = true)
  Page<BookingNativeProjection> findBookingsByClientLastNameNative(
      @Param("lastName") String lastName, Pageable pageable);
}