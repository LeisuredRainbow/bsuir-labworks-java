package by.bsuir.labworks.controllers;

import by.bsuir.labworks.dto.ClientRequestDto;
import by.bsuir.labworks.dto.ClientResponseDto;
import by.bsuir.labworks.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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
@Validated
@Tag(name = "Clients", description = "Operations with clients")
public class ClientController {
  private final ClientService clientService;

  @GetMapping
  @Operation(summary = "Get all clients")
  public List<ClientResponseDto> getAllClients() {
    return clientService.getAllClients();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get client by id")
  public ClientResponseDto getClientById(
      @Parameter(description = "Client id", example = "1")
      @PathVariable @Positive(message = "ID must be positive") Long id) {
    return clientService.getClientById(id);
  }

  @GetMapping("/by-email")
  @Operation(summary = "Get client by email")
  public ClientResponseDto getClientByEmail(
      @Parameter(description = "Email", example = "john@example.com")
      @RequestParam @NotBlank(message = "Email must not be blank")
          @Email(message = "Invalid email format") String email) {
    return clientService.getClientByEmail(email);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create a new client")
  public ClientResponseDto createClient(
      @RequestBody @Valid @Parameter(description = "Client data") ClientRequestDto clientDto) {
    return clientService.createClient(clientDto);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update existing client")
  public ClientResponseDto updateClient(
      @Parameter(description = "Client id", example = "1")
      @PathVariable @Positive(message = "ID must be positive") Long id,
      @RequestBody @Valid ClientRequestDto clientDto) {
    return clientService.updateClient(id, clientDto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete client")
  public void deleteClient(
      @Parameter(description = "Client id", example = "1")
      @PathVariable @Positive(message = "ID must be positive") Long id) {
    clientService.deleteClient(id);
  }
}