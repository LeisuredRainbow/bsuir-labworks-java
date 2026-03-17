package by.bsuir.labworks.tour.controller;

import by.bsuir.labworks.tour.dto.TourRequestDto;
import by.bsuir.labworks.tour.dto.TourResponseDto;
import by.bsuir.labworks.tour.service.TourService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  @GetMapping(params = "country")
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
}