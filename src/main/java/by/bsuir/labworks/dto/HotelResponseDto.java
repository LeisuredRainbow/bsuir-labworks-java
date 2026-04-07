package by.bsuir.labworks.dto;

import lombok.Data;

@Data
public class HotelResponseDto {
  private Long id;
  private String name;
  private String address;
  private Integer stars;
}