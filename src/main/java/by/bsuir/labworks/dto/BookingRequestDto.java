package by.bsuir.labworks.dto;

import by.bsuir.labworks.entity.Booking.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;

@Data
public class BookingRequestDto {

  @Schema(description = "Existing client ID", example = "6")
  private Long clientId;

  @Schema(description = "First name of new client (if clientId not provided)", example = "Alex")
  private String firstName;

  @Schema(description = "Last name of new client", example = "Petrov")
  private String lastName;

  @Email(message = "Invalid email format")
  @Size(max = 320, message = "Email cannot exceed 320 characters")
  @Schema(description = "Email of new client", example = "alex@example.com")
  private String email;

  @Pattern(regexp = "^\\+\\d{7,15}$",
      message = "Phone must start with '+' and contain 7 to 15 digits")
  @Schema(description = "Client phone (optional)", example = "+375291234567")
  private String phone;

  @NotNull(message = "Tour ID is required")
  @Schema(description = "Tour ID", example = "4")
  private Long tourId;

  @NotNull(message = "Booking date is required")
  @Future(message = "Booking date must be in the future")
  @Schema(description = "Booking date", example = "2026-12-25")
  private LocalDate bookingDate;

  @NotNull(message = "Status is required")
  @Schema(description = "Booking status", example = "CONFIRMED",
      allowableValues = {"CONFIRMED", "PENDING", "CANCELLED"})
  private BookingStatus status;

  public boolean isNewClient() {
    return clientId == null && firstName != null && lastName != null && email != null;
  }

  public boolean isValid() {
    return (clientId != null) || isNewClient();
  }
}