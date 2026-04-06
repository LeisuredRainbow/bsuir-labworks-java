package by.bsuir.labworks.tour.cache;

import java.util.Objects;

public class TourSearchKey {
  private final String hotelName;
  private final int page;
  private final int size;
  private final String sort;

  public TourSearchKey(String hotelName, int page, int size, String sort) {
    this.hotelName = hotelName;
    this.page = page;
    this.size = size;
    this.sort = sort;
  }

  public String getHotelName() {
    return hotelName;
  }

  public int getPage() {
    return page;
  }

  public int getSize() {
    return size;
  }

  public String getSort() {
    return sort;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    TourSearchKey that = (TourSearchKey) obj;
    return page == that.page
        && size == that.size
        && Objects.equals(hotelName, that.hotelName)
        && Objects.equals(sort, that.sort);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelName, page, size, sort);
  }
}