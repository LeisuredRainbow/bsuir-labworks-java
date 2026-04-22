package by.bsuir.labworks.cache;

import by.bsuir.labworks.dto.BookingResponseDto;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class BookingSearchCache {
  private final Map<BookingSearchKey, Page<BookingResponseDto>> cache = new HashMap<>();

  public Page<BookingResponseDto> get(BookingSearchKey key) {
    synchronized (cache) {
      return cache.get(key);
    }
  }

  public void put(BookingSearchKey key, Page<BookingResponseDto> value) {
    synchronized (cache) {
      cache.put(key, value);
    }
  }

  public void invalidateAll() {
    synchronized (cache) {
      cache.clear();
    }
  }
}