package by.bsuir.labworks.hotel.repository;

import by.bsuir.labworks.hotel.entity.Hotel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
  List<Hotel> findByCity(String city);
}