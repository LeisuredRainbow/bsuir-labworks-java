package by.bsuir.labworks.booking.service;

import by.bsuir.labworks.booking.dto.BookingRequestDto;
import by.bsuir.labworks.booking.dto.BookingResponseDto;
import by.bsuir.labworks.booking.entity.Booking;
import by.bsuir.labworks.booking.mapper.BookingMapper;
import by.bsuir.labworks.booking.repository.BookingRepository;
import by.bsuir.labworks.client.entity.Client;
import by.bsuir.labworks.client.repository.ClientRepository;
import by.bsuir.labworks.tour.entity.Tour;
import by.bsuir.labworks.tour.repository.TourRepository;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingService {
  private final BookingRepository bookingRepository;
  private final BookingMapper bookingMapper;
  private final ClientRepository clientRepository;
  private final TourRepository tourRepository;

  public List<BookingResponseDto> getAllBookings() {
    return bookingRepository.findAll().stream()
            .map(bookingMapper::toResponseDto)
            .toList();
  }

  public BookingResponseDto getBookingById(Long id) {
    Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                    "Booking not found with id: " + id));
    return bookingMapper.toResponseDto(booking);
  }

  public List<BookingResponseDto> getBookingsByClientId(Long clientId) {
    return bookingRepository.findByClientId(clientId).stream()
            .map(bookingMapper::toResponseDto)
            .toList();
  }

  public List<BookingResponseDto> getBookingsByTourId(Long tourId) {
    return bookingRepository.findByTourId(tourId).stream()
            .map(bookingMapper::toResponseDto)
            .toList();
  }

  //@Transactional
  public BookingResponseDto createBooking(BookingRequestDto bookingDto) {
    if (!bookingDto.isValid()) {
      throw new IllegalArgumentException(
    "Необходимо указать либо существующий clientId, "
    + "либо данные нового клиента (firstName, lastName, email)");
    }

    Client client;
    if (bookingDto.getClientId() != null) {
      client = clientRepository.findById(bookingDto.getClientId())
                    .orElseThrow(() -> new NoSuchElementException(
                            "Client not found with id: " + bookingDto.getClientId()));
    } else {
      if (clientRepository.findByEmail(bookingDto.getEmail()).isPresent()) {
        throw new IllegalArgumentException(
                        "Client with email " + bookingDto.getEmail() + " already exists");
      }
      if (bookingDto.getPhone() != null
          && clientRepository.findByPhone(bookingDto.getPhone()).isPresent()) {
        throw new IllegalArgumentException(
                        "Client with phone " + bookingDto.getPhone() + " already exists");
      }
      Client newClient = new Client();
      newClient.setFirstName(bookingDto.getFirstName());
      newClient.setLastName(bookingDto.getLastName());
      newClient.setEmail(bookingDto.getEmail());
      newClient.setPhone(bookingDto.getPhone());
      client = clientRepository.save(newClient);
    }

    Tour tour = tourRepository.findById(bookingDto.getTourId())
            .orElseThrow(() -> new NoSuchElementException(
                    "Tour not found with id: " + bookingDto.getTourId()));

    Booking booking = bookingMapper.toEntity(bookingDto);
    booking.setClient(client);
    booking.setTour(tour);
    booking = bookingRepository.save(booking);
    return bookingMapper.toResponseDto(booking);
  }

  @Transactional
    public BookingResponseDto updateBooking(Long id, BookingRequestDto bookingDto) {
    Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Booking not found with id: " + id));

    if (bookingDto.getClientId() != null
          && !bookingDto.getClientId().equals(existingBooking.getClient().getId())) {
      Client client = clientRepository.findById(bookingDto.getClientId())
            .orElseThrow(() -> new NoSuchElementException(
            "Client not found with id: " + bookingDto.getClientId()));
      existingBooking.setClient(client);
    }

    if (bookingDto.getTourId() != null
          && !bookingDto.getTourId().equals(existingBooking.getTour().getId())) {
      Tour tour = tourRepository.findById(bookingDto.getTourId())
          .orElseThrow(() -> new NoSuchElementException(
          "Tour not found with id: " + bookingDto.getTourId()));
      existingBooking.setTour(tour);
    }

    existingBooking.setBookingDate(bookingDto.getBookingDate());
    existingBooking.setStatus(bookingDto.getStatus());
    existingBooking = bookingRepository.save(existingBooking);
    return bookingMapper.toResponseDto(existingBooking);
  }

  @Transactional
  public void deleteBooking(Long id) {
    if (!bookingRepository.existsById(id)) {
      throw new NoSuchElementException("Booking not found with id: " + id);
    }
    bookingRepository.deleteById(id);
  }
}