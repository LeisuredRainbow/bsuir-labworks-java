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
import java.util.Optional;
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
  private static final String ID_CANNOT_BE_NULL = "ID cannot be null";
  private static final String BOOKING_DATA_CANNOT_BE_NULL = "Booking data cannot be null";
  private static final String BOOKING_LIST_CANNOT_BE_NULL = "Booking list cannot be null";
  private static final String LAST_NAME_CANNOT_BE_NULL = "Last name cannot be null";
  private static final String PAGEABLE_CANNOT_BE_NULL = "Pageable cannot be null";
  private static final String BOOKING_CANNOT_BE_NULL = "Booking cannot be null";
  private static final String PROJECTION_CANNOT_BE_NULL = "Projection cannot be null";
  private static final String CLIENT_ID_CANNOT_BE_NULL = "Client ID cannot be null";
  private static final String TOUR_ID_CANNOT_BE_NULL = "Tour ID cannot be null";

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
    Long safeId = Optional.ofNullable(id)
        .orElseThrow(() -> new IllegalArgumentException(ID_CANNOT_BE_NULL));
    LOG.debug("Fetching booking by id={}", safeId);
    Booking booking = bookingRepository.findById(safeId)
        .orElseThrow(() -> new NoSuchElementException(BOOKING_NOT_FOUND_MSG + safeId));
    return bookingMapper.toResponseDto(booking);
  }

  public List<BookingResponseDto> getBookingsByClientId(Long clientId) {
    Long safeClientId = Optional.ofNullable(clientId)
        .orElseThrow(() -> new IllegalArgumentException(CLIENT_ID_CANNOT_BE_NULL));
    LOG.debug("Fetching bookings by client id={}", safeClientId);
    return bookingRepository.findByClientId(safeClientId).stream()
        .map(bookingMapper::toResponseDto)
        .toList();
  }

  public List<BookingResponseDto> getBookingsByTourId(Long tourId) {
    Long safeTourId = Optional.ofNullable(tourId)
        .orElseThrow(() -> new IllegalArgumentException(TOUR_ID_CANNOT_BE_NULL));
    LOG.debug("Fetching bookings by tour id={}", safeTourId);
    return bookingRepository.findByTourId(safeTourId).stream()
        .map(bookingMapper::toResponseDto)
        .toList();
  }

  @Transactional
  public BookingResponseDto createBooking(BookingRequestDto bookingDto) {
    BookingRequestDto safeDto = Optional.ofNullable(bookingDto)
        .orElseThrow(() -> new IllegalArgumentException(BOOKING_DATA_CANNOT_BE_NULL));
    return toResponseDto(createBookingInternal(safeDto));
  }

  @Transactional
  public BookingResponseDto updateBooking(Long id, BookingRequestDto bookingDto) {
    Long safeId = Optional.ofNullable(id)
        .orElseThrow(() -> new IllegalArgumentException(ID_CANNOT_BE_NULL));
    BookingRequestDto safeDto = Optional.ofNullable(bookingDto)
        .orElseThrow(() -> new IllegalArgumentException(BOOKING_DATA_CANNOT_BE_NULL));
    LOG.info("Updating booking id={}", safeId);
    Booking existingBooking = bookingRepository.findById(safeId)
        .orElseThrow(() -> new NoSuchElementException(BOOKING_NOT_FOUND_MSG + safeId));

    if (safeDto.getClientId() != null
        && !safeDto.getClientId().equals(existingBooking.getClient().getId())) {
      Client client = clientRepository.findById(safeDto.getClientId())
          .orElseThrow(() -> new NoSuchElementException(
              "Client not found with id: " + safeDto.getClientId()));
      existingBooking.setClient(client);
      LOG.debug("Changed client to id={}", client.getId());
    }

    if (safeDto.getTourId() != null
        && !safeDto.getTourId().equals(existingBooking.getTour().getId())) {
      Tour tour = tourRepository.findById(safeDto.getTourId())
          .orElseThrow(() -> new NoSuchElementException(
              "Tour not found with id: " + safeDto.getTourId()));
      existingBooking.setTour(tour);
      LOG.debug("Changed tour to id={}", tour.getId());
    }

    existingBooking.setBookingDate(safeDto.getBookingDate());
    existingBooking.setStatus(safeDto.getStatus());
    existingBooking = bookingRepository.save(existingBooking);
    bookingSearchCache.invalidateAll();
    LOG.info("Booking updated id={}", existingBooking.getId());
    return bookingMapper.toResponseDto(existingBooking);
  }

  @Transactional
  public void deleteBooking(Long id) {
    Long safeId = Optional.ofNullable(id)
        .orElseThrow(() -> new IllegalArgumentException(ID_CANNOT_BE_NULL));
    LOG.info("Deleting booking id={}", safeId);
    if (!bookingRepository.existsById(safeId)) {
      throw new NoSuchElementException(BOOKING_NOT_FOUND_MSG + safeId);
    }
    bookingRepository.deleteById(safeId);
    bookingSearchCache.invalidateAll();
    LOG.info("Booking deleted id={}", safeId);
  }

  @Transactional(readOnly = true)
  public Page<BookingResponseDto> searchBookingsByClientLastNameJpql(
        String lastName, Pageable pageable) {
    return searchWithCache(lastName, pageable, false);
  }

  @Transactional(readOnly = true)
  public Page<BookingResponseDto> searchBookingsByClientLastNameNative(
        String lastName, Pageable pageable) {
    return searchWithCache(lastName, pageable, true);
  }

  @Transactional
  public List<BookingResponseDto> createBulkBookings(List<BookingRequestDto> bookingDtos) {
    List<BookingRequestDto> safeList = Optional.ofNullable(bookingDtos)
        .orElseThrow(() -> new IllegalArgumentException(BOOKING_LIST_CANNOT_BE_NULL));
    LOG.info("Creating bulk bookings with transaction, size={}", safeList.size());
    return safeList.stream()
        .map(this::createBookingInternal)
        .map(this::toResponseDto)
        .toList();
  }

  public List<BookingResponseDto> createBulkBookingsWithoutTransaction(
        List<BookingRequestDto> bookingDtos) {
    List<BookingRequestDto> safeList = Optional.ofNullable(bookingDtos)
        .orElseThrow(() -> new IllegalArgumentException(BOOKING_LIST_CANNOT_BE_NULL));
    LOG.info("Creating bulk bookings WITHOUT transaction, size={}", safeList.size());
    List<BookingResponseDto> successful = new java.util.ArrayList<>();
    java.util.Map<String, String> failedOperations = new java.util.LinkedHashMap<>();

    for (int i = 0; i < safeList.size(); i++) {
      BookingRequestDto dto = safeList.get(i);
      String operationKey = "operation_" + (i + 1);
      try {
        Booking saved = createBookingInternal(dto);
        successful.add(toResponseDto(saved));
      } catch (RuntimeException ex) {
        String message = ex.getMessage() == null
            ? ex.getClass().getSimpleName() : ex.getMessage();
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
    BookingRequestDto safeDto = Optional.ofNullable(bookingDto)
        .orElseThrow(() -> new IllegalArgumentException(BOOKING_DATA_CANNOT_BE_NULL));
    LOG.info("Creating new booking");
    if (!safeDto.isValid()) {
      throw new IllegalArgumentException(
          "Either existing clientId or new client data "
          + "(firstName, lastName, email) must be provided");
    }

    Client client;
    if (safeDto.getClientId() != null) {
      client = clientRepository.findById(safeDto.getClientId())
          .orElseThrow(() -> new NoSuchElementException(
              "Client not found with id: " + safeDto.getClientId()));
      LOG.debug("Using existing client id={}", client.getId());
    } else {
      if (clientRepository.findByEmail(safeDto.getEmail()).isPresent()) {
        throw new IllegalArgumentException(
            "Client with email " + safeDto.getEmail() + " already exists");
      }
      if (safeDto.getPhone() != null) {
        if (clientRepository.findByPhone(safeDto.getPhone()).isPresent()) {
          throw new IllegalArgumentException(
              "Client with phone " + safeDto.getPhone() + " already exists");
        }
        if (guideRepository.findByPhone(safeDto.getPhone()).isPresent()) {
          throw new IllegalArgumentException(
              "Phone " + safeDto.getPhone() + " is already used by a guide");
        }
      }
      Client newClient = new Client();
      newClient.setFirstName(safeDto.getFirstName());
      newClient.setLastName(safeDto.getLastName());
      newClient.setEmail(safeDto.getEmail());
      newClient.setPhone(safeDto.getPhone());
      client = clientRepository.save(newClient);
      LOG.debug("Created new client id={}", client.getId());
    }

    Tour tour = tourRepository.findById(safeDto.getTourId())
        .orElseThrow(() -> new NoSuchElementException(
            "Tour not found with id: " + safeDto.getTourId()));

    Booking booking = bookingMapper.toEntity(safeDto);
    booking.setClient(client);
    booking.setTour(tour);
    booking = bookingRepository.save(booking);
    bookingSearchCache.invalidateAll();
    LOG.info("Booking created with id={}", booking.getId());
    return booking;
  }

  private Page<BookingResponseDto> searchWithCache(String lastName,
      Pageable pageable, boolean nativeQuery) {
    String safeLastName = Optional.ofNullable(lastName)
        .orElseThrow(() -> new IllegalArgumentException(LAST_NAME_CANNOT_BE_NULL));
    Pageable safePageable = Optional.ofNullable(pageable)
        .orElseThrow(() -> new IllegalArgumentException(PAGEABLE_CANNOT_BE_NULL));
    String queryType = nativeQuery ? "Native" : "JPQL";
    LOG.debug("{} search bookings by client last name: {}", queryType, safeLastName);
    String sortStr = safePageable.getSort().toString();
    BookingSearchKey key = new BookingSearchKey(safeLastName, safePageable.getPageNumber(),
        safePageable.getPageSize(), sortStr);
    Page<BookingResponseDto> cached = bookingSearchCache.get(key);
    if (cached != null) {
      LOG.debug("{} search: result from cache", queryType);
      return cached;
    }
    LOG.debug("{} search: cache miss, querying database", queryType);
    Page<BookingResponseDto> result;
    if (nativeQuery) {
      Page<BookingNativeProjection> projections = bookingRepository
          .findBookingsByClientLastNameNative(safeLastName, safePageable);
      result = projections.map(this::toResponseDto);
    } else {
      Page<Booking> bookings = bookingRepository
          .findBookingsByClientLastNameJpql(safeLastName, safePageable);
      result = bookings.map(bookingMapper::toResponseDto);
    }
    bookingSearchCache.put(key, result);
    return result;
  }

  private BookingResponseDto toResponseDto(Booking booking) {
    return Optional.ofNullable(booking)
        .map(bookingMapper::toResponseDto)
        .orElseThrow(() -> new IllegalArgumentException(BOOKING_CANNOT_BE_NULL));
  }

  private BookingResponseDto toResponseDto(BookingNativeProjection proj) {
    return Optional.ofNullable(proj)
        .map(p -> {
          BookingResponseDto dto = new BookingResponseDto();
          dto.setId(p.getId());
          dto.setBookingDate(p.getBookingDate());
          dto.setClientId(p.getClientId());
          dto.setTourId(p.getTourId());
          dto.setStatus(p.getStatus());
          return dto;
        })
        .orElseThrow(() -> new IllegalArgumentException(PROJECTION_CANNOT_BE_NULL));
  }

  @Transactional
  public void confirmBooking(Long id) {
    Long safeId = Optional.ofNullable(id)
        .orElseThrow(() -> new IllegalArgumentException("ID cannot be null"));
    LOG.info("Confirming booking id={}", safeId);
    Booking booking = bookingRepository.findById(safeId)
        .orElseThrow(() -> new NoSuchElementException("Booking not found with id: " + safeId));
    if (booking.getStatus() == Booking.BookingStatus.CONFIRMED) {
      LOG.info("Booking id={} is already confirmed", safeId);
      return;
    }
    if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
      throw new IllegalStateException("Cannot confirm a cancelled booking");
    }
    booking.setStatus(Booking.BookingStatus.CONFIRMED);
    bookingRepository.save(booking);
    bookingSearchCache.invalidateAll();
    LOG.info("Booking confirmed id={}", safeId);
  }
}