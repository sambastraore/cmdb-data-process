package sn.intouch.pfe.cmdbdataprocess.mapping;

import com.google.cloud.asset.v1.*;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Value;
import lombok.extern.log4j.Log4j2;
import sn.intouch.pfe.cmdbdataprocess.utils.AssetMapping;
import sn.intouch.pfe.cmdbdataprocess.utils.CloudAssetAuthUtil;
import sn.intouch.pfe.cmdbdataprocess.utils.RelationshipMapping;

import java.io.IOException;
import java.util.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

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

public static Map<String,Value> getStructValue (Map<String,Value> dataFields,String structName){ // for ingress, egress, appId for cloud functions
    return dataFields.containsKey(structName) ? dataFields.get(structName).getStructValue().getFieldsMap() : new HashMap<>();
}

public static List<Value> getListValue (Map<String,Value> dataFields, String listName){
    return dataFields.containsKey(listName) ? dataFields.get(listName).getListValue().getValuesList() : new ArrayList<>();
}


public static String getStringValue (Map<String,Value> mapValue, String key){
    return mapValue.containsKey(key) ? mapValue.get(key).getStringValue() : "";
}

public static String getFunctionsAppId (Map<String,Value> serviceConfigFields){
    String service = "";
    if (serviceConfigFields.containsKey("service")) {
        service = serviceConfigFields.get("service").getStringValue();
    }
    String[] serviceParts = service.split("/");
    return serviceParts[serviceParts.length - 1];

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

    public static Set<String> sameSubnetSQLInstancesForVM (String projectId, Asset vm) throws IOException {
        String[] assetTypes = {
                AssetMapping.VM_INSTANCE,
                AssetMapping.SUBNETWORK,
                AssetMapping.CLOUD_SQL,
        };
        Set<String> ips = new HashSet<>();
        try (AssetServiceClient client = CloudAssetAuthUtil.getAssetServiceClient()) {
            ProjectName parent = ProjectName.of(projectId);

            ListAssetsRequest request =
                    ListAssetsRequest.newBuilder()
                            .setParent(parent.toString())
                            .addAllAssetTypes(Arrays.asList(assetTypes))
                            .setContentType(ContentType.RESOURCE)
                            .build();
            AssetServiceClient.ListAssetsPagedResponse response = client.listAssets(request);
            String privateIP = "";
            String range = "";
            List<Value> networkInterfaces = null;
            for (Asset asset : response.getPage().getValues()){
                if (asset.getName().equals(vm.getName())){
                    Resource resource = getResource(asset);
                    Map<String, Value> fields = getFields(resource);
                    networkInterfaces = getListValue(fields,"networkInterfaces");
                    privateIP = networkInterfaces.get(0).getStructValue().getFieldsMap().get("networkIP").getStringValue();

                }
            }
            if (!privateIP.isEmpty()) {
                for (Asset asset : response.getPage().getValues()) {
                    Resource resource = getResource(asset);
                    Map<String, Value> fields = getFields(resource);
                    if (Objects.equals(getAssetType(asset), AssetMapping.SUBNETWORK) && asset.getName().split("projects")[1].equals(networkInterfaces.get(0).getStructValue().getFieldsMap().get("subnetwork").getStringValue().split("projects")[1])) {
                        range = getStringValue(fields,"ipCidrRange");
                    }
                }
            }

            if (!range.isEmpty()){
                for (Asset asset : response.getPage().getValues()){
                    if (Objects.equals(getAssetType(asset), AssetMapping.CLOUD_SQL)) {
                        Resource resource = getResource(asset);
                        Map<String, Value> fields = getFields(resource);
                        List<Value> ipAddresses = getListValue(fields,"ipAddresses");
                        String privateIPSQL = "";
                        if (ipAddresses.size() > 1){
                            privateIPSQL = ipAddresses.get(1).getStructValue().getFieldsMap().get("ipAddress").getStringValue();
                        }

                        if (isIPInRange(privateIPSQL,range))
                            ips.add(privateIPSQL);
                    }

                }
            }
        }
        return ips;

    }

    public static boolean isIPInRange(String ipAddress, String cidrRange) throws UnknownHostException {
        String[] parts = cidrRange.split("/");
        String network = parts[0];
        int prefixLength = Integer.parseInt(parts[1]);

        long ip = ipToLong(InetAddress.getByName(ipAddress));
        long networkIP = ipToLong(InetAddress.getByName(network));

        long subnetMask = (0xFFFFFFFFL << (32 - prefixLength));

        return (ip & subnetMask) == (networkIP & subnetMask);
    }

    private static long ipToLong(InetAddress ip) {
        byte[] addr = ip.getAddress();
        long result = 0;
        for (byte b : addr) {
            result = (result << 8) + (b & 0xFF);
        }
        return result;
    }

    public static void main(String[] args) {
        try {
            String ip = "192.168.1.254";
            String cidr = "192.168.1.0/24";
            boolean isInRange = isIPInRange(ip, cidr);
            log.info("L'IP " + ip + " appartient au réseau " + cidr + ": " + isInRange);
        } catch (UnknownHostException e) {
            log.info("Erreur d'adresse IP ou de réseau : " + e.getMessage());
        }
    }
}
