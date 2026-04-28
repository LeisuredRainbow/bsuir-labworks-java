package by.bsuir.labworks.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

  @NotBlank(message = "Name cannot be empty")
  @Schema(example = "In Search of One Punch Man")
  private String name;

  @NotBlank(message = "Country cannot be empty")
  @Schema(example = "Japan")
  private String country;

  @Min(value = 1, message = "Duration must be at least 1 day")
  @Schema(example = "7")
  private Integer durationDays;

  @NotNull(message = "Price is required")
  @Positive(message = "Price must be positive")
  @Schema(example = "1000.00")
  private BigDecimal price;

  @Schema(example = "true")
  private Boolean hot;

  @Size(max = 1000, message = "Description cannot exceed 1000 characters")
  @Schema(example = "Tour to anime culture places: Akihabara, Nagoya Castle, Mount Fuji.")
  private String description;

  @Schema(description = "List of hotel IDs", example = "[2, 4]")
  private List<Long> hotelIds;

  @Schema(description = "List of guide IDs", example = "[3]")
  private List<Long> guideIds;
}