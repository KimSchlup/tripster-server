package ch.uzh.ifi.hase.soprafs24.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GoogleCloudStorageServiceTest {

    private Storage storage;
    private GoogleCloudStorageService service;

    @BeforeEach
    public void setUp() {
        storage = mock(Storage.class);
        service = new GoogleCloudStorageService(storage);
    }

    @Test
    public void testUploadFile_success() throws IOException {
        // Arrange
        String bucketName = "test-bucket";
        String fileName = "test.txt";
        MockMultipartFile multipartFile = new MockMultipartFile("file", fileName, "text/plain",
                "Hello Cloud".getBytes());

        when(storage.create(any(BlobInfo.class), any(byte[].class))).thenReturn(null);

        // Act
        String url = service.uploadFile(multipartFile, bucketName, fileName);

        // Assert
        assertEquals("https://storage.googleapis.com/test-bucket/uploads/test.txt", url);
        verify(storage, times(1)).create(any(BlobInfo.class), eq("Hello Cloud".getBytes()));
    }

    @Test
    public void testUploadFile_throwsException_whenFileIsEmpty() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> service.uploadFile(emptyFile, "bucket", "empty.txt"));

        assertTrue(exception.getMessage().contains("File is empty or missing"));
    }

    @Test
    public void testDeleteFile_successfulDeletion() {
        // Arrange
        String bucketName = "test-bucket";
        String fileName = "delete.txt";
        when(storage.delete(BlobId.of(bucketName, "uploads/" + fileName))).thenReturn(true);

        // Act & Assert (no exception thrown)
        assertDoesNotThrow(() -> service.deleteFile(bucketName, fileName));
        verify(storage).delete(BlobId.of(bucketName, "uploads/" + fileName));
    }

    @Test
    public void testDeleteFile_failedDeletion_throwsException() {
        // Arrange
        String bucketName = "test-bucket";
        String fileName = "nonexistent.txt";
        when(storage.delete(BlobId.of(bucketName, "uploads/" + fileName))).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> service.deleteFile(bucketName, fileName));

        assertTrue(exception.getMessage().contains("Failed to delete file"));
    }
}
