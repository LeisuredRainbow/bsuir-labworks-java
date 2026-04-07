package by.bsuir.labworks.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HotelRequestDto {
  @NotBlank(message = "Название отеля обязательно")
  private String name;

  private String address;

  @Min(value = 1, message = "Количество звёзд должно быть от 1 до 5")
  @Max(value = 5, message = "Количество звёзд должно быть от 1 до 5")
  private Integer stars;
}