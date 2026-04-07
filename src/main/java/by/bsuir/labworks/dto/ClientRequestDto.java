package by.bsuir.labworks.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClientRequestDto {
  @NotBlank(message = "Имя обязательно")
  private String firstName;

  @NotBlank(message = "Фамилия обязательна")
  private String lastName;

  @NotBlank(message = "Email обязателен")
  @Email(message = "Некорректный формат email")
  @Size(max = 320, message = "Email не может превышать 320 символов")
  private String email;

  @Pattern(regexp = "^\\+\\d{7,15}$",
      message = "Номер телефона должен начинаться с '+' и содержать от 7 до 15 цифр")
  private String phone;
}