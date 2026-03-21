package by.bsuir.labworks.guide.service;

import by.bsuir.labworks.guide.dto.GuideRequestDto;
import by.bsuir.labworks.guide.dto.GuideResponseDto;
import by.bsuir.labworks.guide.entity.Guide;
import by.bsuir.labworks.guide.mapper.GuideMapper;
import by.bsuir.labworks.guide.repository.GuideRepository;
import by.bsuir.labworks.tour.repository.TourRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuideService {
  private final GuideRepository guideRepository;
  private final GuideMapper guideMapper;
  private final TourRepository tourRepository;

  public List<GuideResponseDto> getAllGuides() {
    return guideRepository.findAll().stream()
        .map(guideMapper::toResponseDto)
        .toList();
  }

  public GuideResponseDto getGuideById(Long id) {
    Guide guide = guideRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Guide not found with id: " + id));
    return guideMapper.toResponseDto(guide);
  }

  public GuideResponseDto createGuide(GuideRequestDto guideDto) {
    Guide guide = guideMapper.toEntity(guideDto);
    guide = guideRepository.save(guide);
    return guideMapper.toResponseDto(guide);
  }

  public GuideResponseDto updateGuide(Long id, GuideRequestDto guideDto) {
    Guide existingGuide = guideRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Guide not found with id: " + id));
    existingGuide.setFirstName(guideDto.getFirstName());
    existingGuide.setLastName(guideDto.getLastName());
    existingGuide.setPhone(guideDto.getPhone());
    existingGuide.setEmail(guideDto.getEmail());
    existingGuide.setExperienceYears(guideDto.getExperienceYears());
    existingGuide = guideRepository.save(existingGuide);
    return guideMapper.toResponseDto(existingGuide);
  }

  @Transactional
  public void deleteGuide(Long id) {
    if (!guideRepository.existsById(id)) {
      throw new NoSuchElementException("Guide not found with id: " + id);
    }
    tourRepository.removeGuideFromAllTours(id); // метод удаления связей
    guideRepository.deleteById(id);
  }
}