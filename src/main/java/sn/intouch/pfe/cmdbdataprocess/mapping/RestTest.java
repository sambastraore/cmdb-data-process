package sn.intouch.pfe.cmdbdataprocess.mapping;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

public class RestTest {
    public static void main(String[] args) {
        try {
            String projectId = "eme-iacc";
            String scope = "projects/" + projectId;
            //String assetType = AssetMapping.BACKEND;
            String keyFilePath = "/Users/sambastraore/Desktop/pfe_tra/serviceAccountKey.json";

            FileInputStream serviceAccountStream = new FileInputStream(keyFilePath);
            GoogleCredentials credentials = ServiceAccountCredentials.fromStream(serviceAccountStream);
            credentials = credentials.createScoped("https://www.googleapis.com/auth/cloud-platform");

            credentials.refresh();
            String accessToken = credentials.getAccessToken().getTokenValue();

          //  URL url = new URL("https://cloudasset.googleapis.com/v1/" + scope + "/assets?asset_types=" + assetType);
            URL url1 = new URL("https://compute.googleapis.com/compute/v1/projects/eme-iacc/global/backendServices/lb-wildfly-test-1");
            URL url2 = new URL("https://cloudfunctions.googleapis.com/v2/projects/eme-iacc/locations/europe-west1/functions/balanceUpdateFunctionAndInsert");
            URL url3 = new URL( "https://www.googleapis.com/compute/v1/projects/eme-iacc/zones/europe-west1-b/instances/instance-group-test-bgxf");
            URL url4 = new URL("https://sqladmin.googleapis.com/sql/v1beta4/projects/eme-iacc/instances/eme-iacc");
            HttpURLConnection connection = (HttpURLConnection) url3.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setRequestProperty("Content-Type", "application/json");

            // Lire la réponse
            int responseCode = connection.getResponseCode();
            BufferedReader in;
            if (responseCode >= 200 && responseCode < 300) {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String line;
            StringBuilder response = new StringBuilder();
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            in.close();

            System.out.println("Code de réponse: " + responseCode);
            System.out.println("Réponse de l'API Cloud Asset:");
            System.out.println(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
