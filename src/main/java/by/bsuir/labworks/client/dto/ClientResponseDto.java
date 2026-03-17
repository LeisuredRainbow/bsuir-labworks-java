package by.bsuir.labworks.client.dto;

import lombok.Data;

@Data
public class ClientResponseDto {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String phone;
}