package by.bsuir.labworks.tour.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class TourResponseDto {
  private Long id;
  private String name;
  private String country;
  private Integer durationDays;
  private BigDecimal price;
  private Boolean hot;
  private String description;
}