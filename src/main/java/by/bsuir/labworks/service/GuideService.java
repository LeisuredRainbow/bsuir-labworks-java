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
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuideService {

  private static final Logger LOG = LoggerFactory.getLogger(GuideService.class);
  private static final String GUIDE_NOT_FOUND_MSG = "Guide not found with id: ";
  private static final String ID_CANNOT_BE_NULL = "ID cannot be null";
  private static final String GUIDE_DATA_CANNOT_BE_NULL = "Guide data cannot be null";

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
    Long safeId = Optional.ofNullable(id)
        .orElseThrow(() -> new IllegalArgumentException(ID_CANNOT_BE_NULL));
    LOG.debug("Fetching guide by id={}", safeId);
    Guide guide = guideRepository.findById(safeId)
        .orElseThrow(() -> new NoSuchElementException(GUIDE_NOT_FOUND_MSG + safeId));
    return guideMapper.toResponseDto(guide);
  }

  public GuideResponseDto createGuide(GuideRequestDto guideDto) {
    GuideRequestDto safeDto = Optional.ofNullable(guideDto)
        .orElseThrow(() -> new IllegalArgumentException(GUIDE_DATA_CANNOT_BE_NULL));
    LOG.info("Creating new guide");
    if (safeDto.getEmail() != null
        && guideRepository.findByEmail(safeDto.getEmail()).isPresent()) {
      throw new IllegalArgumentException(
          "Guide with email " + safeDto.getEmail() + " already exists");
    }
    if (safeDto.getPhone() != null) {
      if (guideRepository.findByPhone(safeDto.getPhone()).isPresent()) {
        throw new IllegalArgumentException(
            "Guide with phone " + safeDto.getPhone() + " already exists");
      }
      if (clientRepository.findByPhone(safeDto.getPhone()).isPresent()) {
        throw new IllegalArgumentException(
            "Phone " + safeDto.getPhone() + " is already used by a client");
      }
    }
    Guide guide = guideMapper.toEntity(safeDto);
    guide = guideRepository.save(guide);
    LOG.info("Guide created with id={}", guide.getId());
    return guideMapper.toResponseDto(guide);
  }

  public GuideResponseDto updateGuide(Long id, GuideRequestDto guideDto) {
    Long safeId = Optional.ofNullable(id)
        .orElseThrow(() -> new IllegalArgumentException(ID_CANNOT_BE_NULL));
    GuideRequestDto safeDto = Optional.ofNullable(guideDto)
        .orElseThrow(() -> new IllegalArgumentException(GUIDE_DATA_CANNOT_BE_NULL));
    LOG.info("Updating guide id={}", safeId);
    Guide existingGuide = guideRepository.findById(safeId)
        .orElseThrow(() -> new NoSuchElementException(GUIDE_NOT_FOUND_MSG + safeId));
    if (safeDto.getEmail() != null
        && !safeDto.getEmail().equals(existingGuide.getEmail())
        && guideRepository.findByEmail(safeDto.getEmail()).isPresent()) {
      throw new IllegalArgumentException(
          "Guide with email " + safeDto.getEmail() + " already exists");
    }
    if (safeDto.getPhone() != null
        && !safeDto.getPhone().equals(existingGuide.getPhone())) {
      if (guideRepository.findByPhone(safeDto.getPhone()).isPresent()) {
        throw new IllegalArgumentException(
            "Guide with phone " + safeDto.getPhone() + " already exists");
      }
      if (clientRepository.findByPhone(safeDto.getPhone()).isPresent()) {
        throw new IllegalArgumentException(
            "Phone " + safeDto.getPhone() + " is already used by a client");
      }
    }
    existingGuide.setFirstName(safeDto.getFirstName());
    existingGuide.setLastName(safeDto.getLastName());
    existingGuide.setPhone(safeDto.getPhone());
    existingGuide.setEmail(safeDto.getEmail());
    existingGuide.setExperienceYears(safeDto.getExperienceYears());
    existingGuide = guideRepository.save(existingGuide);
    LOG.info("Guide updated id={}", existingGuide.getId());
    return guideMapper.toResponseDto(existingGuide);
  }

  @Transactional
  public void deleteGuide(Long id) {
    Long safeId = Optional.ofNullable(id)
        .orElseThrow(() -> new IllegalArgumentException(ID_CANNOT_BE_NULL));
    LOG.info("Deleting guide id={}", safeId);
    Guide guide = guideRepository.findById(safeId)
        .orElseThrow(() -> new NoSuchElementException(GUIDE_NOT_FOUND_MSG + safeId));
    tourRepository.removeGuideFromAllTours(safeId);
    guideRepository.delete(guide);
    LOG.info("Guide deleted id={}", safeId);
  }
}