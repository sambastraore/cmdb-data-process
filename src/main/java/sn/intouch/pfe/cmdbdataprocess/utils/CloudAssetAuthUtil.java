package sn.intouch.pfe.cmdbdataprocess.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.asset.v1.AssetServiceClient;
import com.google.cloud.asset.v1.AssetServiceSettings;

import java.io.FileInputStream;
import java.io.IOException;

public class CloudAssetAuthUtil {

    private static final String CREDENTIALS_PATH = "/Users/sambastraore/Desktop/pfe_tra/serviceAccountKey.json";

    public static AssetServiceClient getAssetServiceClient() throws IOException {
        GoogleCredentials credentials;
        try (FileInputStream serviceAccountStream = new FileInputStream(CREDENTIALS_PATH)) {
            credentials = GoogleCredentials.fromStream(serviceAccountStream);
        }

        AssetServiceSettings assetServiceSettings = AssetServiceSettings.newBuilder()
                .setCredentialsProvider(() -> credentials)
                .build();

        return AssetServiceClient.create(assetServiceSettings);
    }
}

