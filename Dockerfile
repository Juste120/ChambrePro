# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copier les fichiers Gradle wrapper
COPY gradlew .
COPY gradle gradle

# Rendre gradlew exécutable et configurer les timeouts
RUN chmod +x gradlew && \
    mkdir -p ~/.gradle && \
    echo "org.gradle.daemon=false" >> ~/.gradle/gradle.properties && \
    echo "org.gradle.parallel=true" >> ~/.gradle/gradle.properties && \
    echo "systemProp.http.socketTimeout=120000" >> ~/.gradle/gradle.properties && \
    echo "systemProp.http.connectionTimeout=120000" >> ~/.gradle/gradle.properties

# Copier les fichiers de configuration
COPY build.gradle settings.gradle ./

# Télécharger les dépendances (avec retry)
RUN ./gradlew dependencies --no-daemon --refresh-dependencies || \
    ./gradlew dependencies --no-daemon --refresh-dependencies || \
    ./gradlew dependencies --no-daemon

# Copier le code source
COPY src ./src

# Build de l'application
RUN ./gradlew clean build -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Installation de curl pour healthcheck
RUN apk add --no-cache curl

# Création d'un utilisateur non-root pour sécurité
RUN addgroup -S spring && adduser -S spring -G spring

# Copie du JAR depuis le stage de build
COPY --from=build /app/build/libs/*.jar app.jar

# Permissions
RUN chown spring:spring app.jar

# Création du dossier logs
RUN mkdir -p /app/logs && chown spring:spring /app/logs

# Changement vers l'utilisateur non-root
USER spring:spring

# Exposition du port
EXPOSE 8080

# Point d'entrée avec optimisations JVM
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+UseG1GC", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]