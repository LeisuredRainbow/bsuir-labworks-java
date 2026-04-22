package by.bsuir.labworks.cache;

import java.util.Objects;

public class BookingSearchKey {
  private final String lastName;
  private final int page;
  private final int size;
  private final String sort;

  public BookingSearchKey(String lastName, int page, int size, String sort) {
    this.lastName = lastName;
    this.page = page;
    this.size = size;
    this.sort = sort;
  }

  public String getLastName() {
    return lastName;
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
    BookingSearchKey that = (BookingSearchKey) obj;
    return page == that.page
        && size == that.size
        && Objects.equals(lastName, that.lastName)
        && Objects.equals(sort, that.sort);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lastName, page, size, sort);
  }
}