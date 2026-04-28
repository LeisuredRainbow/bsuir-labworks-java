package by.bsuir.labworks.service;

import by.bsuir.labworks.dto.ClientRequestDto;
import by.bsuir.labworks.dto.ClientResponseDto;
import by.bsuir.labworks.entity.Client;
import by.bsuir.labworks.mapper.ClientMapper;
import by.bsuir.labworks.repository.ClientRepository;
import by.bsuir.labworks.repository.GuideRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

  private static final Logger LOG = LoggerFactory.getLogger(ClientService.class);
  private static final String CLIENT_NOT_FOUND_MSG = "Client not found with id: ";

  private final ClientRepository clientRepository;
  private final ClientMapper clientMapper;
  private final GuideRepository guideRepository;

  public List<ClientResponseDto> getAllClients() {
    LOG.debug("Fetching all clients");
    return clientRepository.findAll().stream()
        .map(clientMapper::toResponseDto)
        .toList();
  }

  public ClientResponseDto getClientById(Long id) {
    LOG.debug("Fetching client by id={}", id);
    Client client = clientRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException(CLIENT_NOT_FOUND_MSG + id));
    return clientMapper.toResponseDto(client);
  }

  public ClientResponseDto getClientByEmail(String email) {
    LOG.debug("Fetching client by email={}", email);
    Client client = clientRepository.findByEmail(email)
        .orElseThrow(() -> new NoSuchElementException("Client not found with email: " + email));
    return clientMapper.toResponseDto(client);
  }

  public ClientResponseDto createClient(ClientRequestDto clientDto) {
    LOG.info("Creating new client");
    if (clientDto.getPhone() != null) {
      if (clientRepository.findByPhone(clientDto.getPhone()).isPresent()) {
        throw new IllegalArgumentException("Client with phone "
            + clientDto.getPhone() + " already exists");
      }
      if (guideRepository.findByPhone(clientDto.getPhone()).isPresent()) {
        throw new IllegalArgumentException("Phone "
            + clientDto.getPhone() + " is already used by a guide");
      }
    }
    Client client = clientMapper.toEntity(clientDto);
    client = clientRepository.save(client);
    LOG.info("Client created with id={}", client.getId());
    return clientMapper.toResponseDto(client);
  }

  @Transactional
  public ClientResponseDto updateClient(Long id, ClientRequestDto clientDto) {
    LOG.info("Updating client id={}", id);
    Client existingClient = clientRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException(CLIENT_NOT_FOUND_MSG + id));
    if (clientDto.getPhone() != null && !clientDto.getPhone().equals(existingClient.getPhone())) {
      if (clientRepository.findByPhone(clientDto.getPhone()).isPresent()) {
        throw new IllegalArgumentException("Client with phone "
            + clientDto.getPhone() + " already exists");
      }
      if (guideRepository.findByPhone(clientDto.getPhone()).isPresent()) {
        throw new IllegalArgumentException("Phone "
            + clientDto.getPhone() + " is already used by a guide");
      }
    }

    existingClient.setFirstName(clientDto.getFirstName());
    existingClient.setLastName(clientDto.getLastName());
    existingClient.setEmail(clientDto.getEmail());
    existingClient.setPhone(clientDto.getPhone());
    existingClient = clientRepository.save(existingClient);
    LOG.info("Client updated id={}", existingClient.getId());
    return clientMapper.toResponseDto(existingClient);
  }

  @Transactional
  public void deleteClient(Long id) {
    LOG.info("Deleting client id={}", id);
    Client client = clientRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException(CLIENT_NOT_FOUND_MSG + id));
    clientRepository.delete(client);
    LOG.info("Client deleted id={}", id);
  }
}