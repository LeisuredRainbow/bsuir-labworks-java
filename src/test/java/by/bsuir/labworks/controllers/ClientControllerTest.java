package by.bsuir.labworks.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import by.bsuir.labworks.dto.ClientRequestDto;
import by.bsuir.labworks.dto.ClientResponseDto;
import by.bsuir.labworks.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

  private MockMvc mockMvc;

  @Mock
  private ClientService clientService;

  @InjectMocks
  private ClientController controller;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  void getAllClientsShouldReturnList() throws Exception {
    when(clientService.getAllClients()).thenReturn(List.of(new ClientResponseDto()));
    mockMvc.perform(get("/api/clients"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  void getClientByIdShouldReturnClient() throws Exception {
    ClientResponseDto dto = new ClientResponseDto();
    dto.setId(1L);
    when(clientService.getClientById(1L)).thenReturn(dto);
    mockMvc.perform(get("/api/clients/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  void getClientByEmailShouldReturnClient() throws Exception {
    ClientResponseDto dto = new ClientResponseDto();
    dto.setEmail("a@b.com");
    when(clientService.getClientByEmail("a@b.com")).thenReturn(dto);
    mockMvc.perform(get("/api/clients/by-email?email=a@b.com"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("a@b.com"));
  }

  @Test
  void createClientShouldReturnCreated() throws Exception {
    ClientRequestDto request = new ClientRequestDto();
    request.setFirstName("John");
    request.setLastName("Doe");
    request.setEmail("john@example.com");
    ClientResponseDto response = new ClientResponseDto();
    response.setId(5L);
    when(clientService.createClient(any())).thenReturn(response);

    mockMvc.perform(post("/api/clients")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(5));
  }

  @Test
  void updateClientShouldReturnUpdated() throws Exception {
    ClientRequestDto request = new ClientRequestDto();
    request.setFirstName("Jane");
    request.setLastName("Doe");
    request.setEmail("jane@example.com");
    ClientResponseDto response = new ClientResponseDto();
    response.setId(2L);
    when(clientService.updateClient(any(), any())).thenReturn(response);

    mockMvc.perform(put("/api/clients/2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(2));
  }

  @Test
  void deleteClientShouldReturnNoContent() throws Exception {
    mockMvc.perform(delete("/api/clients/3"))
        .andExpect(status().isNoContent());
  }
}