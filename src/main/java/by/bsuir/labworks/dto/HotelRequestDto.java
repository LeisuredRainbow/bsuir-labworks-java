package by.bsuir.labworks.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HotelRequestDto {

  @NotBlank(message = "Hotel name is required")
  @Schema(example = "Galactic Hotel")
  private String name;

  @Schema(example = "Star Way, 1")
  private String address;

  @Min(value = 1, message = "Stars must be between 1 and 5")
  @Max(value = 5, message = "Stars must be between 1 and 5")
  @Schema(example = "5")
  private Integer stars;
}