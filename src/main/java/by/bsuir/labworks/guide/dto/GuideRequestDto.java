package by.bsuir.labworks.guide.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GuideRequestDto {
  @NotBlank(message = "Имя обязательно")
  private String firstName;

  @NotBlank(message = "Фамилия обязательна")
  private String lastName;

  @Pattern(regexp = "^\\+\\d{7,15}$",
      message = "Номер телефона должен начинаться с '+' и содержать от 7 до 15 цифр")
  private String phone;

  @NotBlank(message = "Email обязателен")
  @Email(message = "Некорректный формат email")
  @Size(max = 320, message = "Email не может превышать 320 символов")
  private String email;

  @Min(value = 0, message = "Опыт работы не может быть отрицательным")
  private Integer experienceYears;
}