package by.bsuir.labworks.guide.controller;

import by.bsuir.labworks.guide.dto.GuideRequestDto;
import by.bsuir.labworks.guide.dto.GuideResponseDto;
import by.bsuir.labworks.guide.service.GuideService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guides")
@RequiredArgsConstructor
public class GuideController {
  private final GuideService guideService;

  @GetMapping
  public List<GuideResponseDto> getAllGuides() {
    return guideService.getAllGuides();
  }

  @GetMapping("/{id}")
  public GuideResponseDto getGuideById(@PathVariable Long id) {
    return guideService.getGuideById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public GuideResponseDto createGuide(@RequestBody @Valid GuideRequestDto guideDto) {
    return guideService.createGuide(guideDto);
  }

  @PutMapping("/{id}")
  public GuideResponseDto updateGuide(@PathVariable Long id,
                                      @RequestBody @Valid GuideRequestDto guideDto) {
    return guideService.updateGuide(id, guideDto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteGuide(@PathVariable Long id) {
    guideService.deleteGuide(id);
  }
}