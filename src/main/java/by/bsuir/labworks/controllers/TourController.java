package by.bsuir.labworks.controllers;

import by.bsuir.labworks.dto.TourRequestDto;
import by.bsuir.labworks.dto.TourResponseDto;
import by.bsuir.labworks.service.TourService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
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
@RequestMapping("/api/tours")
@RequiredArgsConstructor
@Validated
@Tag(name = "Tours", description = "Operations with tours")
public class TourController {
  private final TourService tourService;

  @GetMapping
  @Operation(summary = "Get all tours")
  public List<TourResponseDto> getAllTours() {
    return tourService.getAllTours();
  }

  @GetMapping("/country")
  @Operation(summary = "Get tours by country")
  public List<TourResponseDto> getToursByCountry(
      @Parameter(description = "Country name", example = "Italy")
      @RequestParam @NotBlank(message = "Country must not be blank") String country) {
    return tourService.getToursByCountry(country);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get tour by id")
  public TourResponseDto getTourById(
      @Parameter(description = "Tour id", example = "1")
      @PathVariable @Positive(message = "ID must be positive") Long id) {
    return tourService.getTourById(id);
  }

  @PostMapping
  @Operation(summary = "Create a new tour")
  public TourResponseDto createTour(
      @RequestBody @Valid @Parameter(description = "Tour data") TourRequestDto tourDto) {
    return tourService.createTour(tourDto);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update existing tour")
  public TourResponseDto updateTour(
      @Parameter(description = "Tour id", example = "1")
      @PathVariable @Positive(message = "ID must be positive") Long id,
      @RequestBody @Valid TourRequestDto tourDto) {
    return tourService.updateTour(id, tourDto);
  }

  @GetMapping("/price")
  @Operation(summary = "Get tours by exact price")
  public List<TourResponseDto> getToursByPrice(
      @Parameter(description = "Price", example = "1000.00")
      @RequestParam @Positive(message = "Price must be positive") BigDecimal price) {
    return tourService.getToursByPrice(price);
  }

  @GetMapping("/price/min")
  @Operation(summary = "Get tours with price >= minPrice")
  public List<TourResponseDto> getToursByMinPrice(
      @Parameter(description = "Minimum price", example = "500.00")
      @RequestParam @Positive(message = "Minimum price must be positive") BigDecimal minPrice) {
    return tourService.getToursByMinPrice(minPrice);
  }

  @GetMapping("/price/max")
  @Operation(summary = "Get tours with price <= maxPrice")
  public List<TourResponseDto> getToursByMaxPrice(
      @Parameter(description = "Maximum price", example = "2000.00")
      @RequestParam @Positive(message = "Maximum price must be positive") BigDecimal maxPrice) {
    return tourService.getToursByMaxPrice(maxPrice);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete tour")
  public void deleteTour(
      @Parameter(description = "Tour id", example = "1")
      @PathVariable @Positive(message = "ID must be positive") Long id) {
    tourService.deleteTour(id);
  }
}