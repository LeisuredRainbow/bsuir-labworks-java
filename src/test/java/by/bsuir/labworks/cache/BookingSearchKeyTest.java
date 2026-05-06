package by.bsuir.labworks.cache;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BookingSearchKeyTest {

  @Test
  void sameValuesShouldBeEqual() {
    BookingSearchKey key1 = new BookingSearchKey("Smith", 0, 10, "id: ASC");
    BookingSearchKey key2 = new BookingSearchKey("Smith", 0, 10, "id: ASC");
    assertThat(key1).isEqualTo(key2);
    assertThat(key1.hashCode()).isEqualTo(key2.hashCode());
  }

  @Test
  void differentLastNameShouldNotBeEqual() {
    BookingSearchKey key1 = new BookingSearchKey("Smith", 0, 10, "id: ASC");
    BookingSearchKey key2 = new BookingSearchKey("Jones", 0, 10, "id: ASC");
    assertThat(key1).isNotEqualTo(key2);
  }

  @Test
  void differentPageShouldNotBeEqual() {
    BookingSearchKey key1 = new BookingSearchKey("Smith", 0, 10, "id: ASC");
    BookingSearchKey key2 = new BookingSearchKey("Smith", 1, 10, "id: ASC");
    assertThat(key1).isNotEqualTo(key2);
  }
}