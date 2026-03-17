package by.bsuir.labworks.tour.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;

@Entity
@Table(name = "tours")
@Data
public class Tour {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String country;

  private Integer durationDays;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  private Boolean hot; // true — горящий тур

  private String description; // описание тура
}