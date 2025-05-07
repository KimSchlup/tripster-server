package ch.uzh.ifi.hase.soprafs24.utils;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

@Component
public class GenerateV4GetObjectSignedUrl {
    /**
     * Signing a URL requires Credentials which implement ServiceAccountSigner.
     * These can be set
     * explicitly using the Storage.SignUrlOption.signWith(ServiceAccountSigner)
     * option. If you don't,
     * you could also pass a service account signer to StorageOptions, i.e.
     * StorageOptions().newBuilder().setCredentials(ServiceAccountSignerCredentials).
     * In this example,
     * neither of these options are used, which means the following code only works
     * when the
     * credentials are defined via the environment variable
     * GOOGLE_APPLICATION_CREDENTIALS, and those
     * credentials are authorized to sign a URL. See the documentation for
     * Storage.signUrl for more
     * details.
     */
    public String generateV4GetObjectSignedUrl(
            String projectId, String bucketName, String objectName) throws StorageException {

        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

        // Define resource
        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, objectName)).build();

        URL url = storage.signUrl(blobInfo, 15, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature());

        System.out.println("Generated GET signed URL:");
        System.out.println(url);
        return url.toString();
    }
}