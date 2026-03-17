package by.bsuir.labworks.client.controller;

import by.bsuir.labworks.client.dto.ClientRequestDto;
import by.bsuir.labworks.client.dto.ClientResponseDto;
import by.bsuir.labworks.client.service.ClientService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {
  private final ClientService clientService;

  @GetMapping
  public List<ClientResponseDto> getAllClients() {
    return clientService.getAllClients();
  }

  @GetMapping("/{id}")
  public ClientResponseDto getClientById(@PathVariable Long id) {
    return clientService.getClientById(id);
  }

  @GetMapping("/by-email")
  public ClientResponseDto getClientByEmail(@RequestParam String email) {
    return clientService.getClientByEmail(email);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ClientResponseDto createClient(@RequestBody @Valid ClientRequestDto clientDto) {
    return clientService.createClient(clientDto);
  }

  @PutMapping("/{id}")
  public ClientResponseDto updateClient(@PathVariable Long id,
                                        @RequestBody @Valid ClientRequestDto clientDto) {
    return clientService.updateClient(id, clientDto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteClient(@PathVariable Long id) {
    clientService.deleteClient(id);
  }
}