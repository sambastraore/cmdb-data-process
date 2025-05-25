package sn.intouch.pfe.cmdbdataprocess.services;

import com.google.cloud.asset.v1.*;
import com.google.protobuf.Value;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sn.intouch.pfe.cmdbdataprocess.mapping.MappingEngine;
import sn.intouch.pfe.cmdbdataprocess.utils.AssetMapping;
import sn.intouch.pfe.cmdbdataprocess.utils.CloudAssetAuthUtil;
import sn.intouch.pfe.cmdbdataprocess.utils.Config;
import sn.intouch.pfe.cmdbdataprocess.utils.HttpUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@Log4j2
public class PurgeService {

    private static final Map<String, String> TYPE_MAPPING = Map.of(
            AssetMapping.CLOUD_FUNCTION, "CloudFunction",
            AssetMapping.DISK, "Disk",
            AssetMapping.SUBNETWORK, "Subnet",
            AssetMapping.PROJECT, "Project",
            AssetMapping.VPC, "VirtualPrivateCloud",
            AssetMapping.URLMAP, "LoadBalancer",
            AssetMapping.VM_INSTANCE, "VirtualMachine",
            AssetMapping.INSTANCE_GROUP, "InstanceGroup",
            AssetMapping.CLOUD_SQL, "CloudSQL",
            AssetMapping.CLOUD_RUN_SERVICE, "CloudRun"
    );

    public static List<Asset> getAssets(String[] projectIds, String[] assetTypes, ContentType contentType) {
        List<Asset> allAssets = new ArrayList<>();

        for (String projectId : projectIds) {
            try (AssetServiceClient client = CloudAssetAuthUtil.getAssetServiceClient()) {
                ListAssetsRequest request = ListAssetsRequest.newBuilder()
                        .setParent(ProjectName.of(projectId).toString())
                        .addAllAssetTypes(Arrays.asList(assetTypes))
                        .setContentType(contentType)
                        .setPageSize(1000)
                        .build();

                for (Asset asset : client.listAssets(request).iterateAll()) {
                    allAssets.add(asset);
                }

            } catch (Exception e) {
                log.error("Error while processing project {}: {}", projectId, e.getMessage());
            }
        }

        return allAssets;
    }

    @Async
    public void deleteAssets(String[] projectIds, String[] assetTypes, ContentType contentType) throws IOException, JSONException {
        Map<String, List<String>> assetsByType = new HashMap<>();
        List<Asset> assetsList = getAssets(projectIds,assetTypes,contentType);
        for (Asset asset : assetsList) {
            try{
            String typeKey = MappingEngine.getAssetType(asset);
            String assetName = asset.getName().split("projects/")[1];
            if (Objects.equals(MappingEngine.getAssetType(asset), AssetMapping.PROJECT)){
                Resource resource = MappingEngine.getResource(asset);
                Map<String, Value> fields = MappingEngine.getFields(resource);
                assetName = MappingEngine.getStringValue(fields,"name").toLowerCase();
            }

            if (TYPE_MAPPING.containsKey(typeKey)) {
                String mappedType = TYPE_MAPPING.get(typeKey);
                assetsByType.computeIfAbsent(mappedType, k -> new ArrayList<>()).add(assetName);
                log.info("Asset classified: {} → {}", mappedType, assetName);
            } else {
                log.info("Asset not processed (unknown type): {}", typeKey);
            }
            }
            catch (Exception e){
                log.error("error while classifying assets in purge : " + e);
            }
        }

        for (Map.Entry<String, List<String>> entry : assetsByType.entrySet()) {
            purgeStaleCards(entry.getKey(), entry.getValue());
        }
        List<String> types = List.of("Apache", "Vhost", "Wildfly", "MySQL");
        for (String type : types) {
            purgeStaleCardsLastSeen(type);
        }
    }

    private void purgeStaleCards(String assetType, List<String> currentAssetNames) throws IOException, JSONException {
        String url = Config.baseUrl + "classes/" + assetType + "/cards";
        HttpURLConnection connection = HttpUtil.getHttpURLConnection(url, null, HttpUtil.getToken(), "GET");

        if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
            log.info("Fetching cards for asset type: {}", assetType);
            JSONArray cards = HttpUtil.getTheResponse(connection);

            for (int i = 0; i < cards.length(); i++) {
                JSONObject card = cards.getJSONObject(i);

                String nameField = "name";
                if ("Project".equals(assetType)) {
                    nameField = "projectName";
                }

                if (!card.has(nameField)) {
                    log.warn("Card missing expected field '{}': {}", nameField, card);
                    continue;
                }

                String name = card.getString(nameField);
                int id = card.getInt("_id");

                if (!currentAssetNames.contains(name)) {
                    String deleteUrl = url + "/" + id;
                    HttpURLConnection deleteConnection = HttpUtil.getHttpURLConnection(deleteUrl, null, HttpUtil.getToken(), "DELETE");

                    if (deleteConnection.getResponseCode() >= 200 && deleteConnection.getResponseCode() < 300) {
                        log.info("Successfully purged card: {} (id: {})", name, id);
                    } else {
                        log.warn("Failed to purge card: {} (id: {}) - HTTP {}", name, id, deleteConnection.getResponseCode());
                    }
                }
            }
        } else {
            log.warn("Failed to fetch cards for type {} - HTTP {}", assetType, connection.getResponseCode());
        }
    }


    private void purgeStaleCardsLastSeen(String assetType) throws IOException, JSONException {
        String url = Config.baseUrl + "classes/" + assetType + "/cards";
        HttpURLConnection connection = HttpUtil.getHttpURLConnection(url, null, HttpUtil.getToken(), "GET");

        if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
            log.info("Fetching cards for asset type: {}", assetType);
            JSONArray cards = HttpUtil.getTheResponse(connection);

            Instant now = Instant.now();

            for (int i = 0; i < cards.length(); i++) {
                JSONObject card = cards.getJSONObject(i);
                int id = card.getInt("_id");

                if (!card.has("LastSeen")) {
                    log.warn("Card {} missing 'LastSeen' field, skipping", id);
                    continue;
                }

                String lastSeenStr = card.getString("LastSeen");
                Instant lastSeenTime;
                try {
                    lastSeenTime = Instant.parse(lastSeenStr);
                } catch (DateTimeParseException e) {
                    log.warn("Failed to parse LastSeen for card {}: {}", id, lastSeenStr);
                    continue;
                }

                Duration age = Duration.between(lastSeenTime, now);
                if (age.toDays() >= 40) {
                    String deleteUrl = url + "/" + id;
                    HttpURLConnection deleteConnection = HttpUtil.getHttpURLConnection(deleteUrl, null, HttpUtil.getToken(), "DELETE");

                    if (deleteConnection.getResponseCode() >= 200 && deleteConnection.getResponseCode() < 300) {
                        log.info("Purged stale card ({}): id={}, LastSeen={}, Age={} days", assetType, id, lastSeenStr, age.toDays());
                    } else {
                        log.warn("Failed to purge card {} ({}), HTTP {}", id, assetType, deleteConnection.getResponseCode());
                    }
                }
            }
        } else {
            log.warn("Failed to fetch cards for type {} - HTTP {}", assetType, connection.getResponseCode());
        }
    }


}
