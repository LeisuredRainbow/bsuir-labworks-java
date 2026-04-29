package by.bsuir.labworks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.bsuir.labworks.dto.GuideRequestDto;
import by.bsuir.labworks.dto.GuideResponseDto;
import by.bsuir.labworks.entity.Guide;
import by.bsuir.labworks.mapper.GuideMapper;
import by.bsuir.labworks.repository.ClientRepository;
import by.bsuir.labworks.repository.GuideRepository;
import by.bsuir.labworks.repository.TourRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GuideServiceTest {

  @Mock
  private GuideRepository guideRepository;

  @Mock
  private GuideMapper guideMapper;

  @Mock
  private TourRepository tourRepository;

  @Mock
  private ClientRepository clientRepository;

  private GuideService guideService;

  @BeforeEach
  void setUp() {
    guideService = new GuideService(guideRepository, guideMapper, tourRepository, clientRepository);
  }

  @Test
  void getAllGuidesMapsEntities() {
    Guide guide = new Guide();
    GuideResponseDto response = new GuideResponseDto();
    when(guideRepository.findAll()).thenReturn(List.of(guide));
    when(guideMapper.toResponseDto(guide)).thenReturn(response);

    List<GuideResponseDto> result = guideService.getAllGuides();

    assertThat(result).containsExactly(response);
  }

  @Test
  void getGuideByIdThrowsWhenMissing() {
    when(guideRepository.findById(1L)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> guideService.getGuideById(1L))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Guide not found");
  }

  @Test
  void getGuideByIdMapsEntity() {
    Guide guide = new Guide();
    GuideResponseDto response = new GuideResponseDto();
    when(guideRepository.findById(1L)).thenReturn(java.util.Optional.of(guide));
    when(guideMapper.toResponseDto(guide)).thenReturn(response);

    GuideResponseDto result = guideService.getGuideById(1L);

    assertThat(result).isSameAs(response);
  }

  @Test
  void createGuideRejectsDuplicateEmail() {
    GuideRequestDto dto = new GuideRequestDto();
    dto.setEmail("a@b.com");

    when(guideRepository.findByEmail("a@b.com"))
        .thenReturn(java.util.Optional.of(new Guide()));

    assertThatThrownBy(() -> guideService.createGuide(dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("already exists");
  }

  @Test
  void createGuideRejectsPhoneUsedByGuide() {
    GuideRequestDto dto = new GuideRequestDto();
    dto.setPhone("+111111111");

    when(guideRepository.findByPhone("+111111111"))
        .thenReturn(java.util.Optional.of(new Guide()));

    assertThatThrownBy(() -> guideService.createGuide(dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("already exists");
  }

  @Test
  void createGuideRejectsPhoneUsedByClient() {
    GuideRequestDto dto = new GuideRequestDto();
    dto.setPhone("+111111111");

    when(guideRepository.findByPhone("+111111111"))
        .thenReturn(java.util.Optional.empty());
    when(clientRepository.findByPhone("+111111111"))
        .thenReturn(java.util.Optional.of(new by.bsuir.labworks.entity.Client()));

    assertThatThrownBy(() -> guideService.createGuide(dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("already used by a client");
  }

  @Test
  void createGuideSavesAndMaps() {
    GuideRequestDto dto = new GuideRequestDto();
    dto.setEmail("a@b.com");
    dto.setPhone("+111111111");

    when(guideRepository.findByEmail("a@b.com")).thenReturn(java.util.Optional.empty());
    when(guideRepository.findByPhone("+111111111")).thenReturn(java.util.Optional.empty());
    when(clientRepository.findByPhone("+111111111")).thenReturn(java.util.Optional.empty());

    Guide entity = new Guide();
    Guide saved = new Guide();
    saved.setId(3L);
    GuideResponseDto response = new GuideResponseDto();
    when(guideMapper.toEntity(dto)).thenReturn(entity);
    when(guideRepository.save(entity)).thenReturn(saved);
    when(guideMapper.toResponseDto(saved)).thenReturn(response);

    GuideResponseDto result = guideService.createGuide(dto);

    assertThat(result).isSameAs(response);
  }

  @Test
  void updateGuideRejectsMissing() {
    when(guideRepository.findById(2L)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> guideService.updateGuide(2L, new GuideRequestDto()))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Guide not found");
  }

  @Test
  void updateGuideRejectsDuplicateEmail() {
    Guide existing = new Guide();
    existing.setEmail("old@b.com");
    when(guideRepository.findById(2L)).thenReturn(java.util.Optional.of(existing));

    GuideRequestDto dto = new GuideRequestDto();
    dto.setEmail("new@b.com");

    when(guideRepository.findByEmail("new@b.com"))
        .thenReturn(java.util.Optional.of(new Guide()));

    assertThatThrownBy(() -> guideService.updateGuide(2L, dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("already exists");
  }

  @Test
  void updateGuideRejectsPhoneUsedByGuide() {
    Guide existing = new Guide();
    existing.setPhone("+111111111");
    when(guideRepository.findById(2L)).thenReturn(java.util.Optional.of(existing));

    GuideRequestDto dto = new GuideRequestDto();
    dto.setPhone("+222222222");

    when(guideRepository.findByPhone("+222222222"))
        .thenReturn(java.util.Optional.of(new Guide()));

    assertThatThrownBy(() -> guideService.updateGuide(2L, dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("already exists");
  }

  @Test
  void updateGuideRejectsPhoneUsedByClient() {
    Guide existing = new Guide();
    existing.setPhone("+111111111");
    when(guideRepository.findById(2L)).thenReturn(java.util.Optional.of(existing));

    GuideRequestDto dto = new GuideRequestDto();
    dto.setPhone("+222222222");

    when(guideRepository.findByPhone("+222222222"))
        .thenReturn(java.util.Optional.empty());
    when(clientRepository.findByPhone("+222222222"))
        .thenReturn(java.util.Optional.of(new by.bsuir.labworks.entity.Client()));

    assertThatThrownBy(() -> guideService.updateGuide(2L, dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("already used by a client");
  }

  @Test
  void updateGuideAllowsSamePhoneAndEmail() {
    Guide existing = new Guide();
    existing.setEmail("same@b.com");
    existing.setPhone("+111111111");
    when(guideRepository.findById(2L)).thenReturn(java.util.Optional.of(existing));

    GuideRequestDto dto = new GuideRequestDto();
    dto.setEmail("same@b.com");
    dto.setPhone("+111111111");

    Guide saved = new Guide();
    when(guideRepository.save(existing)).thenReturn(saved);
    when(guideMapper.toResponseDto(saved)).thenReturn(new GuideResponseDto());

    guideService.updateGuide(2L, dto);

    verify(guideRepository, never()).findByEmail(any());
    verify(guideRepository, never()).findByPhone(any());
    verify(clientRepository, never()).findByPhone(any());
  }

  @Test
  void deleteGuideRejectsMissing() {
    when(guideRepository.findById(7L)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> guideService.deleteGuide(7L))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Guide not found");
  }

  @Test
  void deleteGuideRemovesRelationsAndDeletes() {
    Guide guide = new Guide();
    when(guideRepository.findById(7L)).thenReturn(java.util.Optional.of(guide));

    guideService.deleteGuide(7L);

    verify(tourRepository).removeGuideFromAllTours(7L);
    verify(guideRepository).delete(guide);
  }
  @Test
  void createGuideWithNullPhoneSkipsPhoneChecks() {
    GuideRequestDto dto = new GuideRequestDto();
    dto.setEmail("a@b.com");

    when(guideRepository.findByEmail("a@b.com")).thenReturn(java.util.Optional.empty());

    Guide entity = new Guide();
    Guide saved = new Guide();
    GuideResponseDto response = new GuideResponseDto();
    when(guideMapper.toEntity(dto)).thenReturn(entity);
    when(guideRepository.save(entity)).thenReturn(saved);
    when(guideMapper.toResponseDto(saved)).thenReturn(response);

    GuideResponseDto result = guideService.createGuide(dto);

    assertThat(result).isSameAs(response);
    verify(guideRepository, never()).findByPhone(any());
    verify(clientRepository, never()).findByPhone(any());
  }

  @Test
  void updateGuideUpdatesWhenEmailAndPhoneAreUnique() {
    Guide existing = new Guide();
    existing.setEmail("old@b.com");
    existing.setPhone("+111111111");
    when(guideRepository.findById(2L)).thenReturn(java.util.Optional.of(existing));

    GuideRequestDto dto = new GuideRequestDto();
    dto.setEmail("new@b.com");
    dto.setPhone("+333333333");

    when(guideRepository.findByEmail("new@b.com")).thenReturn(java.util.Optional.empty());
    when(guideRepository.findByPhone("+333333333")).thenReturn(java.util.Optional.empty());
    when(clientRepository.findByPhone("+333333333")).thenReturn(java.util.Optional.empty());

    Guide saved = new Guide();
    when(guideRepository.save(existing)).thenReturn(saved);
    when(guideMapper.toResponseDto(saved)).thenReturn(new GuideResponseDto());

    guideService.updateGuide(2L, dto);

    verify(guideRepository).findByEmail("new@b.com");
    verify(guideRepository).findByPhone("+333333333");
    verify(clientRepository).findByPhone("+333333333");
  }

  @Test
  void updateGuideSkipsPhoneChecksWhenPhoneNull() {
    Guide existing = new Guide();
    existing.setEmail("old@b.com");
    existing.setPhone("+111111111");
    when(guideRepository.findById(2L)).thenReturn(java.util.Optional.of(existing));

    GuideRequestDto dto = new GuideRequestDto();
    dto.setEmail("old@b.com");
    dto.setPhone(null);

    Guide saved = new Guide();
    when(guideRepository.save(existing)).thenReturn(saved);
    when(guideMapper.toResponseDto(saved)).thenReturn(new GuideResponseDto());

    guideService.updateGuide(2L, dto);

    verify(guideRepository, never()).findByPhone(any());
    verify(clientRepository, never()).findByPhone(any());
  }

}
