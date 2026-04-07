package by.bsuir.labworks.repository;

import by.bsuir.labworks.entity.Hotel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
  Optional<Hotel> findByAddress(String address);
}