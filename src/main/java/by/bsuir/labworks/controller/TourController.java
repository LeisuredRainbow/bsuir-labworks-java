package by.bsuir.labworks.controller;

import by.bsuir.labworks.dto.TourDto;
import by.bsuir.labworks.service.TourService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController // @Controller + @ResponseBody
@RequestMapping("/api/tours")
@RequiredArgsConstructor
public class TourController {

  private final TourService tourService;

  // GET endpoint с @RequestParam: /api/tours?country=Италия
  @GetMapping
  public List<TourDto> getTours(@RequestParam(required = false) String country) {
    if (country != null) {
      return tourService.getToursByCountry(country);
    }
    return tourService.getAllTours();
  }

  // GET endpoint с @PathVariable: /api/tours/1
  @GetMapping("/{id}")
  public TourDto getTourById(@PathVariable Long id) {
    return tourService.getTourById(id);
  }
}