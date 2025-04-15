package sn.intouch.pfe.cmdbdataprocess.mapping;

import com.google.cloud.asset.v1.AssetServiceClient;
import com.google.cloud.asset.v1.ResourceSearchResult;
import com.google.cloud.asset.v1.SearchAllResourcesRequest;
import com.google.cloud.asset.v1.SearchAllResourcesResponse;
import com.google.api.gax.rpc.ApiException;
import sn.intouch.pfe.cmdbdataprocess.utils.CloudAssetAuthUtil;

import java.io.IOException;
import java.util.Arrays;

public class Testv2 {
    public static void main(String[] args) throws IOException {
        // TODO: Remplacer par votre ID de projet
        String projectId = "eme-iacc";
        String scope = String.format("projects/%s", projectId);

        try (AssetServiceClient client = CloudAssetAuthUtil.getAssetServiceClient()) {
            // 1. D'abord, lister toutes les instances Cloud SQL
            SearchAllResourcesRequest sqlRequest = SearchAllResourcesRequest.newBuilder()
                    .setScope(scope)
                    .addAssetTypes("sqladmin.googleapis.com/Instance")
                    .setPageSize(500)
                    .build();

            System.out.println("Instances Cloud SQL trouvées:");
            for (ResourceSearchResult result : client.searchAllResources(sqlRequest).iterateAll()) {
                System.out.println("Nom: " + result.getDisplayName());
                System.out.println("  - Type: " + result.getAssetType());
                System.out.println("  - Nom de ressource: " + result.getName());
                System.out.println("  - Projet: " + result.getProject());
                // Les annotations peuvent contenir des infos sur les connexions
                result.getAdditionalAttributes().getFieldsMap().forEach((key, value) -> {
                    System.out.println("  - " + key + ": " + value);
                });
            }

            // 2. Lister les VMs pour éventuellement analyser les relations
            SearchAllResourcesRequest vmRequest = SearchAllResourcesRequest.newBuilder()
                    .setScope(scope)
                    .addAssetTypes("compute.googleapis.com/Instance")
                    .setPageSize(500)
                    .build();

            System.out.println("\nInstances VM trouvées:");
            for (ResourceSearchResult result : client.searchAllResources(vmRequest).iterateAll()) {
                System.out.println("Nom: " + result.getDisplayName());
                System.out.println("  - Type: " + result.getAssetType());
                System.out.println("  - Nom de ressource: " + result.getName());

                // Pour identifier les relations, vous devez analyser les métadonnées
                result.getAdditionalAttributes().getFieldsMap().forEach((key, value) -> {
                    if (key.contains("sqlInstance") || key.contains("connectionName")) {
                        System.out.println("  - Relation Cloud SQL détectée: " + key + " = " + value);
                    }
                });
            }
        } catch (ApiException e) {
            System.err.println("Exception API: " + e.getMessage());
        }
    }
}
