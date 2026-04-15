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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HotelService {

  private static final String HOTEL_NOT_FOUND_MSG = "Hotel not found with id: ";

  private final HotelRepository hotelRepository;
  private final HotelMapper hotelMapper;
  private final TourRepository tourRepository;

  public List<HotelResponseDto> getAllHotels() {
    return hotelRepository.findAll().stream()
        .map(hotelMapper::toResponseDto)
        .toList();
  }

  public HotelResponseDto getHotelById(Long id) {
    Hotel hotel = hotelRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException(HOTEL_NOT_FOUND_MSG + id));
    return hotelMapper.toResponseDto(hotel);
  }

  public HotelResponseDto getHotelByAddress(String address) {
    Hotel hotel = hotelRepository.findByAddress(address)
        .orElseThrow(() -> new NoSuchElementException("Hotel not found with address: " + address));
    return hotelMapper.toResponseDto(hotel);
  }

  public HotelResponseDto createHotel(HotelRequestDto hotelDto) {
    if (hotelDto.getAddress() != null
        && hotelRepository.findByAddress(hotelDto.getAddress()).isPresent()) {
      throw new IllegalArgumentException("Hotel at address "
          + hotelDto.getAddress() + " already exists");
    }
    Hotel hotel = hotelMapper.toEntity(hotelDto);
    hotel = hotelRepository.save(hotel);
    return hotelMapper.toResponseDto(hotel);
  }

  public HotelResponseDto updateHotel(Long id, HotelRequestDto hotelDto) {
    Hotel existingHotel = hotelRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException(HOTEL_NOT_FOUND_MSG + id));
    if (hotelDto.getAddress() != null && !hotelDto.getAddress().equals(existingHotel.getAddress())
        && hotelRepository.findByAddress(hotelDto.getAddress()).isPresent()) {
      throw new IllegalArgumentException("Hotel at address "
          + hotelDto.getAddress() + " already exists");
    }
    existingHotel.setName(hotelDto.getName());
    existingHotel.setAddress(hotelDto.getAddress());
    existingHotel.setStars(hotelDto.getStars());
    existingHotel = hotelRepository.save(existingHotel);
    return hotelMapper.toResponseDto(existingHotel);
  }

  @Transactional
  public void deleteHotel(Long id) {
    Hotel hotel = hotelRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException(HOTEL_NOT_FOUND_MSG + id));
    tourRepository.removeHotelFromAllTours(id);
    hotelRepository.delete(hotel);
  }
}