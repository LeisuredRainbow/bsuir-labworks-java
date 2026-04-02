package by.bsuir.labworks.hotel.entity;

import by.bsuir.labworks.tour.entity.Tour;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "hotels",
       uniqueConstraints = @UniqueConstraint(columnNames = {"address"}))
@Data
public class Hotel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  private String address;

  private Integer stars;

  @ManyToMany(mappedBy = "hotels")
  private List<Tour> tours = new ArrayList<>();
}