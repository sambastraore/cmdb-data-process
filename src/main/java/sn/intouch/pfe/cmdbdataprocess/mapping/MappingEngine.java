package sn.intouch.pfe.cmdbdataprocess.mapping;

import com.google.cloud.asset.v1.*;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Value;
import lombok.extern.log4j.Log4j2;
import sn.intouch.pfe.cmdbdataprocess.utils.AssetMapping;
import sn.intouch.pfe.cmdbdataprocess.utils.CloudAssetAuthUtil;

import java.io.IOException;
import java.util.*;

@Log4j2
public class MappingEngine {

public static String getName (Asset asset){
    Map<Descriptors.FieldDescriptor,Object> allFields = asset.getAllFields();
    return (String)allFields.get(Asset.getDescriptor().findFieldByName("name"));

}

public static String getAssetType (Asset asset){
    Map<Descriptors.FieldDescriptor, Object> allFields = asset.getAllFields();
    return (String)allFields.get(Asset.getDescriptor().findFieldByName("asset_type"));
}

public static Resource getResource (Asset asset){
    Map<Descriptors.FieldDescriptor,Object> allFields = asset.getAllFields();
    return (Resource) allFields.get(Asset.getDescriptor().findFieldByName("resource"));
}

public static String getLocation(Resource resource){
    return resource.getLocation();
}

public static Map<String, Value> getFields(Resource resource){
    return resource.getData().getFieldsMap();
}

public static String getFunctionsUrl(Map<String,Value> dataFields){ // works only for cloud functions
    return dataFields.containsKey("url") ? dataFields.get("url").getStringValue() : "";
}

public static Map<String,Value> getStructValue (Map<String,Value> dataFields,String structName){ // for ingress, egress, appId for cloud functions
    return dataFields.containsKey(structName) ? dataFields.get(structName).getStructValue().getFieldsMap() : new HashMap<>();
}

public static List<Value> getListValue (Map<String,Value> dataFields, String listName){
    return dataFields.containsKey(listName) ? dataFields.get(listName).getListValue().getValuesList() : new ArrayList<>();
}


public static String getStringValue (Map<String,Value> mapValue, String key){
    return mapValue.containsKey(key) ? mapValue.get(key).getStringValue() : "";
}



public static String getFunctionsIngressSettings (Map<String,Value> serviceConfigFields){
    return serviceConfigFields.containsKey("ingressSettings") ? serviceConfigFields.get("ingressSettings").getStringValue() : "";
}

public static String getFunctionsEgressSettings (Map<String,Value> serviceConfigFields){
        return serviceConfigFields.containsKey("vpcConnectorEgressSettings") ? serviceConfigFields.get("vpcConnectorEgressSettings").getStringValue() : "";
    }

public static String getFunctionsAppId (Map<String,Value> serviceConfigFields){
    String service = "";
    if (serviceConfigFields.containsKey("service")) {
        service = serviceConfigFields.get("service").getStringValue();
    }
    String[] serviceParts = service.split("/");
    return serviceParts[serviceParts.length - 1];

}

public static String getDiskSize (Map<String,Value> dataFields){
    return dataFields.containsKey("sizeGb") ? dataFields.get("sizeGb").getStringValue() : "";
}

public static String getProjectFromName(String name){
    String[] parts = name.split("/");
    String project="";
    for (int i = 0; i < parts.length-1; i++) {
        if (Objects.equals(parts[i], "projects")){
            project = parts[i+1];
            break;
        }
    }
    return project;
}

public static String getRealValue(String string){
    String[] parts = string.split("/");
    return parts[parts.length - 1];
}

public static String getLoadBalancerNameFromProxy(String string){
    String[] parts = string.split("-target-proxy");
    return parts[0];
}

public static List<Asset> getFrontends(Asset backend){
    return new ArrayList<>();
}

    public static Map<String,List<String>> getLoadBalancerRelationships(String projectId, Asset urlmap)
            throws IOException, IllegalArgumentException {

        List<String> backends = new ArrayList<>();
        List<String> proxies = new ArrayList<>();
        String[] assetTypes = {
                AssetMapping.URLMAP
        };
        try (AssetServiceClient client = CloudAssetAuthUtil.getAssetServiceClient()) {
            ProjectName parent = ProjectName.of(projectId);

            ListAssetsRequest request =
                    ListAssetsRequest.newBuilder()
                            .setParent(parent.toString())
                            .addAllAssetTypes(Arrays.asList(assetTypes))
                            .setContentType(ContentType.RELATIONSHIP)
                            .build();
            AssetServiceClient.ListAssetsPagedResponse response = client.listAssets(request);
            //handleResponse(response);
            for (Asset asset : response.getPage().getValues()){
                if (MappingEngine.getRelationship(asset).equals(RelationshipMapping.URLMAP_TO_BACKEND_SERVICE)){
                    if (Objects.equals(getName(urlmap), getName(asset)))
                        backends.add(MappingEngine.getRelatedAssetName(asset));
                        //log.info("backend for this urlmap : " + MappingEngine.getRelatedAssetName(asset));
                }

                if (MappingEngine.getRelationship(asset).equals(RelationshipMapping.URLMAP_TO_HTTP_PROXY)){
                    if (Objects.equals(getName(urlmap), getName(asset)))
                        proxies.add(MappingEngine.getRelatedAssetName(asset));
                        //log.info("http proxy for this urlmap : " + MappingEngine.getRelatedAssetName(asset));
                }

                if (MappingEngine.getRelationship(asset).equals(RelationshipMapping.URLMAP_TO_HTTPS_PROXY)){
                    if (Objects.equals(getName(urlmap), getName(asset)))
                        proxies.add(MappingEngine.getRelatedAssetName(asset));
                        //log.info("https proxy for this urlmap : " + MappingEngine.getRelatedAssetName(asset));
                }
            }
        }
        Map<String,List<String>> loadBalancerComponents = new HashMap<>();
        loadBalancerComponents.put("backends",backends);
        loadBalancerComponents.put("frontends",proxies);
        return loadBalancerComponents;
    }

    public static String getInstanceRelationships(String projectId, Asset instance)
            throws IOException, IllegalArgumentException {

        String[] assetTypes = {
                AssetMapping.VM_INSTANCE
        };
        try (AssetServiceClient client = CloudAssetAuthUtil.getAssetServiceClient()) {
            ProjectName parent = ProjectName.of(projectId);

            ListAssetsRequest request =
                    ListAssetsRequest.newBuilder()
                            .setParent(parent.toString())
                            .addAllAssetTypes(Arrays.asList(assetTypes))
                            .setContentType(ContentType.RELATIONSHIP)
                            .build();
            AssetServiceClient.ListAssetsPagedResponse response = client.listAssets(request);
            for (Asset asset : response.getPage().getValues()){
                if (MappingEngine.getRelationship(asset).equals(RelationshipMapping.INSTANCE_TO_GROUP)){
                    if (Objects.equals(getName(instance), getName(asset)))
                        return MappingEngine.getRelatedAssetName(asset);
                }
            }
        }
        return "";
    }

    public static String getInstanceGroupRelationships(String projectId, Asset group)
            throws IOException, IllegalArgumentException {

        String[] assetTypes = {
                AssetMapping.INSTANCE_GROUP
        };
        try (AssetServiceClient client = CloudAssetAuthUtil.getAssetServiceClient()) {
            ProjectName parent = ProjectName.of(projectId);

            ListAssetsRequest request =
                    ListAssetsRequest.newBuilder()
                            .setParent(parent.toString())
                            .addAllAssetTypes(Arrays.asList(assetTypes))
                            .setContentType(ContentType.RELATIONSHIP)
                            .build();
            AssetServiceClient.ListAssetsPagedResponse response = client.listAssets(request);
            for (Asset asset : response.getPage().getValues()){
                if (MappingEngine.getRelationship(asset).equals(RelationshipMapping.GROUP_TO_MANAGER)){
                    if (Objects.equals(getName(group), getName(asset)))
                        return MappingEngine.getRelatedAssetName(asset);
                }
            }
        }
        return "";
    }

    private static Map<String,String> handleResponse(AssetServiceClient.ListAssetsPagedResponse response) {
        Map<String,String> theResponse = new HashMap<>();
        for (Asset asset : response.getPage().getValues()) {
            Map< Descriptors.FieldDescriptor,Object> fields = asset.getAllFields();
            String asset_type = (String) fields.get(Asset.getDescriptor().findFieldByName("asset_type"));
            asset_type = getRealValue(asset_type);
            String name = (String) fields.get(Asset.getDescriptor().findFieldByName("name"));
            name = getRealValue(name);
            //theResponse.put("asset_type",asset_type);
            //theResponse.put("asset_name",name);
            //theResponse.put("relationship",relationship);
            //theResponse.put("related_asset",MappingEngine.getRealValue(relatedAssetName));
            //log.info("asset type : " + asset_type);
            //log.info("asset name : " + name);
            //log.info("relationship : " + relationship);
            //log.info("related asset : " + MappingEngine.getRealValue(relatedAssetName));
            //log.info(getRelatedAsset(asset).get("relationship"));
            //log.info(getRelatedAsset(asset).get("related_asset"));
            System.out.println("-------------------------------------------------");
        }

        return theResponse;
    }

    public static String getRelatedAssetName(Asset asset){
        Map< Descriptors.FieldDescriptor,Object> fields = asset.getAllFields();
        RelatedAsset relatedAsset = (RelatedAsset) fields.get(Asset.getDescriptor().findFieldByName("related_asset"));
        return relatedAsset.getAsset();
    }

    public static String getRelationship(Asset asset){
        Map< Descriptors.FieldDescriptor,Object> fields = asset.getAllFields();
        RelatedAsset relatedAsset = (RelatedAsset) fields.get(Asset.getDescriptor().findFieldByName("related_asset"));
        return relatedAsset.getRelationshipType();
    }
}
