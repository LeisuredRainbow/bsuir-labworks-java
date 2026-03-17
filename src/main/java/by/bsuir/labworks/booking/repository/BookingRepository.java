package by.bsuir.labworks.booking.repository;

import by.bsuir.labworks.booking.entity.Booking;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
  List<Booking> findByClientId(Long clientId);

  List<Booking> findByTourId(Long tourId);
}