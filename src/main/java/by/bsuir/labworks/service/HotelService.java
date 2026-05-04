package by.bsuir.labworks.service;

import by.bsuir.labworks.dto.HotelRequestDto;
import by.bsuir.labworks.dto.HotelResponseDto;
import by.bsuir.labworks.entity.Hotel;
import by.bsuir.labworks.mapper.HotelMapper;
import by.bsuir.labworks.repository.HotelRepository;
import by.bsuir.labworks.repository.TourRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HotelService {

  private static final Logger LOG = LoggerFactory.getLogger(HotelService.class);
  private static final String HOTEL_NOT_FOUND_MSG = "Hotel not found with id: ";

  private final HotelRepository hotelRepository;
  private final HotelMapper hotelMapper;
  private final TourRepository tourRepository;

  public List<HotelResponseDto> getAllHotels() {
    LOG.debug("Fetching all hotels");
    return hotelRepository.findAll().stream()
        .map(hotelMapper::toResponseDto)
        .toList();
  }

  public HotelResponseDto getHotelById(Long id) {
    Long safeId = Optional.ofNullable(id)
        .orElseThrow(() -> new IllegalArgumentException("ID cannot be null"));
    LOG.debug("Fetching hotel by id={}", safeId);
    Hotel hotel = hotelRepository.findById(safeId)
        .orElseThrow(() -> new NoSuchElementException(HOTEL_NOT_FOUND_MSG + safeId));
    return hotelMapper.toResponseDto(hotel);
  }

  public HotelResponseDto getHotelByAddress(String address) {
    String safeAddress = Optional.ofNullable(address)
        .orElseThrow(() -> new IllegalArgumentException("Address cannot be null"));
    LOG.debug("Fetching hotel by address={}", safeAddress);
    Hotel hotel = hotelRepository.findByAddress(safeAddress)
        .orElseThrow(() -> new NoSuchElementException(
            "Hotel not found with address: " + safeAddress));
    return hotelMapper.toResponseDto(hotel);
  }

  public HotelResponseDto createHotel(HotelRequestDto hotelDto) {
    HotelRequestDto safeDto = Optional.ofNullable(hotelDto)
        .orElseThrow(() -> new IllegalArgumentException("Hotel data cannot be null"));
    LOG.info("Creating new hotel");
    if (safeDto.getAddress() != null
        && hotelRepository.findByAddress(safeDto.getAddress()).isPresent()) {
      throw new IllegalArgumentException(
          "Hotel with address " + safeDto.getAddress() + " already exists");
    }
    Hotel hotel = hotelMapper.toEntity(safeDto);
    hotel = hotelRepository.save(hotel);
    LOG.info("Hotel created with id={}", hotel.getId());
    return hotelMapper.toResponseDto(hotel);
  }

  public HotelResponseDto updateHotel(Long id, HotelRequestDto hotelDto) {
    Long safeId = Optional.ofNullable(id)
        .orElseThrow(() -> new IllegalArgumentException("ID cannot be null"));
    HotelRequestDto safeDto = Optional.ofNullable(hotelDto)
        .orElseThrow(() -> new IllegalArgumentException("Hotel data cannot be null"));
    LOG.info("Updating hotel id={}", safeId);
    Hotel existingHotel = hotelRepository.findById(safeId)
        .orElseThrow(() -> new NoSuchElementException(HOTEL_NOT_FOUND_MSG + safeId));
    if (safeDto.getAddress() != null
        && !safeDto.getAddress().equals(existingHotel.getAddress())
        && hotelRepository.findByAddress(safeDto.getAddress()).isPresent()) {
      throw new IllegalArgumentException(
          "Hotel with address " + safeDto.getAddress() + " already exists");
    }
    existingHotel.setName(safeDto.getName());
    existingHotel.setAddress(safeDto.getAddress());
    existingHotel.setStars(safeDto.getStars());
    existingHotel = hotelRepository.save(existingHotel);
    LOG.info("Hotel updated id={}", existingHotel.getId());
    return hotelMapper.toResponseDto(existingHotel);
  }

  @Transactional
  public void deleteHotel(Long id) {
    Long safeId = Optional.ofNullable(id)
        .orElseThrow(() -> new IllegalArgumentException("ID cannot be null"));
    LOG.info("Deleting hotel id={}", safeId);
    Hotel hotel = hotelRepository.findById(safeId)
        .orElseThrow(() -> new NoSuchElementException(HOTEL_NOT_FOUND_MSG + safeId));
    tourRepository.removeHotelFromAllTours(safeId);
    hotelRepository.delete(hotel);
    LOG.info("Hotel deleted id={}", safeId);
  }
}