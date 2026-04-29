package by.bsuir.labworks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.bsuir.labworks.dto.ClientRequestDto;
import by.bsuir.labworks.dto.ClientResponseDto;
import by.bsuir.labworks.entity.Client;
import by.bsuir.labworks.mapper.ClientMapper;
import by.bsuir.labworks.repository.ClientRepository;
import by.bsuir.labworks.repository.GuideRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

  @Mock
  private ClientRepository clientRepository;

  @Mock
  private ClientMapper clientMapper;

  @Mock
  private GuideRepository guideRepository;

  private ClientService clientService;

  @BeforeEach
  void setUp() {
    clientService = new ClientService(clientRepository, clientMapper, guideRepository);
  }

  @Test
  void getAllClientsMapsEntities() {
    Client client = new Client();
    ClientResponseDto response = new ClientResponseDto();
    when(clientRepository.findAll()).thenReturn(List.of(client));
    when(clientMapper.toResponseDto(client)).thenReturn(response);

    List<ClientResponseDto> result = clientService.getAllClients();

    assertThat(result).containsExactly(response);
  }

  @Test
  void getClientByIdThrowsWhenMissing() {
    when(clientRepository.findById(1L)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> clientService.getClientById(1L))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Client not found");
  }

  @Test
  void getClientByIdMapsEntity() {
    Client client = new Client();
    ClientResponseDto response = new ClientResponseDto();
    when(clientRepository.findById(1L)).thenReturn(java.util.Optional.of(client));
    when(clientMapper.toResponseDto(client)).thenReturn(response);

    ClientResponseDto result = clientService.getClientById(1L);

    assertThat(result).isSameAs(response);
  }

  @Test
  void getClientByEmailThrowsWhenMissing() {
    when(clientRepository.findByEmail("a@b.com")).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> clientService.getClientByEmail("a@b.com"))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Client not found with email");
  }

  @Test
  void getClientByEmailMapsEntity() {
    Client client = new Client();
    ClientResponseDto response = new ClientResponseDto();
    when(clientRepository.findByEmail("a@b.com")).thenReturn(java.util.Optional.of(client));
    when(clientMapper.toResponseDto(client)).thenReturn(response);

    ClientResponseDto result = clientService.getClientByEmail("a@b.com");

    assertThat(result).isSameAs(response);
  }

  @Test
  void createClientRejectsPhoneUsedByClient() {
    ClientRequestDto dto = new ClientRequestDto();
    dto.setPhone("+375291234567");

    when(clientRepository.findByPhone("+375291234567"))
        .thenReturn(java.util.Optional.of(new Client()));

    assertThatThrownBy(() -> clientService.createClient(dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("already exists");
  }

  @Test
  void createClientRejectsPhoneUsedByGuide() {
    ClientRequestDto dto = new ClientRequestDto();
    dto.setPhone("+375291234567");

    when(clientRepository.findByPhone("+375291234567"))
        .thenReturn(java.util.Optional.empty());
    when(guideRepository.findByPhone("+375291234567"))
        .thenReturn(java.util.Optional.of(new by.bsuir.labworks.entity.Guide()));

    assertThatThrownBy(() -> clientService.createClient(dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("already used by a guide");
  }

  @Test
  void createClientSavesAndMaps() {
    ClientRequestDto dto = new ClientRequestDto();
    dto.setPhone("+375291234567");
    Client entity = new Client();
    Client saved = new Client();
    saved.setId(3L);
    ClientResponseDto response = new ClientResponseDto();

    when(clientRepository.findByPhone("+375291234567")).thenReturn(java.util.Optional.empty());
    when(guideRepository.findByPhone("+375291234567")).thenReturn(java.util.Optional.empty());
    when(clientMapper.toEntity(dto)).thenReturn(entity);
    when(clientRepository.save(entity)).thenReturn(saved);
    when(clientMapper.toResponseDto(saved)).thenReturn(response);

    ClientResponseDto result = clientService.createClient(dto);

    assertThat(result).isSameAs(response);
  }

  @Test
  void updateClientRejectsMissing() {
    when(clientRepository.findById(4L)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> clientService.updateClient(4L, new ClientRequestDto()))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Client not found");
  }

  @Test
  void updateClientRejectsPhoneUsedByClient() {
    Client existing = new Client();
    existing.setPhone("+111111111");
    when(clientRepository.findById(5L)).thenReturn(java.util.Optional.of(existing));

    ClientRequestDto dto = new ClientRequestDto();
    dto.setPhone("+222222222");

    when(clientRepository.findByPhone("+222222222"))
        .thenReturn(java.util.Optional.of(new Client()));

    assertThatThrownBy(() -> clientService.updateClient(5L, dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("already exists");
  }

  @Test
  void updateClientRejectsPhoneUsedByGuide() {
    Client existing = new Client();
    existing.setPhone("+111111111");
    when(clientRepository.findById(5L)).thenReturn(java.util.Optional.of(existing));

    ClientRequestDto dto = new ClientRequestDto();
    dto.setPhone("+222222222");

    when(clientRepository.findByPhone("+222222222"))
        .thenReturn(java.util.Optional.empty());
    when(guideRepository.findByPhone("+222222222"))
        .thenReturn(java.util.Optional.of(new by.bsuir.labworks.entity.Guide()));

    assertThatThrownBy(() -> clientService.updateClient(5L, dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("already used by a guide");
  }

  @Test
  void updateClientAllowsSamePhone() {
    Client existing = new Client();
    existing.setPhone("+111111111");
    when(clientRepository.findById(5L)).thenReturn(java.util.Optional.of(existing));

    ClientRequestDto dto = new ClientRequestDto();
    dto.setPhone("+111111111");

    Client saved = new Client();
    when(clientRepository.save(existing)).thenReturn(saved);
    when(clientMapper.toResponseDto(saved)).thenReturn(new ClientResponseDto());

    clientService.updateClient(5L, dto);

    verify(clientRepository, never()).findByPhone(any());
    verify(guideRepository, never()).findByPhone(any());
  }

  @Test
  void deleteClientRejectsMissing() {
    when(clientRepository.findById(8L)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> clientService.deleteClient(8L))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Client not found");
  }

  @Test
  void deleteClientRemovesEntity() {
    Client client = new Client();
    when(clientRepository.findById(8L)).thenReturn(java.util.Optional.of(client));

    clientService.deleteClient(8L);

    verify(clientRepository).delete(client);
  }
  @Test
  void createClientSavesAndMapsWhenPhoneNull() {
    ClientRequestDto dto = new ClientRequestDto();
    Client entity = new Client();
    Client saved = new Client();
    ClientResponseDto response = new ClientResponseDto();

    when(clientMapper.toEntity(dto)).thenReturn(entity);
    when(clientRepository.save(entity)).thenReturn(saved);
    when(clientMapper.toResponseDto(saved)).thenReturn(response);

    ClientResponseDto result = clientService.createClient(dto);

    assertThat(result).isSameAs(response);
    verify(clientRepository, never()).findByPhone(any());
    verify(guideRepository, never()).findByPhone(any());
  }

  @Test
  void updateClientUpdatesWhenNewPhoneAvailable() {
    Client existing = new Client();
    existing.setPhone("+111111111");
    when(clientRepository.findById(5L)).thenReturn(java.util.Optional.of(existing));

    ClientRequestDto dto = new ClientRequestDto();
    dto.setPhone("+333333333");

    when(clientRepository.findByPhone("+333333333")).thenReturn(java.util.Optional.empty());
    when(guideRepository.findByPhone("+333333333")).thenReturn(java.util.Optional.empty());

    Client saved = new Client();
    when(clientRepository.save(existing)).thenReturn(saved);
    when(clientMapper.toResponseDto(saved)).thenReturn(new ClientResponseDto());

    clientService.updateClient(5L, dto);

    verify(clientRepository).findByPhone("+333333333");
    verify(guideRepository).findByPhone("+333333333");
  }

  @Test
  void updateClientSkipsPhoneChecksWhenPhoneNull() {
    Client existing = new Client();
    existing.setPhone("+111111111");
    when(clientRepository.findById(5L)).thenReturn(java.util.Optional.of(existing));

    ClientRequestDto dto = new ClientRequestDto();
    dto.setPhone(null);

    Client saved = new Client();
    when(clientRepository.save(existing)).thenReturn(saved);
    when(clientMapper.toResponseDto(saved)).thenReturn(new ClientResponseDto());

    clientService.updateClient(5L, dto);

    verify(clientRepository, never()).findByPhone(any());
    verify(guideRepository, never()).findByPhone(any());
  }

}
