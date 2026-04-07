package by.bsuir.labworks.repository;

import by.bsuir.labworks.entity.Guide;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuideRepository extends JpaRepository<Guide, Long> {
  Optional<Guide> findByEmail(String email);

  Optional<Guide> findByPhone(String phone);
}