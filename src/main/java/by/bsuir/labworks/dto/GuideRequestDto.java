package by.bsuir.labworks.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GuideRequestDto {

  @NotBlank(message = "First name is required")
  @Schema(example = "Vladimir")
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Schema(example = "Korotkevich")
  private String lastName;

  @Pattern(regexp = "^\\+\\d{7,15}$",
      message = "Phone must start with '+' and contain 7 to 15 digits")
  @Schema(example = "+375449876543")
  private String phone;

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  @Size(max = 320, message = "Email cannot exceed 320 characters")
  @Schema(example = "vlad@literature.by")
  private String email;

  @Min(value = 0, message = "Experience years cannot be negative")
  @Schema(example = "20")
  private Integer experienceYears;
}