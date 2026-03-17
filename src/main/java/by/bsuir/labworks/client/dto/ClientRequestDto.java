package by.bsuir.labworks.client.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ClientRequestDto {
  @NotBlank(message = "Имя обязательно")
  private String firstName;

  @NotBlank(message = "Фамилия обязательна")
  private String lastName;

  @NotBlank(message = "Email обязателен")
  @Email(message = "Некорректный формат email")
  private String email;

  @Pattern(regexp = "^\\+?[0-9\\-\\s]+$", message = "Некорректный формат телефона")
  private String phone;
}