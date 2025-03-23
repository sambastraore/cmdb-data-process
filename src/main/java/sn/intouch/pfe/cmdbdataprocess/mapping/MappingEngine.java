package sn.intouch.pfe.cmdbdataprocess.mapping;

import com.google.cloud.asset.v1.Asset;
import com.google.cloud.asset.v1.Resource;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
}
