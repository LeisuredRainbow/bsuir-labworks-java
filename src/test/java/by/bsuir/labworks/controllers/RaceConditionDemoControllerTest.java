package by.bsuir.labworks.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import by.bsuir.labworks.dto.RaceConditionDemoDto;
import by.bsuir.labworks.service.RaceConditionDemoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class RaceConditionDemoControllerTest {

  private MockMvc mockMvc;

  @Mock
  private RaceConditionDemoService raceConditionDemoService;

  @InjectMocks
  private RaceConditionDemoController controller;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  void runRaceDemoShouldReturnOk() throws Exception {
    when(raceConditionDemoService.runDemo(64, 10000))
        .thenReturn(new RaceConditionDemoDto(64, 10000, 640000, 500000, 640000, 640000, true));
    mockMvc.perform(get("/concurrency/race-demo?threads=64&incrementsPerThread=10000"))
        .andExpect(status().isOk());
  }
}