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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuideService {

  private static final Logger LOG = LoggerFactory.getLogger(GuideService.class);
  private static final String GUIDE_NOT_FOUND_MSG = "Guide not found with id: ";

  private final GuideRepository guideRepository;
  private final GuideMapper guideMapper;
  private final TourRepository tourRepository;
  private final ClientRepository clientRepository;

  public List<GuideResponseDto> getAllGuides() {
    LOG.debug("Fetching all guides");
    return guideRepository.findAll().stream()
        .map(guideMapper::toResponseDto)
        .toList();
  }

  public GuideResponseDto getGuideById(Long id) {
    LOG.debug("Fetching guide by id={}", id);
    Guide guide = guideRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException(GUIDE_NOT_FOUND_MSG + id));
    return guideMapper.toResponseDto(guide);
  }

  public GuideResponseDto createGuide(GuideRequestDto guideDto) {
    LOG.info("Creating new guide");
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
            + guideDto.getPhone() + " is already used by a client");
      }
    }
    Guide guide = guideMapper.toEntity(guideDto);
    guide = guideRepository.save(guide);
    LOG.info("Guide created with id={}", guide.getId());
    return guideMapper.toResponseDto(guide);
  }

  public GuideResponseDto updateGuide(Long id, GuideRequestDto guideDto) {
    LOG.info("Updating guide id={}", id);
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
            + guideDto.getPhone() + " is already used by a client");
      }
    }
    existingGuide.setFirstName(guideDto.getFirstName());
    existingGuide.setLastName(guideDto.getLastName());
    existingGuide.setPhone(guideDto.getPhone());
    existingGuide.setEmail(guideDto.getEmail());
    existingGuide.setExperienceYears(guideDto.getExperienceYears());
    existingGuide = guideRepository.save(existingGuide);
    LOG.info("Guide updated id={}", existingGuide.getId());
    return guideMapper.toResponseDto(existingGuide);
  }

  @Transactional
  public void deleteGuide(Long id) {
    LOG.info("Deleting guide id={}", id);
    Guide guide = guideRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException(GUIDE_NOT_FOUND_MSG + id));
    tourRepository.removeGuideFromAllTours(id);
    guideRepository.delete(guide);
    LOG.info("Guide deleted id={}", id);
  }
}