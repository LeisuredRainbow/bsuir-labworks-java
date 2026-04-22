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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

  private static final String CLIENT_NOT_FOUND_MSG = "Client not found with id: ";

  private final ClientRepository clientRepository;
  private final ClientMapper clientMapper;
  private final GuideRepository guideRepository;

  public List<ClientResponseDto> getAllClients() {
    return clientRepository.findAll().stream()
        .map(clientMapper::toResponseDto)
        .toList();
  }

  public ClientResponseDto getClientById(Long id) {
    Client client = clientRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException(CLIENT_NOT_FOUND_MSG + id));
    return clientMapper.toResponseDto(client);
  }

  public ClientResponseDto getClientByEmail(String email) {
    Client client = clientRepository.findByEmail(email)
        .orElseThrow(() -> new NoSuchElementException("Client not found with email: " + email));
    return clientMapper.toResponseDto(client);
  }

  public ClientResponseDto createClient(ClientRequestDto clientDto) {
    if (clientDto.getPhone() != null) {
      if (clientRepository.findByPhone(clientDto.getPhone()).isPresent()) {
        throw new IllegalArgumentException("Client with phone "
            + clientDto.getPhone() + " already exists");
      }
      if (guideRepository.findByPhone(clientDto.getPhone()).isPresent()) {
        throw new IllegalArgumentException("Phone "
            + clientDto.getPhone() + " already used by a guide");
      }
    }
    Client client = clientMapper.toEntity(clientDto);
    client = clientRepository.save(client);
    return clientMapper.toResponseDto(client);
  }

  @Transactional
  public ClientResponseDto updateClient(Long id, ClientRequestDto clientDto) {
    Client existingClient = clientRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException(CLIENT_NOT_FOUND_MSG + id));
    if (clientDto.getPhone() != null && !clientDto.getPhone().equals(existingClient.getPhone())) {
      if (clientRepository.findByPhone(clientDto.getPhone()).isPresent()) {
        throw new IllegalArgumentException("Client with phone "
            + clientDto.getPhone() + " already exists");
      }
      if (guideRepository.findByPhone(clientDto.getPhone()).isPresent()) {
        throw new IllegalArgumentException("Phone "
            + clientDto.getPhone() + " already used by a guide");
      }
    }

    existingClient.setFirstName(clientDto.getFirstName());
    existingClient.setLastName(clientDto.getLastName());
    existingClient.setEmail(clientDto.getEmail());
    existingClient.setPhone(clientDto.getPhone());
    existingClient = clientRepository.save(existingClient);
    return clientMapper.toResponseDto(existingClient);
  }

  @Transactional
  public void deleteClient(Long id) {
    Client client = clientRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException(CLIENT_NOT_FOUND_MSG + id));
    clientRepository.delete(client);
  }
}