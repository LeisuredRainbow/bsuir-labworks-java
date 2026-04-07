package by.bsuir.labworks.dto;

import lombok.Data;

@Data
public class GuideResponseDto {
  private Long id;
  private String firstName;
  private String lastName;
  private String phone;
  private String email;
  private Integer experienceYears;
}