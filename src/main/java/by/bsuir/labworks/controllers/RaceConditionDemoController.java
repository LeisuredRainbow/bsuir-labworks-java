package by.bsuir.labworks.controllers;

import by.bsuir.labworks.dto.RaceConditionDemoDto;
import by.bsuir.labworks.service.RaceConditionDemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/concurrency")
@Tag(name = "Concurrency", description = "Race condition and synchronization demo")
public class RaceConditionDemoController {

  private final RaceConditionDemoService raceConditionDemoService;

  public RaceConditionDemoController(RaceConditionDemoService raceConditionDemoService) {
    this.raceConditionDemoService = raceConditionDemoService;
  }

  @GetMapping("/race-demo")
  @Operation(summary = "Run race condition demo with 50+ threads")
  public ResponseEntity<RaceConditionDemoDto> runRaceDemo(
      @Parameter(description = "Thread count, must be >= 50", example = "64")
      @RequestParam(defaultValue = "64") int threads,
      @Parameter(description = "Increments per thread", example = "10000")
      @RequestParam(defaultValue = "10000") int incrementsPerThread) {
    return ResponseEntity.ok(raceConditionDemoService.runDemo(threads, incrementsPerThread));
  }
}