package by.bsuir.labworks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.bsuir.labworks.cache.BookingSearchCache;
import by.bsuir.labworks.cache.BookingSearchKey;
import by.bsuir.labworks.dto.BookingRequestDto;
import by.bsuir.labworks.dto.BookingResponseDto;
import by.bsuir.labworks.entity.Booking;
import by.bsuir.labworks.entity.Booking.BookingStatus;
import by.bsuir.labworks.entity.Client;
import by.bsuir.labworks.entity.Tour;
import by.bsuir.labworks.exception.PartialBulkOperationException;
import by.bsuir.labworks.mapper.BookingMapper;
import by.bsuir.labworks.projection.BookingNativeProjection;
import by.bsuir.labworks.repository.BookingRepository;
import by.bsuir.labworks.repository.ClientRepository;
import by.bsuir.labworks.repository.GuideRepository;
import by.bsuir.labworks.repository.TourRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

  @Mock
  private BookingRepository bookingRepository;

  @Mock
  private BookingMapper bookingMapper;

  @Mock
  private ClientRepository clientRepository;

  @Mock
  private TourRepository tourRepository;

  @Mock
  private GuideRepository guideRepository;

  @Mock
  private BookingSearchCache bookingSearchCache;

  private BookingService bookingService;

  @BeforeEach
  void setUp() {
    bookingService = new BookingService(
        bookingRepository,
        bookingMapper,
        clientRepository,
        tourRepository,
        guideRepository,
        bookingSearchCache,
        null);
    ReflectionTestUtils.setField(bookingService, "self", bookingService);
  }

  @Test
  void getAllBookingsMapsEntities() {
    Booking booking = new Booking();
    BookingResponseDto response = new BookingResponseDto();
    when(bookingRepository.findAll()).thenReturn(List.of(booking));
    when(bookingMapper.toResponseDto(booking)).thenReturn(response);

    List<BookingResponseDto> result = bookingService.getAllBookings();

    assertThat(result).containsExactly(response);
  }

  @Test
  void getBookingByIdThrowsWhenMissing() {
    when(bookingRepository.findById(10L)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> bookingService.getBookingById(10L))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Booking not found with id: 10");
  }

  @Test
  void getBookingByIdMapsEntity() {
    Booking booking = new Booking();
    BookingResponseDto response = new BookingResponseDto();
    when(bookingRepository.findById(10L)).thenReturn(java.util.Optional.of(booking));
    when(bookingMapper.toResponseDto(booking)).thenReturn(response);

    BookingResponseDto result = bookingService.getBookingById(10L);

    assertThat(result).isSameAs(response);
  }

  @Test
  void getBookingsByClientIdMapsEntities() {
    Booking booking = new Booking();
    BookingResponseDto response = new BookingResponseDto();
    when(bookingRepository.findByClientId(2L)).thenReturn(List.of(booking));
    when(bookingMapper.toResponseDto(booking)).thenReturn(response);

    List<BookingResponseDto> result = bookingService.getBookingsByClientId(2L);

    assertThat(result).containsExactly(response);
  }

  @Test
  void getBookingsByTourIdMapsEntities() {
    Booking booking = new Booking();
    BookingResponseDto response = new BookingResponseDto();
    when(bookingRepository.findByTourId(3L)).thenReturn(List.of(booking));
    when(bookingMapper.toResponseDto(booking)).thenReturn(response);

    List<BookingResponseDto> result = bookingService.getBookingsByTourId(3L);

    assertThat(result).containsExactly(response);
  }

  @Test
  void createBookingRejectsInvalidDto() {
    BookingRequestDto dto = new BookingRequestDto();
    dto.setTourId(1L);

    assertThatThrownBy(() -> bookingService.createBooking(dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Either existing clientId");
  }

  @Test
  void createBookingUsesExistingClient() {
    BookingRequestDto dto = baseBookingRequest();
    dto.setClientId(7L);

    Client client = new Client();
    client.setId(7L);
    Tour tour = new Tour();
    tour.setId(11L);
    Booking booking = new Booking();
    booking.setId(99L);

    when(clientRepository.findById(7L)).thenReturn(java.util.Optional.of(client));
    when(tourRepository.findById(11L)).thenReturn(java.util.Optional.of(tour));
    when(bookingMapper.toEntity(dto)).thenReturn(new Booking());
    when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
    BookingResponseDto response = new BookingResponseDto();
    when(bookingMapper.toResponseDto(booking)).thenReturn(response);

    BookingResponseDto result = bookingService.createBooking(dto);

    assertThat(result).isSameAs(response);
    ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
    verify(bookingRepository).save(captor.capture());
    assertThat(captor.getValue().getClient()).isSameAs(client);
    assertThat(captor.getValue().getTour()).isSameAs(tour);
    verify(bookingSearchCache).invalidateAll();
  }

  @Test
  void createBookingRejectsMissingClient() {
    BookingRequestDto dto = baseBookingRequest();
    dto.setClientId(7L);

    when(clientRepository.findById(7L)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> bookingService.createBooking(dto))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Client not found with id: 7");
  }

  @Test
  void createBookingCreatesNewClient() {
    BookingRequestDto dto = baseBookingRequest();
    dto.setClientId(null);
    dto.setFirstName("Alex");
    dto.setLastName("Petrov");
    dto.setEmail("alex@example.com");
    dto.setPhone("+375291234567");

    when(clientRepository.findByEmail("alex@example.com")).thenReturn(java.util.Optional.empty());
    when(clientRepository.findByPhone("+375291234567")).thenReturn(java.util.Optional.empty());
    when(guideRepository.findByPhone("+375291234567")).thenReturn(java.util.Optional.empty());

    Client savedClient = new Client();
    savedClient.setId(1L);
    when(clientRepository.save(any(Client.class))).thenReturn(savedClient);

    Tour tour = new Tour();
    tour.setId(11L);
    when(tourRepository.findById(11L)).thenReturn(java.util.Optional.of(tour));

    Booking booking = new Booking();
    booking.setId(5L);
    when(bookingMapper.toEntity(dto)).thenReturn(new Booking());
    when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
    BookingResponseDto response = new BookingResponseDto();
    when(bookingMapper.toResponseDto(booking)).thenReturn(response);

    BookingResponseDto result = bookingService.createBooking(dto);

    assertThat(result).isSameAs(response);
    ArgumentCaptor<Client> clientCaptor = ArgumentCaptor.forClass(Client.class);
    verify(clientRepository).save(clientCaptor.capture());
    assertThat(clientCaptor.getValue().getFirstName()).isEqualTo("Alex");
    assertThat(clientCaptor.getValue().getLastName()).isEqualTo("Petrov");
    assertThat(clientCaptor.getValue().getEmail()).isEqualTo("alex@example.com");
    assertThat(clientCaptor.getValue().getPhone()).isEqualTo("+375291234567");
    verify(bookingSearchCache).invalidateAll();
  }

  @Test
  void createBookingRejectsExistingEmail() {
    BookingRequestDto dto = baseBookingRequest();
    dto.setClientId(null);
    dto.setFirstName("Alex");
    dto.setLastName("Petrov");
    dto.setEmail("alex@example.com");

    when(clientRepository.findByEmail("alex@example.com"))
        .thenReturn(java.util.Optional.of(new Client()));

    assertThatThrownBy(() -> bookingService.createBooking(dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("already exists");
  }

  @Test
  void createBookingRejectsPhoneUsedByClient() {
    BookingRequestDto dto = baseBookingRequest();
    dto.setClientId(null);
    dto.setFirstName("Alex");
    dto.setLastName("Petrov");
    dto.setEmail("alex@example.com");
    dto.setPhone("+375291234567");

    when(clientRepository.findByEmail("alex@example.com")).thenReturn(java.util.Optional.empty());
    when(clientRepository.findByPhone("+375291234567"))
        .thenReturn(java.util.Optional.of(new Client()));

    assertThatThrownBy(() -> bookingService.createBooking(dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("already exists");
  }

  @Test
  void createBookingRejectsPhoneUsedByGuide() {
    BookingRequestDto dto = baseBookingRequest();
    dto.setClientId(null);
    dto.setFirstName("Alex");
    dto.setLastName("Petrov");
    dto.setEmail("alex@example.com");
    dto.setPhone("+375291234567");

    when(clientRepository.findByEmail("alex@example.com")).thenReturn(java.util.Optional.empty());
    when(clientRepository.findByPhone("+375291234567"))
        .thenReturn(java.util.Optional.empty());
    when(guideRepository.findByPhone("+375291234567"))
        .thenReturn(java.util.Optional.of(new by.bsuir.labworks.entity.Guide()));

    assertThatThrownBy(() -> bookingService.createBooking(dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("already used by a guide");
  }

  @Test
  void createBookingRejectsMissingTour() {
    BookingRequestDto dto = baseBookingRequest();
    dto.setClientId(1L);
    when(clientRepository.findById(1L)).thenReturn(java.util.Optional.of(new Client()));
    when(tourRepository.findById(11L)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> bookingService.createBooking(dto))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Tour not found");
  }

  @Test
  void updateBookingRejectsMissingBooking() {
    when(bookingRepository.findById(5L)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> bookingService.updateBooking(5L, new BookingRequestDto()))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Booking not found");
  }

  @Test
  void updateBookingChangesClientAndTour() {
    Booking existing = new Booking();
    Client oldClient = new Client();
    oldClient.setId(1L);
    Tour oldTour = new Tour();
    oldTour.setId(10L);
    existing.setClient(oldClient);
    existing.setTour(oldTour);

    when(bookingRepository.findById(5L)).thenReturn(java.util.Optional.of(existing));

    Client newClient = new Client();
    newClient.setId(2L);
    when(clientRepository.findById(2L)).thenReturn(java.util.Optional.of(newClient));

    Tour newTour = new Tour();
    newTour.setId(12L);
    when(tourRepository.findById(12L)).thenReturn(java.util.Optional.of(newTour));

    BookingRequestDto dto = baseBookingRequest();
    dto.setClientId(2L);
    dto.setTourId(12L);
    dto.setBookingDate(LocalDate.now().plusDays(5));
    dto.setStatus(BookingStatus.CANCELLED);

    Booking saved = new Booking();
    saved.setId(5L);
    saved.setClient(newClient);
    saved.setTour(newTour);
    saved.setBookingDate(dto.getBookingDate());
    saved.setStatus(dto.getStatus());
    when(bookingRepository.save(existing)).thenReturn(saved);
    BookingResponseDto response = new BookingResponseDto();
    when(bookingMapper.toResponseDto(saved)).thenReturn(response);

    BookingResponseDto result = bookingService.updateBooking(5L, dto);

    assertThat(result).isSameAs(response);
    assertThat(existing.getClient()).isSameAs(newClient);
    assertThat(existing.getTour()).isSameAs(newTour);
    assertThat(existing.getBookingDate()).isEqualTo(dto.getBookingDate());
    assertThat(existing.getStatus()).isEqualTo(dto.getStatus());
    verify(bookingSearchCache).invalidateAll();
  }

  @Test
  void updateBookingRejectsMissingClient() {
    Booking existing = new Booking();
    Client oldClient = new Client();
    oldClient.setId(1L);
    Tour oldTour = new Tour();
    oldTour.setId(10L);
    existing.setClient(oldClient);
    existing.setTour(oldTour);

    when(bookingRepository.findById(5L)).thenReturn(java.util.Optional.of(existing));

    BookingRequestDto dto = baseBookingRequest();
    dto.setClientId(2L);

    when(clientRepository.findById(2L)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> bookingService.updateBooking(5L, dto))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Client not found with id: 2");
  }

  @Test
  void updateBookingRejectsMissingTour() {
    Booking existing = new Booking();
    Client oldClient = new Client();
    oldClient.setId(1L);
    Tour oldTour = new Tour();
    oldTour.setId(10L);
    existing.setClient(oldClient);
    existing.setTour(oldTour);

    when(bookingRepository.findById(5L)).thenReturn(java.util.Optional.of(existing));

    BookingRequestDto dto = baseBookingRequest();
    dto.setTourId(12L);

    when(tourRepository.findById(12L)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> bookingService.updateBooking(5L, dto))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Tour not found with id: 12");
  }

  @Test
  void updateBookingSkipsSameClientAndTour() {
    Booking existing = new Booking();
    Client client = new Client();
    client.setId(1L);
    Tour tour = new Tour();
    tour.setId(10L);
    existing.setClient(client);
    existing.setTour(tour);
    when(bookingRepository.findById(5L)).thenReturn(java.util.Optional.of(existing));

    BookingRequestDto dto = baseBookingRequest();
    dto.setClientId(1L);
    dto.setTourId(10L);
    dto.setBookingDate(LocalDate.now().plusDays(1));
    dto.setStatus(BookingStatus.PENDING);

    Booking saved = new Booking();
    saved.setId(5L);
    when(bookingRepository.save(existing)).thenReturn(saved);
    BookingResponseDto response = new BookingResponseDto();
    when(bookingMapper.toResponseDto(saved)).thenReturn(response);

    bookingService.updateBooking(5L, dto);

    verify(clientRepository, never()).findById(any());
    verify(tourRepository, never()).findById(any());
    verify(bookingSearchCache).invalidateAll();
  }

  @Test
  void deleteBookingRejectsMissing() {
    when(bookingRepository.existsById(99L)).thenReturn(false);

    assertThatThrownBy(() -> bookingService.deleteBooking(99L))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Booking not found");
  }

  @Test
  void deleteBookingRemovesAndInvalidatesCache() {
    when(bookingRepository.existsById(99L)).thenReturn(true);

    bookingService.deleteBooking(99L);

    verify(bookingRepository).deleteById(99L);
    verify(bookingSearchCache).invalidateAll();
  }

  @Test
  void searchBookingsByClientLastNameJpqlReturnsCached() {
    PageRequest pageable = PageRequest.of(0, 5, Sort.by("id").ascending());
    Page<BookingResponseDto> cached = new PageImpl<>(List.of(new BookingResponseDto()));
    when(bookingSearchCache.get(any(BookingSearchKey.class))).thenReturn(cached);

    Page<BookingResponseDto> result = bookingService.searchBookingsByClientLastNameJpql(
        "Ivanov", pageable);

    assertThat(result).isSameAs(cached);
    verify(bookingRepository, never()).findBookingsByClientLastNameJpql(eq("Ivanov"), eq(pageable));
  }

  @Test
  void searchBookingsByClientLastNameJpqlCachesResult() {
    PageRequest pageable = PageRequest.of(1, 10, Sort.by("bookingDate").descending());
    when(bookingSearchCache.get(any(BookingSearchKey.class))).thenReturn(null);

    Booking booking = new Booking();
    BookingResponseDto response = new BookingResponseDto();
    Page<Booking> bookings = new PageImpl<>(List.of(booking));
    when(bookingRepository.findBookingsByClientLastNameJpql("Petrov", pageable))
        .thenReturn(bookings);
    when(bookingMapper.toResponseDto(booking)).thenReturn(response);

    Page<BookingResponseDto> result = bookingService.searchBookingsByClientLastNameJpql(
        "Petrov", pageable);

    assertThat(result.getContent()).containsExactly(response);
    ArgumentCaptor<BookingSearchKey> keyCaptor = ArgumentCaptor.forClass(BookingSearchKey.class);
    verify(bookingSearchCache).put(keyCaptor.capture(), eq(result));
    BookingSearchKey key = keyCaptor.getValue();
    assertThat(key.getLastName()).isEqualTo("Petrov");
    assertThat(key.getPage()).isEqualTo(1);
    assertThat(key.getSize()).isEqualTo(10);
    assertThat(key.getSort()).isEqualTo(pageable.getSort().toString());
  }

  @Test
  void searchBookingsByClientLastNameNativeReturnsCached() {
    PageRequest pageable = PageRequest.of(0, 5, Sort.by("id").ascending());
    Page<BookingResponseDto> cached = new PageImpl<>(List.of(new BookingResponseDto()));
    when(bookingSearchCache.get(any(BookingSearchKey.class))).thenReturn(cached);

    Page<BookingResponseDto> result = bookingService.searchBookingsByClientLastNameNative(
        "Ivanov", pageable);

    assertThat(result).isSameAs(cached);
    verify(bookingRepository, never()).findBookingsByClientLastNameNative(eq("Ivanov"), eq(pageable));
  }

  @Test
  void searchBookingsByClientLastNameNativeMapsProjection() {
    PageRequest pageable = PageRequest.of(0, 5, Sort.by("id").ascending());
    when(bookingSearchCache.get(any(BookingSearchKey.class))).thenReturn(null);

    BookingNativeProjection projection = new BookingNativeProjection() {
      @Override
      public Long getId() {
        return 55L;
      }

      @Override
      public LocalDate getBookingDate() {
        return LocalDate.now().plusDays(3);
      }

      @Override
      public Long getClientId() {
        return 5L;
      }

      @Override
      public Long getTourId() {
        return 9L;
      }

      @Override
      public BookingStatus getStatus() {
        return BookingStatus.CONFIRMED;
      }
    };

    Page<BookingNativeProjection> projections = new PageImpl<>(List.of(projection));
    when(bookingRepository.findBookingsByClientLastNameNative("Smith", pageable))
        .thenReturn(projections);

    Page<BookingResponseDto> result = bookingService.searchBookingsByClientLastNameNative(
        "Smith", pageable);

    BookingResponseDto dto = result.getContent().getFirst();
    assertThat(dto.getId()).isEqualTo(55L);
    assertThat(dto.getClientId()).isEqualTo(5L);
    assertThat(dto.getTourId()).isEqualTo(9L);
    assertThat(dto.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    verify(bookingSearchCache).put(any(BookingSearchKey.class), eq(result));
  }

  @Test
  void createBulkBookingsDelegatesToSelf() {
    BookingService selfMock = mock(BookingService.class);
    ReflectionTestUtils.setField(bookingService, "self", selfMock);

    BookingResponseDto first = new BookingResponseDto();
    BookingResponseDto second = new BookingResponseDto();

    BookingRequestDto dto1 = baseBookingRequest();
    BookingRequestDto dto2 = baseBookingRequest();
    dto2.setTourId(22L);
    dto2.setTourId(22L);
    when(selfMock.createBooking(dto1)).thenReturn(first);
    when(selfMock.createBooking(dto2)).thenReturn(second);

    List<BookingResponseDto> result = bookingService.createBulkBookings(List.of(dto1, dto2));

    assertThat(result).containsExactly(first, second);
  }

  @Test
  void createBulkBookingsWithoutTransactionThrowsPartialException() {
    BookingService selfMock = mock(BookingService.class);
    ReflectionTestUtils.setField(bookingService, "self", selfMock);

    BookingRequestDto dto1 = baseBookingRequest();
    BookingRequestDto dto2 = baseBookingRequest();
    dto2.setTourId(22L);
    BookingResponseDto ok = new BookingResponseDto();
    when(selfMock.createBooking(dto1)).thenReturn(ok);
    doThrow(new RuntimeException()).when(selfMock).createBooking(dto2);

    assertThatThrownBy(() -> bookingService.createBulkBookingsWithoutTransaction(List.of(dto1, dto2)))
        .isInstanceOf(PartialBulkOperationException.class)
        .extracting("savedCount", "failedCount")
        .containsExactly(1, 1);
  }

  @Test
  void createBulkBookingsWithoutTransactionReturnsAllOnSuccess() {
    BookingService selfMock = mock(BookingService.class);
    ReflectionTestUtils.setField(bookingService, "self", selfMock);

    BookingRequestDto dto1 = baseBookingRequest();
    BookingRequestDto dto2 = baseBookingRequest();
    dto2.setTourId(22L);
    BookingResponseDto r1 = new BookingResponseDto();
    BookingResponseDto r2 = new BookingResponseDto();
    when(selfMock.createBooking(dto1)).thenReturn(r1);
    when(selfMock.createBooking(dto2)).thenReturn(r2);

    List<BookingResponseDto> result = bookingService.createBulkBookingsWithoutTransaction(
        List.of(dto1, dto2));

    assertThat(result).containsExactly(r1, r2);
  }

  @Test
  void createBulkBookingsWithoutTransactionCapturesFailureMessage() {
    BookingService selfMock = mock(BookingService.class);
    ReflectionTestUtils.setField(bookingService, "self", selfMock);

    BookingRequestDto dto1 = baseBookingRequest();
    BookingRequestDto dto2 = baseBookingRequest();
    dto2.setTourId(22L);
    BookingResponseDto ok = new BookingResponseDto();
    when(selfMock.createBooking(dto1)).thenReturn(ok);
    doThrow(new IllegalStateException("boom")).when(selfMock).createBooking(dto2);

    assertThatThrownBy(() -> bookingService.createBulkBookingsWithoutTransaction(List.of(dto1, dto2)))
        .isInstanceOfSatisfying(PartialBulkOperationException.class, ex ->
            assertThat(ex.getFailedOperations())
                .isEqualTo(Map.of("operation_2", "boom")));
  }

  private BookingRequestDto baseBookingRequest() {
    BookingRequestDto dto = new BookingRequestDto();
    dto.setTourId(11L);
    dto.setBookingDate(LocalDate.now().plusDays(2));
    dto.setStatus(BookingStatus.CONFIRMED);
    return dto;
  }
}
