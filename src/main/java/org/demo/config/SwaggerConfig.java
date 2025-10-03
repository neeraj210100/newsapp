package org.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:" + serverPort);
        server.setDescription("Development server");

        Contact contact = new Contact();
        contact.setEmail("admin@newsapp.com");
        contact.setName("News App Team");
        contact.setUrl("https://www.newsapp.com");

        License license = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("News Application API")
                .version("1.0.0")
                .contact(contact)
                .description("This API provides endpoints for managing news articles, including fetching external news, searching, categorization, and translation services.")
                .termsOfService("https://www.newsapp.com/terms")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}

