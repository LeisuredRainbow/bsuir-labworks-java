package by.bsuir.labworks.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;
/**
 * DTO для передачи данных тура.
 * Содержит все поля, которые возвращаются клиенту или принимаются от клиента.
 */

@Data
public class TourDto {
  private Long id; // id не должен передаваться при создании, поэтому без валидации

  @NotBlank(message = "Название тура не может быть пустым")
  @Size(max = 100, message = "Название тура не может быть длиннее 100 символов")
  private String name;

  @Size(max = 500, message = "Описание не может быть длиннее 500 символов")
  private String description;

  @NotBlank(message = "Страна не может быть пустой")
  private String country;

  @Positive(message = "Продолжительность должна быть положительным числом")
  private Integer durationDays;

  @NotNull(message = "Цена должна быть указана")
  @DecimalMin(value = "0.0", inclusive = false,
      message = "Цена должна быть больше 0")
  @Digits(integer = 10, fraction = 2,
      message = "Цена должна иметь не более 10 целых и 2 дробных знаков")
  private BigDecimal price;

  private boolean isHot; // boolean имеет значение по умолчанию false, можно не валидировать
}