package by.bsuir.labworks.service;

import by.bsuir.labworks.dto.GuideRequestDto;
import by.bsuir.labworks.dto.GuideResponseDto;
import by.bsuir.labworks.entity.Guide;
import by.bsuir.labworks.mapper.GuideMapper;
import by.bsuir.labworks.repository.ClientRepository;
import by.bsuir.labworks.repository.GuideRepository;
import by.bsuir.labworks.repository.TourRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuideService {

  private static final String GUIDE_NOT_FOUND_MSG = "Guide not found with id: ";

  private final GuideRepository guideRepository;
  private final GuideMapper guideMapper;
  private final TourRepository tourRepository;
  private final ClientRepository clientRepository;

  public List<GuideResponseDto> getAllGuides() {
    return guideRepository.findAll().stream()
        .map(guideMapper::toResponseDto)
        .toList();
  }

  public GuideResponseDto getGuideById(Long id) {
    Guide guide = guideRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException(GUIDE_NOT_FOUND_MSG + id));
    return guideMapper.toResponseDto(guide);
  }

  public GuideResponseDto createGuide(GuideRequestDto guideDto) {
    if (guideDto.getEmail() != null
        && guideRepository.findByEmail(guideDto.getEmail()).isPresent()) {
      throw new IllegalArgumentException("Guide with email "
          + guideDto.getEmail() + " already exists");
    }
    if (guideDto.getPhone() != null) {
      if (guideRepository.findByPhone(guideDto.getPhone()).isPresent()) {
        throw new IllegalArgumentException("Guide with phone "
            + guideDto.getPhone() + " already exists");
      }
      if (clientRepository.findByPhone(guideDto.getPhone()).isPresent()) {
        throw new IllegalArgumentException("Phone "
            + guideDto.getPhone() + " already used by a client");
      }
    }
    Guide guide = guideMapper.toEntity(guideDto);
    guide = guideRepository.save(guide);
    return guideMapper.toResponseDto(guide);
  }

  public GuideResponseDto updateGuide(Long id, GuideRequestDto guideDto) {
    Guide existingGuide = guideRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException(GUIDE_NOT_FOUND_MSG + id));
    if (guideDto.getEmail() != null && !guideDto.getEmail().equals(existingGuide.getEmail())
        && guideRepository.findByEmail(guideDto.getEmail()).isPresent()) {
      throw new IllegalArgumentException("Guide with email "
        + guideDto.getEmail() + " already exists");
    }
    if (guideDto.getPhone() != null && !guideDto.getPhone().equals(existingGuide.getPhone())) {
      if (guideRepository.findByPhone(guideDto.getPhone()).isPresent()) {
        throw new IllegalArgumentException("Guide with phone "
            + guideDto.getPhone() + " already exists");
      }
      if (clientRepository.findByPhone(guideDto.getPhone()).isPresent()) {
        throw new IllegalArgumentException("Phone "
            + guideDto.getPhone() + " already used by a client");
      }
    }
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
    Guide guide = guideRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException(GUIDE_NOT_FOUND_MSG + id));
    tourRepository.removeGuideFromAllTours(id);
    guideRepository.delete(guide);
  }
}