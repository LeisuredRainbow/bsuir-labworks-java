package by.bsuir.labworks.dto;

import java.math.BigDecimal;
import lombok.Data;
/**
 * DTO для передачи данных тура.
 * Содержит все поля, которые возвращаются клиенту или принимаются от клиента.
 */

@Data
public class TourDto {
  private Long id;
  private String name;
  private String description;
  private String country;
  private Integer durationDays;
  private BigDecimal price;
  private boolean isHot;
}