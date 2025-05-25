package sn.intouch.pfe.cmdbdataprocess.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.LoggingOptions;

import java.io.FileInputStream;
import java.io.IOException;

public class LoggingUtil {

    public static Logging createLoggingClient() throws IOException {
        // Chemin vers ta clé de compte de service
        String credentialsPath = Config.CREDENTIALS_PATH;

        // Crée les credentials
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath));

        // Crée le client Logging avec credentials + project ID
        Logging logging = LoggingOptions.newBuilder()
                .setCredentials(credentials)
                .setProjectId("eme-iacc") // <-- remplace par ton vrai Project ID
                .build()
                .getService();

        return logging;
    }
}
