package by.bsuir.labworks.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Travel Agency API",
        version = "v1",
        description = "API for managing tours, clients, bookings, hotels and guides",
        contact = @Contact(name = "Travel Agency Team"),
        license = @License(name = "Internal License")
    )
)
public class OpenApiConfig {

  @Bean
  public OperationCustomizer customizePageable() {
    return (operation, handlerMethod) -> {
      if (handlerMethod.getMethod().getReturnType() == Page.class) {
        operation.getParameters().stream()
            .filter(p -> p.getName().equals("page") || p.getName().equals("size"))
            .forEach(p -> {
              p.setExample(null);
              p.setSchema(new Schema<>().type("integer").format("int32"));
            });
        operation.getParameters().stream()
            .filter(p -> p.getName().equals("sort"))
            .forEach(p -> {
              p.setExample(null);
              p.setSchema(new Schema<>().type("string"));
            });
      }
      return operation;
    };
  }
}