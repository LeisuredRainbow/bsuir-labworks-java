package by.bsuir.labworks.booking.entity;

import by.bsuir.labworks.client.entity.Client;
import by.bsuir.labworks.tour.entity.Tour;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Table(name = "bookings")
@Data
public class Booking {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "client_id", nullable = false)
  private Client client;

  @ManyToOne
  @JoinColumn(name = "tour_id", nullable = false)
  private Tour tour;

  @Column(nullable = false)
  private LocalDate bookingDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BookingStatus status;

  public enum BookingStatus {
    CONFIRMED,
    PENDING,
    CANCELLED
  }
}