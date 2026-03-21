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

  @Pattern(regexp = "^\\+375[\\s-]?\\(\\d{2}\\)[\\s-]?\\d{3}[\\s-]?\\d{2}[\\s-]?\\d{2}$",
         message = "Некорректный формат телефона (ожидается +375(xx)xxx-xx-xx)")
  private String phone;
}