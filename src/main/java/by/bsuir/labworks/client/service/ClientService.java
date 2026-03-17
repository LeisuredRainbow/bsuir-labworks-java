package by.bsuir.labworks.client.service;

import by.bsuir.labworks.client.dto.ClientRequestDto;
import by.bsuir.labworks.client.dto.ClientResponseDto;
import by.bsuir.labworks.client.entity.Client;
import by.bsuir.labworks.client.mapper.ClientMapper;
import by.bsuir.labworks.client.repository.ClientRepository;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {
  private final ClientRepository clientRepository;
  private final ClientMapper clientMapper;

  public List<ClientResponseDto> getAllClients() {
    return clientRepository.findAll().stream()
        .map(clientMapper::toResponseDto)
        .toList();
  }

  public ClientResponseDto getClientById(Long id) {
    Client client = clientRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Client not found with id: " + id));
    return clientMapper.toResponseDto(client);
  }

  public ClientResponseDto getClientByEmail(String email) {
    Client client = clientRepository.findByEmail(email)
        .orElseThrow(() -> new NoSuchElementException("Client not found with email: " + email));
    return clientMapper.toResponseDto(client);
  }

  public ClientResponseDto createClient(ClientRequestDto clientDto) {
    Client client = clientMapper.toEntity(clientDto);
    client = clientRepository.save(client);
    return clientMapper.toResponseDto(client);
  }

  public ClientResponseDto updateClient(Long id, ClientRequestDto clientDto) {
    Client existingClient = clientRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Client not found with id: " + id));
    existingClient.setFirstName(clientDto.getFirstName());
    existingClient.setLastName(clientDto.getLastName());
    existingClient.setEmail(clientDto.getEmail());
    existingClient.setPhone(clientDto.getPhone());
    existingClient = clientRepository.save(existingClient);
    return clientMapper.toResponseDto(existingClient);
  }

  public void deleteClient(Long id) {
    if (!clientRepository.existsById(id)) {
      throw new NoSuchElementException("Client not found with id: " + id);
    }
    clientRepository.deleteById(id);
  }
}