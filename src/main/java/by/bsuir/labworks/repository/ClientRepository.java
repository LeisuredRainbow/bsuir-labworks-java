package by.bsuir.labworks.repository;

import by.bsuir.labworks.entity.Client;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
  Optional<Client> findByEmail(String email);

  Optional<Client> findByPhone(String phone);
}