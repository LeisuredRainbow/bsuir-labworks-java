package by.bsuir.labworks.client.repository;

import by.bsuir.labworks.client.entity.Client;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
  Optional<Client> findByEmail(String email);
}