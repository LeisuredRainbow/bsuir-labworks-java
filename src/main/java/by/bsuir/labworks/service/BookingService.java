package by.bsuir.labworks.service;

import by.bsuir.labworks.cache.BookingSearchCache;
import by.bsuir.labworks.cache.BookingSearchKey;
import by.bsuir.labworks.dto.BookingRequestDto;
import by.bsuir.labworks.dto.BookingResponseDto;
import by.bsuir.labworks.entity.Booking;
import by.bsuir.labworks.entity.Client;
import by.bsuir.labworks.entity.Tour;
import by.bsuir.labworks.exception.PartialBulkOperationException;
import by.bsuir.labworks.mapper.BookingMapper;
import by.bsuir.labworks.projection.BookingNativeProjection;
import by.bsuir.labworks.repository.BookingRepository;
import by.bsuir.labworks.repository.ClientRepository;
import by.bsuir.labworks.repository.GuideRepository;
import by.bsuir.labworks.repository.TourRepository;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingService {

  private static final Logger LOG = LoggerFactory.getLogger(BookingService.class);
  private static final String BOOKING_NOT_FOUND_MSG = "Booking not found with id: ";

  private final BookingRepository bookingRepository;
  private final BookingMapper bookingMapper;
  private final ClientRepository clientRepository;
  private final TourRepository tourRepository;
  private final GuideRepository guideRepository;
  private final BookingSearchCache bookingSearchCache;

  public List<BookingResponseDto> getAllBookings() {
    LOG.debug("Fetching all bookings");
    return bookingRepository.findAll().stream()
        .map(bookingMapper::toResponseDto)
        .toList();
  }

  public BookingResponseDto getBookingById(Long id) {
    LOG.debug("Fetching booking by id={}", id);
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException(BOOKING_NOT_FOUND_MSG + id));
    return bookingMapper.toResponseDto(booking);
  }

  public List<BookingResponseDto> getBookingsByClientId(Long clientId) {
    LOG.debug("Fetching bookings by client id={}", clientId);
    return bookingRepository.findByClientId(clientId).stream()
        .map(bookingMapper::toResponseDto)
        .toList();
  }

  public List<BookingResponseDto> getBookingsByTourId(Long tourId) {
    LOG.debug("Fetching bookings by tour id={}", tourId);
    return bookingRepository.findByTourId(tourId).stream()
        .map(bookingMapper::toResponseDto)
        .toList();
  }

  @Transactional
  public BookingResponseDto createBooking(BookingRequestDto bookingDto) {
    return toResponseDto(createBookingInternal(bookingDto));
  }

  @Transactional
  public BookingResponseDto updateBooking(Long id, BookingRequestDto bookingDto) {
    LOG.info("Updating booking id={}", id);
    Booking existingBooking = bookingRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException(BOOKING_NOT_FOUND_MSG + id));

    if (bookingDto.getClientId() != null
        && !bookingDto.getClientId().equals(existingBooking.getClient().getId())) {
      Client client = clientRepository.findById(bookingDto.getClientId())
          .orElseThrow(() -> new NoSuchElementException(
              "Client not found with id: " + bookingDto.getClientId()));
      existingBooking.setClient(client);
      LOG.debug("Changed client to id={}", client.getId());
    }

    if (bookingDto.getTourId() != null
        && !bookingDto.getTourId().equals(existingBooking.getTour().getId())) {
      Tour tour = tourRepository.findById(bookingDto.getTourId())
          .orElseThrow(() -> new NoSuchElementException(
              "Tour not found with id: " + bookingDto.getTourId()));
      existingBooking.setTour(tour);
      LOG.debug("Changed tour to id={}", tour.getId());
    }

    existingBooking.setBookingDate(bookingDto.getBookingDate());
    existingBooking.setStatus(bookingDto.getStatus());
    existingBooking = bookingRepository.save(existingBooking);
    bookingSearchCache.invalidateAll();
    LOG.info("Booking updated id={}", existingBooking.getId());
    return bookingMapper.toResponseDto(existingBooking);
  }

  @Transactional
  public void deleteBooking(Long id) {
    LOG.info("Deleting booking id={}", id);
    if (!bookingRepository.existsById(id)) {
      throw new NoSuchElementException(BOOKING_NOT_FOUND_MSG + id);
    }
    bookingRepository.deleteById(id);
    bookingSearchCache.invalidateAll();
    LOG.info("Booking deleted id={}", id);
  }

  @Transactional(readOnly = true)
  public Page<BookingResponseDto> searchBookingsByClientLastNameJpql(String lastName,
                                                                     Pageable pageable) {
    LOG.debug("JPQL search bookings by client last name: {}", lastName);
    String sortStr = pageable.getSort().toString();
    BookingSearchKey key = new BookingSearchKey(lastName, pageable.getPageNumber(),
        pageable.getPageSize(), sortStr);
    Page<BookingResponseDto> cached = bookingSearchCache.get(key);
    if (cached != null) {
      LOG.debug("JPQL search: result from cache");
      return cached;
    }
    LOG.debug("JPQL search: cache miss, querying database");
    Page<Booking> bookings = bookingRepository.findBookingsByClientLastNameJpql(lastName,
        pageable);
    Page<BookingResponseDto> result = bookings.map(bookingMapper::toResponseDto);
    bookingSearchCache.put(key, result);
    return result;
  }

  @Transactional(readOnly = true)
  public Page<BookingResponseDto> searchBookingsByClientLastNameNative(String lastName,
                                                                       Pageable pageable) {
    LOG.debug("Native search bookings by client last name: {}", lastName);
    String sortStr = pageable.getSort().toString();
    BookingSearchKey key = new BookingSearchKey(lastName, pageable.getPageNumber(),
        pageable.getPageSize(), sortStr);
    Page<BookingResponseDto> cached = bookingSearchCache.get(key);
    if (cached != null) {
      LOG.debug("Native search: result from cache");
      return cached;
    }
    LOG.debug("Native search: cache miss, querying database");
    Page<BookingNativeProjection> projections =
        bookingRepository.findBookingsByClientLastNameNative(lastName, pageable);
    Page<BookingResponseDto> result = projections.map(this::toResponseDto);
    bookingSearchCache.put(key, result);
    return result;
  }

  @Transactional
  public List<BookingResponseDto> createBulkBookings(List<BookingRequestDto> bookingDtos) {
    LOG.info("Creating bulk bookings with transaction, size={}", bookingDtos.size());
    return bookingDtos.stream()
        .map(this::createBookingInternal)
        .map(this::toResponseDto)
        .toList();
  }

  public List<BookingResponseDto> createBulkBookingsWithoutTransaction(
        List<BookingRequestDto> bookingDtos) {
    LOG.info("Creating bulk bookings WITHOUT transaction, size={}", bookingDtos.size());
    List<BookingResponseDto> successful = new java.util.ArrayList<>();
    java.util.Map<String, String> failedOperations = new java.util.LinkedHashMap<>();

    for (int i = 0; i < bookingDtos.size(); i++) {
      BookingRequestDto dto = bookingDtos.get(i);
      String operationKey = "operation_" + (i + 1);
      try {
        Booking saved = createBookingInternal(dto);
        successful.add(toResponseDto(saved));
      } catch (RuntimeException ex) {
        String message = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
        failedOperations.put(operationKey, message);
        LOG.warn("Failed to create booking in non-transactional bulk: {} - {}",
            operationKey, message);
      }
    }

    if (!failedOperations.isEmpty()) {
      throw new PartialBulkOperationException(
          "Some bookings were not saved",
          successful.size(),
          failedOperations.size(),
          failedOperations);
    }

    LOG.info("Successfully created {} bookings without transaction", successful.size());
    return successful;
  }

  private Booking createBookingInternal(BookingRequestDto bookingDto) {
    LOG.info("Creating new booking");
    if (!bookingDto.isValid()) {
      throw new IllegalArgumentException(
          "Either existing clientId or new"
          + " client data (firstName, lastName, email) must be provided");
    }

    Client client;
    if (bookingDto.getClientId() != null) {
      client = clientRepository.findById(bookingDto.getClientId())
          .orElseThrow(() -> new NoSuchElementException(
              "Client not found with id: " + bookingDto.getClientId()));
      LOG.debug("Using existing client id={}", client.getId());
    } else {
      if (clientRepository.findByEmail(bookingDto.getEmail()).isPresent()) {
        throw new IllegalArgumentException(
            "Client with email " + bookingDto.getEmail() + " already exists");
      }
      if (bookingDto.getPhone() != null) {
        if (clientRepository.findByPhone(bookingDto.getPhone()).isPresent()) {
          throw new IllegalArgumentException(
              "Client with phone " + bookingDto.getPhone() + " already exists");
        }
        if (guideRepository.findByPhone(bookingDto.getPhone()).isPresent()) {
          throw new IllegalArgumentException(
              "Phone " + bookingDto.getPhone() + " is already used by a guide");
        }
      }
      Client newClient = new Client();
      newClient.setFirstName(bookingDto.getFirstName());
      newClient.setLastName(bookingDto.getLastName());
      newClient.setEmail(bookingDto.getEmail());
      newClient.setPhone(bookingDto.getPhone());
      client = clientRepository.save(newClient);
      LOG.debug("Created new client id={}", client.getId());
    }

    Tour tour = tourRepository.findById(bookingDto.getTourId())
        .orElseThrow(() -> new NoSuchElementException(
            "Tour not found with id: " + bookingDto.getTourId()));

    Booking booking = bookingMapper.toEntity(bookingDto);
    booking.setClient(client);
    booking.setTour(tour);
    booking = bookingRepository.save(booking);
    bookingSearchCache.invalidateAll();
    LOG.info("Booking created with id={}", booking.getId());
    return booking;
  }

  private BookingResponseDto toResponseDto(Booking booking) {
    return bookingMapper.toResponseDto(booking);
  }

  private BookingResponseDto toResponseDto(BookingNativeProjection proj) {
    BookingResponseDto dto = new BookingResponseDto();
    dto.setId(proj.getId());
    dto.setBookingDate(proj.getBookingDate());
    dto.setClientId(proj.getClientId());
    dto.setTourId(proj.getTourId());
    dto.setStatus(proj.getStatus());
    return dto;
  }
}