package by.bsuir.labworks.mapper;

import by.bsuir.labworks.dto.TourDto;
import by.bsuir.labworks.model.Tour;
import org.springframework.stereotype.Component;
/**
 * Маппер для преобразования между сущностью Tour и DTO TourDto.
 */

@Component // Чтобы Spring мог внедрить этот компонент в сервис
public class TourMapper {
  
  /**
   * Преобразует сущность Tour в DTO TourDto.

   * @param tour сущность
   * @return DTO
   */

  public TourDto toDto(Tour tour) {
    if (tour == null) {
      return null;
    }
    TourDto dto = new TourDto();
    dto.setId(tour.getId());
    dto.setName(tour.getName());
    dto.setDescription(tour.getDescription());
    dto.setCountry(tour.getCountry());
    dto.setDurationDays(tour.getDurationDays());
    dto.setPrice(tour.getPrice());
    dto.setHot(tour.isHot());
    return dto;
  }

  /**
   * Преобразует DTO TourDto в сущность Tour.

   * @param dto DTO
   * @return сущность
   */

  public Tour toEntity(TourDto dto) {
    if (dto == null) {
      return null;
    }
    Tour tour = new Tour();
    tour.setId(dto.getId());
    tour.setName(dto.getName());
    tour.setDescription(dto.getDescription());
    tour.setCountry(dto.getCountry());
    tour.setDurationDays(dto.getDurationDays());
    tour.setPrice(dto.getPrice());
    tour.setHot(dto.isHot());
    return tour;
  }
}