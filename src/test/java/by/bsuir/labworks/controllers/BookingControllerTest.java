package by.bsuir.labworks.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import by.bsuir.labworks.dto.BookingResponseDto;
import by.bsuir.labworks.service.BookingService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

  private MockMvc mockMvc;

  @Mock
  private BookingService bookingService;

  @InjectMocks
  private BookingController controller;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  void getAllBookingsShouldReturnOk() throws Exception {
    when(bookingService.getAllBookings()).thenReturn(List.of(new BookingResponseDto()));
    mockMvc.perform(get("/api/bookings")).andExpect(status().isOk());
  }

  @Test
  void getBookingByIdShouldReturnOk() throws Exception {
    when(bookingService.getBookingById(1L)).thenReturn(new BookingResponseDto());
    mockMvc.perform(get("/api/bookings/1")).andExpect(status().isOk());
  }
}