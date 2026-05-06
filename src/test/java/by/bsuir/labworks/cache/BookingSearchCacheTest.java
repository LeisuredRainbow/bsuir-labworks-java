package by.bsuir.labworks.cache;

import static org.assertj.core.api.Assertions.assertThat;

import by.bsuir.labworks.dto.BookingResponseDto;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;

class BookingSearchCacheTest {

  private BookingSearchCache cache;

  @BeforeEach
  void setUp() {
    cache = new BookingSearchCache();
  }

  @Test
  void putAndGetShouldReturnSameValue() {
    BookingSearchKey key = new BookingSearchKey("Ivanov", 0, 5, "id: ASC");
    PageImpl<BookingResponseDto> page =
        new PageImpl<>(List.of(new BookingResponseDto()));
    cache.put(key, page);
    assertThat(cache.get(key)).isSameAs(page);
  }

  @Test
  void getForMissingKeyShouldReturnNull() {
    assertThat(cache.get(new BookingSearchKey("x", 0, 5, ""))).isNull();
  }

  @Test
  void invalidateAllShouldClearCache() {
    BookingSearchKey key = new BookingSearchKey("Ivanov", 0, 5, "id: ASC");
    cache.put(key, new PageImpl<>(List.of(new BookingResponseDto())));
    cache.invalidateAll();
    assertThat(cache.get(key)).isNull();
  }
}