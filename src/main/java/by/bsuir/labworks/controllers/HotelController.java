package by.bsuir.labworks.controllers;

import by.bsuir.labworks.dto.HotelRequestDto;
import by.bsuir.labworks.dto.HotelResponseDto;
import by.bsuir.labworks.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
@Validated
@Tag(name = "Hotels", description = "Operations with hotels")
public class HotelController {
  private final HotelService hotelService;

  @GetMapping
  @Operation(summary = "Get all hotels")
  public List<HotelResponseDto> getAllHotels() {
    return hotelService.getAllHotels();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get hotel by id")
  public HotelResponseDto getHotelById(
      @Parameter(description = "Hotel id", example = "1")
      @PathVariable @Positive(message = "ID must be positive") Long id) {
    return hotelService.getHotelById(id);
  }

  @GetMapping("/by-address")
  @Operation(summary = "Get hotel by address")
  public HotelResponseDto getHotelByAddress(
      @Parameter(description = "Address", example = "Minsk, Nezavisimosti 1")
      @RequestParam @NotBlank(message = "Address must not be blank") String address) {
    return hotelService.getHotelByAddress(address);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create a new hotel")
  public HotelResponseDto createHotel(
      @RequestBody @Valid @Parameter(description = "Hotel data") HotelRequestDto hotelDto) {
    return hotelService.createHotel(hotelDto);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update existing hotel")
  public HotelResponseDto updateHotel(
      @Parameter(description = "Hotel id", example = "1")
      @PathVariable @Positive(message = "ID must be positive") Long id,
      @RequestBody @Valid HotelRequestDto hotelDto) {
    return hotelService.updateHotel(id, hotelDto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete hotel")
  public void deleteHotel(
      @Parameter(description = "Hotel id", example = "1")
      @PathVariable @Positive(message = "ID must be positive") Long id) {
    hotelService.deleteHotel(id);
  }
}