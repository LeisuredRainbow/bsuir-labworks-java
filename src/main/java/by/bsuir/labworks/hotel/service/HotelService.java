package by.bsuir.labworks.hotel.service;

import by.bsuir.labworks.hotel.dto.HotelRequestDto;
import by.bsuir.labworks.hotel.dto.HotelResponseDto;
import by.bsuir.labworks.hotel.entity.Hotel;
import by.bsuir.labworks.hotel.mapper.HotelMapper;
import by.bsuir.labworks.hotel.repository.HotelRepository;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HotelService {
  private final HotelRepository hotelRepository;
  private final HotelMapper hotelMapper;

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

  public List<HotelResponseDto> getHotelsByCity(String city) {
    return hotelRepository.findByCity(city).stream()
        .map(hotelMapper::toResponseDto)
        .toList();
  }

  public HotelResponseDto createHotel(HotelRequestDto hotelDto) {
    Hotel hotel = hotelMapper.toEntity(hotelDto);
    hotel = hotelRepository.save(hotel);
    return hotelMapper.toResponseDto(hotel);
  }

  public HotelResponseDto updateHotel(Long id, HotelRequestDto hotelDto) {
    Hotel existingHotel = hotelRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Hotel not found with id: " + id));
    existingHotel.setName(hotelDto.getName());
    existingHotel.setCity(hotelDto.getCity());
    existingHotel.setAddress(hotelDto.getAddress());
    existingHotel.setStars(hotelDto.getStars());
    existingHotel = hotelRepository.save(existingHotel);
    return hotelMapper.toResponseDto(existingHotel);
  }

  public void deleteHotel(Long id) {
    if (!hotelRepository.existsById(id)) {
      throw new NoSuchElementException("Hotel not found with id: " + id);
    }
    hotelRepository.deleteById(id);
  }
}