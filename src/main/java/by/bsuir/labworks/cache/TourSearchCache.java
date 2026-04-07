package by.bsuir.labworks.cache;

import by.bsuir.labworks.dto.TourResponseDto;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class TourSearchCache {
  private final Map<TourSearchKey, Page<TourResponseDto>> cache = new HashMap<>();

  public Page<TourResponseDto> get(TourSearchKey key) {
    synchronized (cache) {
      return cache.get(key);
    }
  }

  public void put(TourSearchKey key, Page<TourResponseDto> value) {
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