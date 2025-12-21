package juste.chambrepro.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import juste.chambrepro.config.MinioProperties;
import juste.chambrepro.exception.FileStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    /**
     * Initialise le bucket MinIO au démarrage de l'application
     */
    @PostConstruct
    public void init() {
        try {
            createBucketIfNotExists();
            log.info("MinIO initialisé avec succès. Bucket: {}", minioProperties.getBucketName());
        } catch (Exception e) {
            log.error("Erreur lors de l'initialisation de MinIO", e);
            throw new FileStorageException("Impossible d'initialiser MinIO", e);
        }
    }

    /**
     * Crée le bucket s'il n'existe pas
     */
    private void createBucketIfNotExists() throws Exception {
        boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .build()
        );

        if (!bucketExists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .build()
            );
            log.info("Bucket créé: {}", minioProperties.getBucketName());

            // Rendre le bucket public pour les images
            String policy = """
                {
                    "Version": "2012-10-17",
                    "Statement": [
                        {
                            "Effect": "Allow",
                            "Principal": {"AWS": "*"},
                            "Action": ["s3:GetObject"],
                            "Resource": ["arn:aws:s3:::%s/*"]
                        }
                    ]
                }
                """.formatted(minioProperties.getBucketName());

            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .config(policy)
                            .build()
            );
        }
    }

    /**
     * Upload un fichier vers MinIO
     * @param file Le fichier à uploader
     * @param prefix Préfixe pour organiser les fichiers (ex: "chambres/")
     * @return L'URL publique du fichier uploadé
     */
    public String uploadFile(MultipartFile file, String prefix) {
        validateFile(file);

        String fileName = generateUniqueFileName(file.getOriginalFilename(), prefix);

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            String fileUrl = getFileUrl(fileName);
            log.info("Fichier uploadé avec succès: {}", fileUrl);
            return fileUrl;

        } catch (Exception e) {
            log.error("Erreur lors de l'upload du fichier: {}", fileName, e);
            throw new FileStorageException("Impossible d'uploader le fichier: " + file.getOriginalFilename(), e);
        }
    }

    /**
     * Supprime un fichier de MinIO
     * @param fileUrl L'URL du fichier à supprimer
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            String fileName = extractFileNameFromUrl(fileUrl);

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(fileName)
                            .build()
            );

            log.info("Fichier supprimé avec succès: {}", fileName);

        } catch (Exception e) {
            log.error("Erreur lors de la suppression du fichier: {}", fileUrl, e);
            throw new FileStorageException("Impossible de supprimer le fichier: " + fileUrl, e);
        }
    }

    /**
     * Génère une URL pré-signée temporaire (pour fichiers privés)
     * @param fileName Le nom du fichier
     * @param expiryMinutes Durée de validité en minutes
     * @return URL pré-signée
     */
    public String getPresignedUrl(String fileName, int expiryMinutes) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioProperties.getBucketName())
                            .object(fileName)
                            .expiry(expiryMinutes, TimeUnit.MINUTES)
                            .build()
            );
        } catch (Exception e) {
            log.error("Erreur lors de la génération de l'URL pré-signée: {}", fileName, e);
            throw new FileStorageException("Impossible de générer l'URL pré-signée", e);
        }
    }

    /**
     * Vérifie si un fichier existe dans MinIO
     * @param fileName Le nom du fichier
     * @return true si le fichier existe
     */
    public boolean fileExists(String fileName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(fileName)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Construit l'URL publique d'un fichier
     * @param fileName Le nom du fichier
     * @return L'URL publique complète
     */
    private String getFileUrl(String fileName) {
        return String.format("%s/%s/%s",
                minioProperties.getUrl(),
                minioProperties.getBucketName(),
                fileName
        );
    }

    /**
     * Extrait le nom du fichier depuis une URL
     * @param fileUrl L'URL complète du fichier
     * @return Le nom du fichier
     */
    private String extractFileNameFromUrl(String fileUrl) {
        String prefix = minioProperties.getUrl() + "/" + minioProperties.getBucketName() + "/";
        if (fileUrl.startsWith(prefix)) {
            return fileUrl.substring(prefix.length());
        }
        return fileUrl;
    }

    /**
     * Génère un nom de fichier unique
     * @param originalFileName Le nom du fichier original
     * @param prefix Préfixe pour organiser les fichiers
     * @return Nom de fichier unique
     */
    private String generateUniqueFileName(String originalFileName, String prefix) {
        String extension = getFileExtension(originalFileName);
        String uniqueId = UUID.randomUUID().toString();
        return prefix + uniqueId + extension;
    }

    /**
     * Extrait l'extension d'un fichier
     * @param fileName Le nom du fichier
     * @return L'extension (avec le point)
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex);
    }

    /**
     * Valide le fichier uploadé
     * @param file Le fichier à valider
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("Le fichier est vide");
        }

        String contentType = file.getContentType();
        if (contentType == null || !isImageContentType(contentType)) {
            throw new FileStorageException("Le fichier doit être une image (JPEG, PNG, GIF, WebP)");
        }

        // Limite de taille : 10 MB (définie dans application.yml)
        long maxSize = 10 * 1024 * 1024; // 10 MB
        if (file.getSize() > maxSize) {
            throw new FileStorageException("Le fichier dépasse la taille maximale autorisée (10 MB)");
        }
    }

    /**
     * Vérifie si le type MIME est une image
     * @param contentType Le type MIME
     * @return true si c'est une image
     */
    private boolean isImageContentType(String contentType) {
        return contentType.equals("image/jpeg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp");
    }
}