package by.bsuir.labworks.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClientRequestDto {

  @NotBlank(message = "First name is required")
  @Schema(example = "Darth")
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Schema(example = "Vader")
  private String lastName;

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  @Size(max = 320, message = "Email cannot exceed 320 characters")
  @Schema(example = "darth.vader@empire.com")
  private String email;

  @Pattern(regexp = "^\\+\\d{7,15}$",
      message = "Phone must start with '+' and contain 7 to 15 digits")
  @Schema(example = "+375296666666")
  private String phone;
}