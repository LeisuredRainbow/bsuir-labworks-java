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

  @Pattern(regexp = "^\\+375[\\s-]?\\(\\d{2}\\)[\\s-]?\\d{3}[\\s-]?\\d{2}[\\s-]?\\d{2}$",
         message = "Некорректный формат телефона (ожидается +375(xx)xxx-xx-xx)")
  private String phone;

  @Email(message = "Некорректный формат email")
  private String email;

  @Min(value = 0, message = "Опыт работы не может быть отрицательным")
  private Integer experienceYears;
}