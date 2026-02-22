package by.bsuir.labworks.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tours")
@Data // Lombok: генерирует геттеры, сеттеры, toString, equals, hashCode
@NoArgsConstructor // Lombok: генерирует пустой конструктор
public class Tour {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  private String description;

  @Column(nullable = false)
  private String country;

  private Integer durationDays; // Продолжительность в днях

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price; // Использует BigDecimal для денег

  @Column(name = "is_hot")
  private boolean isHot; // Горящий тур или нет
}