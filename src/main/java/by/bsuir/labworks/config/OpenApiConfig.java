package by.bsuir.labworks.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

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
}