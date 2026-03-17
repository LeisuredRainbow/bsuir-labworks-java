package by.bsuir.labworks.tour.entity;

import by.bsuir.labworks.booking.entity.Booking;
import by.bsuir.labworks.guide.entity.Guide;
import by.bsuir.labworks.hotel.entity.Hotel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

  private Boolean hot;

  private String description;

  @ManyToMany
  @JoinTable(
      name = "tour_hotel",
      joinColumns = @JoinColumn(name = "tour_id"),
      inverseJoinColumns = @JoinColumn(name = "hotel_id")
  )
  private List<Hotel> hotels = new ArrayList<>();

  @ManyToMany
  @JoinTable(
      name = "tour_guide",
      joinColumns = @JoinColumn(name = "tour_id"),
      inverseJoinColumns = @JoinColumn(name = "guide_id")
  )
  private List<Guide> guides = new ArrayList<>();

  @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Booking> bookings = new ArrayList<>();
}