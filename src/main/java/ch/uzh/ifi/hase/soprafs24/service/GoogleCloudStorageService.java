package ch.uzh.ifi.hase.soprafs24.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class GoogleCloudStorageService {

    private final Storage storage;

    public GoogleCloudStorageService(Storage storage) {
        this.storage = storage; // Injected via Spring
    }

    public String uploadFile(MultipartFile file, String bucketName, String fileName) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or missing");
        }

        String objectName = "uploads/" + fileName;

        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType()) // Proper MIME type
                .build();

        storage.create(blobInfo, file.getBytes());

        return String.format("https://storage.googleapis.com/%s/%s", bucketName, objectName);
    }

    public void deleteFile(String bucketName, String fileName) {
        String objectName = "uploads/" + fileName;
        boolean deleted = storage.delete(BlobId.of(bucketName, objectName));
        if (!deleted) {
            throw new IllegalArgumentException("Failed to delete file: " + objectName);
        }
    }
}
