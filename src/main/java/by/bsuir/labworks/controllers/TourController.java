package by.bsuir.labworks.controllers;

import by.bsuir.labworks.dto.TourRequestDto;
import by.bsuir.labworks.dto.TourResponseDto;
import by.bsuir.labworks.service.TourService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
public class TourController {
  private final TourService tourService;

  @GetMapping
  public List<TourResponseDto> getAllTours() {
    return tourService.getAllTours();
  }

  @GetMapping("/country")
  public List<TourResponseDto> getToursByCountry(@RequestParam String country) {
    return tourService.getToursByCountry(country);
  }

  @GetMapping("/{id}")
  public TourResponseDto getTourById(@PathVariable Long id) {
    return tourService.getTourById(id);
  }

  @PostMapping
  public TourResponseDto createTour(@RequestBody @Valid TourRequestDto tourDto) {
    return tourService.createTour(tourDto);
  }

  @PutMapping("/{id}")
  public TourResponseDto updateTour(@PathVariable Long id,
      @RequestBody @Valid TourRequestDto tourDto) {
    return tourService.updateTour(id, tourDto);
  }

  @GetMapping("/price")
  public List<TourResponseDto> getToursByPrice(@RequestParam BigDecimal price) {
    return tourService.getToursByPrice(price);
  }

  @GetMapping("/price/min")
  public List<TourResponseDto> getToursByMinPrice(@RequestParam BigDecimal minPrice) {
    return tourService.getToursByMinPrice(minPrice);
  }

  @GetMapping("/price/max")
  public List<TourResponseDto> getToursByMaxPrice(@RequestParam BigDecimal maxPrice) {
    return tourService.getToursByMaxPrice(maxPrice);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTour(@PathVariable Long id) {
    tourService.deleteTour(id);
  }
}