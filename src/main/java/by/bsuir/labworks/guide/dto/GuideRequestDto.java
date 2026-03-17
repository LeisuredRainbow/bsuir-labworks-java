package by.bsuir.labworks.guide.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class GuideRequestDto {
  @NotBlank(message = "Имя обязательно")
  private String firstName;

  @NotBlank(message = "Фамилия обязательна")
  private String lastName;

  @Pattern(regexp = "^\\+?[0-9\\-\\s]+$", message = "Некорректный формат телефона")
  private String phone;

  @Email(message = "Некорректный формат email")
  private String email;

  @Min(value = 0, message = "Опыт работы не может быть отрицательным")
  private Integer experienceYears;
}