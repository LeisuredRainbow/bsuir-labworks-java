package by.bsuir.labworks.hotel.controller;

import by.bsuir.labworks.hotel.dto.HotelRequestDto;
import by.bsuir.labworks.hotel.dto.HotelResponseDto;
import by.bsuir.labworks.hotel.service.HotelService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {
  private final HotelService hotelService;

  @GetMapping
  public List<HotelResponseDto> getAllHotels() {
    return hotelService.getAllHotels();
  }

  @GetMapping("/{id}")
  public HotelResponseDto getHotelById(@PathVariable Long id) {
    return hotelService.getHotelById(id);
  }

  @GetMapping("/by-city")
  public List<HotelResponseDto> getHotelsByCity(@RequestParam String city) {
    return hotelService.getHotelsByCity(city);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public HotelResponseDto createHotel(@RequestBody @Valid HotelRequestDto hotelDto) {
    return hotelService.createHotel(hotelDto);
  }

  @PutMapping("/{id}")
  public HotelResponseDto updateHotel(@PathVariable Long id,
                                      @RequestBody @Valid HotelRequestDto hotelDto) {
    return hotelService.updateHotel(id, hotelDto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteHotel(@PathVariable Long id) {
    hotelService.deleteHotel(id);
  }
}