package by.bsuir.labworks.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import by.bsuir.labworks.dto.TourRequestDto;
import by.bsuir.labworks.dto.TourResponseDto;
import by.bsuir.labworks.service.TourService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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
class TourControllerTest {

  private MockMvc mockMvc;

  @Mock
  private TourService tourService;

  @InjectMocks
  private TourController controller;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  void getAllToursShouldReturnList() throws Exception {
    when(tourService.getAllTours()).thenReturn(List.of(new TourResponseDto()));
    mockMvc.perform(get("/api/tours"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  void getToursByCountryShouldReturnList() throws Exception {
    when(tourService.getToursByCountry("France")).thenReturn(List.of(new TourResponseDto()));
    mockMvc.perform(get("/api/tours/country?country=France"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  void getTourByIdShouldReturnTour() throws Exception {
    TourResponseDto dto = new TourResponseDto();
    dto.setId(1L);
    when(tourService.getTourById(1L)).thenReturn(dto);
    mockMvc.perform(get("/api/tours/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  void createTourShouldReturnCreated() throws Exception {
    TourRequestDto request = new TourRequestDto();
    request.setName("Paris Tour");
    request.setCountry("France");
    request.setPrice(BigDecimal.valueOf(100));
    TourResponseDto response = new TourResponseDto();
    response.setId(10L);
    when(tourService.createTour(any())).thenReturn(response);

    mockMvc.perform(post("/api/tours")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(10));
  }

  @Test
  void updateTourShouldReturnUpdated() throws Exception {
    TourRequestDto request = new TourRequestDto();
    request.setName("Updated Tour");
    request.setCountry("Italy");
    request.setPrice(BigDecimal.valueOf(200));
    TourResponseDto response = new TourResponseDto();
    response.setId(2L);
    when(tourService.updateTour(any(), any())).thenReturn(response);

    mockMvc.perform(put("/api/tours/2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(2));
  }

  @Test
  void getToursByPriceShouldReturnList() throws Exception {
    when(tourService.getToursByPrice(BigDecimal.valueOf(100)))
        .thenReturn(List.of(new TourResponseDto()));
    mockMvc.perform(get("/api/tours/price?price=100"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  void getToursByMinPriceShouldReturnList() throws Exception {
    when(tourService.getToursByMinPrice(BigDecimal.valueOf(50)))
        .thenReturn(List.of(new TourResponseDto()));
    mockMvc.perform(get("/api/tours/price/min?minPrice=50"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  void getToursByMaxPriceShouldReturnList() throws Exception {
    when(tourService.getToursByMaxPrice(BigDecimal.valueOf(200)))
        .thenReturn(List.of(new TourResponseDto()));
    mockMvc.perform(get("/api/tours/price/max?maxPrice=200"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  void deleteTourShouldReturnNoContent() throws Exception {
    mockMvc.perform(delete("/api/tours/3"))
        .andExpect(status().isNoContent());
  }
}