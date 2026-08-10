package ph.edu.dlsu.lbycpob.memorymatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// UNDERSTAND: The Spring Boot entry point for the backend half of the app — starting it boots up the
// database connection, repositories, and any @Service-annotated beans like LeaderboardServiceImpl.
// DECISION: @SpringBootApplication was used instead of manually configuring each Spring component,
// because it auto-detects and wires up everything under this package (entities, repositories, services)
// with sensible defaults.
@SpringBootApplication
public class MemoryShowdownApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemoryShowdownApplication.class, args);
    }
}