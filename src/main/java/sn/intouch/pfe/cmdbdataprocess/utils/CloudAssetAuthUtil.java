package sn.intouch.pfe.cmdbdataprocess.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.asset.v1.AssetServiceClient;
import com.google.cloud.asset.v1.AssetServiceSettings;

import java.io.FileInputStream;
import java.io.IOException;


public class CloudAssetAuthUtil {


    public static AssetServiceClient getAssetServiceClient() throws IOException {
        GoogleCredentials credentials;
        try (FileInputStream serviceAccountStream = new FileInputStream(Config.CREDENTIALS_PATH)) {
            credentials = GoogleCredentials.fromStream(serviceAccountStream);
        }

        AssetServiceSettings assetServiceSettings = AssetServiceSettings.newBuilder()
                .setCredentialsProvider(() -> credentials)
                .build();

        return AssetServiceClient.create(assetServiceSettings);
    }
}

