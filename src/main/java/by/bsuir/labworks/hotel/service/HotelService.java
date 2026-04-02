package by.bsuir.labworks.hotel.service;

import by.bsuir.labworks.hotel.dto.HotelRequestDto;
import by.bsuir.labworks.hotel.dto.HotelResponseDto;
import by.bsuir.labworks.hotel.entity.Hotel;
import by.bsuir.labworks.hotel.mapper.HotelMapper;
import by.bsuir.labworks.hotel.repository.HotelRepository;
import by.bsuir.labworks.tour.repository.TourRepository;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HotelService {
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
        .orElseThrow(() -> new NoSuchElementException("Hotel not found with id: " + id));
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
        .orElseThrow(() -> new NoSuchElementException("Hotel not found with id: " + id));
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

  public void deleteHotel(Long id) {
    if (!hotelRepository.existsById(id)) {
      throw new NoSuchElementException("Hotel not found with id: " + id);
    }
    tourRepository.removeHotelFromAllTours(id);
    hotelRepository.deleteById(id);
  }
}