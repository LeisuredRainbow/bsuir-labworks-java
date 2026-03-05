package by.bsuir.labworks.repository;

import by.bsuir.labworks.model.Tour;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
  List<Tour> findByCountry(String country);
}