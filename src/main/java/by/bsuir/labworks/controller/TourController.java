package by.bsuir.labworks.controller;

import by.bsuir.labworks.dto.TourDto;
import by.bsuir.labworks.service.TourService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * REST-контроллер для управления турами.
 */

@RestController // @Controller + @ResponseBody
@RequestMapping("/api/tours")
@RequiredArgsConstructor
public class TourController {

  private final TourService tourService;

  /**
   * Возвращает список туров. Если указан параметр country, возвращает туры только из этой страны.

   * @param country название страны (необязательный)
   * @return список туров
   */

  @GetMapping
  public List<TourDto> getTours(@RequestParam(required = false) String country) {
    if (country != null) {
      return tourService.getToursByCountry(country);
    }
    return tourService.getAllTours();
  }

  /**
   * Возвращает тур по его идентификатору.

   * @param id идентификатор тура
   * @return тур или null, если не найден
   */

  @GetMapping("/{id}")
  public TourDto getTourById(@PathVariable Long id) {
    return tourService.getTourById(id);
  }

  /**
   * Создаёт новый тур.

   * @param tourDto данные тура
   * @return созданный тур с присвоенным ID
   */

  @PostMapping
  public TourDto createTour(@RequestBody TourDto tourDto) {
    return tourService.createTour(tourDto);
  }
}