package by.bsuir.labworks.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class RootControllerTest {

  private final RootController controller = new RootController();

  @Test
  void rootShouldReturnOkWithMessage() {
    var response = controller.root();
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Travel Agency API is running.", response.getBody());
  }
}