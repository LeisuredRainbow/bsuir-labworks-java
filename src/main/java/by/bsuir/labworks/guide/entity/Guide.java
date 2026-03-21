package by.bsuir.labworks.guide.entity;

import by.bsuir.labworks.tour.entity.Tour;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "guides")
@Data
public class Guide {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @Column(unique = true)
  private String phone;

  @Column(unique = true)
  private String email;

  private Integer experienceYears;

  @ManyToMany(mappedBy = "guides")
  private List<Tour> tours = new ArrayList<>();
}