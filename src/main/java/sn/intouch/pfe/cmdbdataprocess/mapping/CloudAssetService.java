package sn.intouch.pfe.cmdbdataprocess.mapping;

import com.google.cloud.asset.v1.AssetServiceClient;
import com.google.cloud.asset.v1.ResourceSearchResult;
import com.google.cloud.asset.v1.SearchAllResourcesRequest;
import sn.intouch.pfe.cmdbdataprocess.utils.CloudAssetAuthUtil;


import java.io.IOException;

//@Log4j2
public class CloudAssetService {

    static String scope = "projects/eme-iacc";
    static String scope1 = "organizations/349241044184";
    static String scope2 = "folders/437674684111";

    public static void listAssets() {
        try (AssetServiceClient assetServiceClient = CloudAssetAuthUtil.getAssetServiceClient()) {
            SearchAllResourcesRequest request = SearchAllResourcesRequest.newBuilder()
                //.addAssetTypes(AssetMapping.TARGET_HTTPS_PROXY)
                    //.addAssetTypes(AssetMapping.TARGET_HTTP_PROXY)
                    //.addAssetTypes(AssetMapping.IP_ADDRESSES)
                    .addAssetTypes("appengine.googleapis.com/Version")
                    //.setQuery("name:lb-wildfly-test-1")
                    //.addAssetTypes(AssetMapping.CloudSQL)
                    //.addAssetTypes("compute.googleapis.com/BackendService")
                    .setScope(scope)
                    .build();

            for (ResourceSearchResult result : assetServiceClient.searchAllResources(request).iterateAll()) {
                //System.out.println("Nom: " + result.getName());
                //System.out.println("Location: " + result.getLocation());
                System.out.println("Fields: " + result.getAllFields());
                //System.out.println("Description: " + result.getDescription());
                //System.out.println("Asset type : " + result.getAssetType());
                //System.out.println("Asset Relations: " + result.getParentAssetType());
                //System.out.println("Additional : " + result.getAdditionalAttributesOrBuilder());
                System.out.println("-----------------------------");
            }
        } catch (IOException e) {
            System.err.println("Erreur d'authentification à Cloud Asset API : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        listAssets();
    }
}

