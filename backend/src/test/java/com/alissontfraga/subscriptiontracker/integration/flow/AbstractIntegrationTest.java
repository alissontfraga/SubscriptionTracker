package com.alissontfraga.subscriptiontracker.integration.flow;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;

@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    static EmbeddedPostgres postgres;

   @BeforeAll
static void startDb() throws IOException {
    postgres = EmbeddedPostgres.start();

    System.setProperty(
        "spring.datasource.url",
        postgres.getJdbcUrl("postgres", "postgres")
    );
    System.setProperty("spring.datasource.username", "postgres");
    System.setProperty("spring.datasource.password", "postgres");
}

    @AfterAll
    static void stopDb() throws IOException {
        postgres.close();
    }
}
