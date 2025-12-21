# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copier les fichiers Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Télécharger les dépendances
RUN ./mvnw dependency:go-offline -B

# Copier le code source
COPY src src

# Build de l'application
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Installation de curl pour healthcheck
RUN apk add --no-cache curl

# Création d'un utilisateur non-root pour sécurité
RUN addgroup -S spring && adduser -S spring -G spring

# Copie du JAR depuis le stage de build
COPY --from=build /app/target/*.jar app.jar

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