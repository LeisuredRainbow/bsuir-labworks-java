package by.bsuir.labworks.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import by.bsuir.labworks.dto.GuideRequestDto;
import by.bsuir.labworks.dto.GuideResponseDto;
import by.bsuir.labworks.service.GuideService;
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
class GuideControllerTest {

  private MockMvc mockMvc;

  @Mock
  private GuideService guideService;

  @InjectMocks
  private GuideController controller;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  void getAllGuidesShouldReturnList() throws Exception {
    when(guideService.getAllGuides()).thenReturn(List.of(new GuideResponseDto()));
    mockMvc.perform(get("/api/guides"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  void getGuideByIdShouldReturnGuide() throws Exception {
    GuideResponseDto dto = new GuideResponseDto();
    dto.setId(1L);
    when(guideService.getGuideById(1L)).thenReturn(dto);
    mockMvc.perform(get("/api/guides/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  void createGuideShouldReturnCreated() throws Exception {
    GuideRequestDto request = new GuideRequestDto();
    request.setFirstName("Anna");
    request.setLastName("Smith");
    request.setEmail("anna@example.com");
    GuideResponseDto response = new GuideResponseDto();
    response.setId(3L);
    when(guideService.createGuide(any())).thenReturn(response);

    mockMvc.perform(post("/api/guides")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(3));
  }

  @Test
  void updateGuideShouldReturnUpdated() throws Exception {
    GuideRequestDto request = new GuideRequestDto();
    request.setFirstName("Updated");
    request.setLastName("Guide");
    request.setEmail("updated@example.com");
    GuideResponseDto response = new GuideResponseDto();
    response.setId(4L);
    when(guideService.updateGuide(any(), any())).thenReturn(response);

    mockMvc.perform(put("/api/guides/4")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(4));
  }

  @Test
  void deleteGuideShouldReturnNoContent() throws Exception {
    mockMvc.perform(delete("/api/guides/5"))
        .andExpect(status().isNoContent());
  }
}