package juste.chambrepro.config;

import juste.chambrepro.service.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Initialise MinIO au démarrage de l'application
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MinioInitializer implements CommandLineRunner {

    private final MinioService minioService;

    @Override
    public void run(String... args) {
        log.info("Vérification de la configuration MinIO...");
        // L'initialisation est déjà faite dans MinioService @PostConstruct
        log.info("MinIO prêt à l'emploi");
    }
}