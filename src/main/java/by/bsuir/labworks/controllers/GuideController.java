package by.bsuir.labworks.controllers;

import by.bsuir.labworks.dto.GuideRequestDto;
import by.bsuir.labworks.dto.GuideResponseDto;
import by.bsuir.labworks.service.GuideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guides")
@RequiredArgsConstructor
@Validated
@Tag(name = "Guides", description = "Operations with guides")
public class GuideController {
  private final GuideService guideService;

  @GetMapping
  @Operation(summary = "Get all guides")
  public List<GuideResponseDto> getAllGuides() {
    return guideService.getAllGuides();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get guide by id")
  public GuideResponseDto getGuideById(
      @Parameter(description = "Guide id", example = "1")
      @PathVariable @Positive(message = "ID must be positive") Long id) {
    return guideService.getGuideById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create a new guide")
  public GuideResponseDto createGuide(
      @RequestBody @Valid @Parameter(description = "Guide data") GuideRequestDto guideDto) {
    return guideService.createGuide(guideDto);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update existing guide")
  public GuideResponseDto updateGuide(
      @Parameter(description = "Guide id", example = "1")
      @PathVariable @Positive(message = "ID must be positive") Long id,
      @RequestBody @Valid GuideRequestDto guideDto) {
    return guideService.updateGuide(id, guideDto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete guide")
  public void deleteGuide(
      @Parameter(description = "Guide id", example = "1")
      @PathVariable @Positive(message = "ID must be positive") Long id) {
    guideService.deleteGuide(id);
  }
}