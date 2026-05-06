package by.bsuir.labworks.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import by.bsuir.labworks.dto.HotelRequestDto;
import by.bsuir.labworks.dto.HotelResponseDto;
import by.bsuir.labworks.service.HotelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class HotelControllerTest {

  private MockMvc mockMvc;

  @Mock
  private HotelService hotelService;

  @InjectMocks
  private HotelController controller;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  void getAllHotelsShouldReturnList() throws Exception {
    when(hotelService.getAllHotels()).thenReturn(List.of(new HotelResponseDto()));
    mockMvc.perform(get("/api/hotels"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  void getHotelByIdShouldReturnHotel() throws Exception {
    HotelResponseDto dto = new HotelResponseDto();
    dto.setId(1L);
    when(hotelService.getHotelById(1L)).thenReturn(dto);
    mockMvc.perform(get("/api/hotels/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  void getHotelByAddressShouldReturnHotel() throws Exception {
    HotelResponseDto dto = new HotelResponseDto();
    dto.setAddress("Minsk");
    when(hotelService.getHotelByAddress("Minsk")).thenReturn(dto);
    mockMvc.perform(get("/api/hotels/by-address?address=Minsk"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.address").value("Minsk"));
  }

  @Test
  void createHotelShouldReturnCreated() throws Exception {
    HotelRequestDto request = new HotelRequestDto();
    request.setName("Hotel Minsk");
    HotelResponseDto response = new HotelResponseDto();
    response.setId(2L);
    when(hotelService.createHotel(any())).thenReturn(response);

    mockMvc.perform(post("/api/hotels")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(2));
  }

  @Test
  void updateHotelShouldReturnUpdated() throws Exception {
    HotelRequestDto request = new HotelRequestDto();
    request.setName("Updated Hotel");
    HotelResponseDto response = new HotelResponseDto();
    response.setId(3L);
    when(hotelService.updateHotel(any(), any())).thenReturn(response);

    mockMvc.perform(put("/api/hotels/3")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(3));
  }

  @Test
  void deleteHotelShouldReturnNoContent() throws Exception {
    mockMvc.perform(delete("/api/hotels/4"))
        .andExpect(status().isNoContent());
  }
}