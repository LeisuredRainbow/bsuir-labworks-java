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
import java.util.Optional;
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
    Long safeId = Optional.ofNullable(id)
        .orElseThrow(() -> new IllegalArgumentException("ID cannot be null"));
    LOG.debug("Fetching client by id={}", safeId);
    Client client = clientRepository.findById(safeId)
        .orElseThrow(() -> new NoSuchElementException(CLIENT_NOT_FOUND_MSG + safeId));
    return clientMapper.toResponseDto(client);
  }

  public ClientResponseDto getClientByEmail(String email) {
    String safeEmail = Optional.ofNullable(email)
        .orElseThrow(() -> new IllegalArgumentException("Email cannot be null"));
    LOG.debug("Fetching client by email={}", safeEmail);
    Client client = clientRepository.findByEmail(safeEmail)
        .orElseThrow(() -> new NoSuchElementException(
            "Client not found with email: " + safeEmail));
    return clientMapper.toResponseDto(client);
  }

  public ClientResponseDto createClient(ClientRequestDto clientDto) {
    ClientRequestDto safeDto = Optional.ofNullable(clientDto)
        .orElseThrow(() -> new IllegalArgumentException("Client data cannot be null"));
    LOG.info("Creating new client");
    if (safeDto.getPhone() != null) {
      if (clientRepository.findByPhone(safeDto.getPhone()).isPresent()) {
        throw new IllegalArgumentException(
            "Client with phone " + safeDto.getPhone() + " already exists");
      }
      if (guideRepository.findByPhone(safeDto.getPhone()).isPresent()) {
        throw new IllegalArgumentException(
            "Phone " + safeDto.getPhone() + " is already used by a guide");
      }
    }
    Client client = clientMapper.toEntity(safeDto);
    client = clientRepository.save(client);
    LOG.info("Client created with id={}", client.getId());
    return clientMapper.toResponseDto(client);
  }

  @Transactional
  public ClientResponseDto updateClient(Long id, ClientRequestDto clientDto) {
    Long safeId = Optional.ofNullable(id)
        .orElseThrow(() -> new IllegalArgumentException("ID cannot be null"));
    ClientRequestDto safeDto = Optional.ofNullable(clientDto)
        .orElseThrow(() -> new IllegalArgumentException("Client data cannot be null"));
    LOG.info("Updating client id={}", safeId);
    Client existingClient = clientRepository.findById(safeId)
        .orElseThrow(() -> new NoSuchElementException(CLIENT_NOT_FOUND_MSG + safeId));
    if (safeDto.getPhone() != null
        && !safeDto.getPhone().equals(existingClient.getPhone())) {
      if (clientRepository.findByPhone(safeDto.getPhone()).isPresent()) {
        throw new IllegalArgumentException(
            "Client with phone " + safeDto.getPhone() + " already exists");
      }
      if (guideRepository.findByPhone(safeDto.getPhone()).isPresent()) {
        throw new IllegalArgumentException(
            "Phone " + safeDto.getPhone() + " is already used by a guide");
      }
    }

    existingClient.setFirstName(safeDto.getFirstName());
    existingClient.setLastName(safeDto.getLastName());
    existingClient.setEmail(safeDto.getEmail());
    existingClient.setPhone(safeDto.getPhone());
    existingClient = clientRepository.save(existingClient);
    LOG.info("Client updated id={}", existingClient.getId());
    return clientMapper.toResponseDto(existingClient);
  }

  @Transactional
  public void deleteClient(Long id) {
    Long safeId = Optional.ofNullable(id)
        .orElseThrow(() -> new IllegalArgumentException("ID cannot be null"));
    LOG.info("Deleting client id={}", safeId);
    Client client = clientRepository.findById(safeId)
        .orElseThrow(() -> new NoSuchElementException(CLIENT_NOT_FOUND_MSG + safeId));
    clientRepository.delete(client);
    LOG.info("Client deleted id={}", safeId);
  }
}