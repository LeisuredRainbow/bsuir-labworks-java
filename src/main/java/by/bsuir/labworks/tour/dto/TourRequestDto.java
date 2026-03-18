package by.bsuir.labworks.tour.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class TourRequestDto {
  @NotBlank(message = "Название не может быть пустым")
  private String name;

  @NotBlank(message = "Страна не может быть пустой")
  private String country;

  @Min(value = 1, message = "Длительность должна быть не менее 1 дня")
  private Integer durationDays;

  @NotNull(message = "Цена обязательна")
  @Positive(message = "Цена должна быть положительной")
  private BigDecimal price;

  private Boolean hot;

  @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
  private String description;

  private List<Long> hotelIds;

  private List<Long> guideIds;
}