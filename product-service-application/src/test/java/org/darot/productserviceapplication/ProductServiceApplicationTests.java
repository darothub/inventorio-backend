package org.darot.productserviceapplication;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ProductServiceApplicationTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("product_db")
            .withUsername("admin")
            .withPassword("admin");

    // ⚙️ Dynamically override Spring Boot datasource properties
    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {

        final String finalJdbcUrl = "true".equals(System.getenv("TESTCONTAINERS_CI_MODE"))
                ? postgres.getJdbcUrl().replace("localhost", "testcontainers")
                : postgres.getJdbcUrl();

        registry.add("spring.datasource.url", () -> finalJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

    }
    @BeforeAll
    static void setup() {
        if (!DockerClientFactory.instance().isDockerAvailable()) {
            throw new IllegalStateException("Docker is required for these tests");
        }
    }
    @Test
    public void contextLoads() {
        assertTrue(postgres.isRunning());
    }

}
