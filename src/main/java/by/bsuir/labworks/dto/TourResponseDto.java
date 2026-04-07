package by.bsuir.labworks.dto;

import java.math.BigDecimal;
import java.util.List;
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
  private List<Long> hotelIds;
  private List<Long> guideIds;
}