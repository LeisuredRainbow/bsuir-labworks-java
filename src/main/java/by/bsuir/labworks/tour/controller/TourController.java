package by.bsuir.labworks.controller;

import by.bsuir.labworks.dto.TourDto;
import by.bsuir.labworks.service.TourService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor
public class TourController {

  private final TourService tourService;
  
  @GetMapping
  public List<TourDto> getTours(@RequestParam(required = false) String country) {
    if (country != null) {
      return tourService.getToursByCountry(country);
    }
    return tourService.getAllTours();
  }

  @GetMapping("/{id}")
  public TourDto getTourById(@PathVariable Long id) {
    return tourService.getTourById(id)
    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
      "Тур с id " + id + " не найден"));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TourDto createTour(@Valid @RequestBody TourDto tourDto) {
    if (tourDto.getId() != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
      "Id не должен указываться при создании тура");
    }
    return tourService.createTour(tourDto);
  }
}