package sn.intouch.pfe.cmdbdataprocess.entities.serverless;

import com.google.cloud.asset.v1.Asset;
import com.google.cloud.asset.v1.Resource;
import com.google.protobuf.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import sn.intouch.pfe.cmdbdataprocess.mapping.History;
import sn.intouch.pfe.cmdbdataprocess.mapping.MappingEngine;
import sn.intouch.pfe.cmdbdataprocess.utils.Config;
import sn.intouch.pfe.cmdbdataprocess.utils.HttpUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CloudRunBuilder {
    public static CloudRun cloudRunBuilder(Asset asset, String projectId) throws IOException, JSONException {
        String name = MappingEngine.getName(asset);
        String[] assetNames = {name};
        if(History.toUpdate(projectId,List.of(assetNames))){
            name = name.split("projects/")[1];
            Resource resource = MappingEngine.getResource(asset);
            String location = MappingEngine.getLocation(resource);
            Map<String, Value> fields = MappingEngine.getFields(resource);
            Map<String, Value> status = MappingEngine.getStructValue(fields, "status");
            Map<String,Value> address = MappingEngine.getStructValue(status,"address");
            String url = MappingEngine.getStringValue(address,"url");


            //List<Value> ownerReferences = MappingEngine.getListValue(metadata,"ownerReferences");
            Map<String,Value> spec = MappingEngine.getStructValue(fields,"spec");
            Map<String,Value> template = MappingEngine.getStructValue(spec,"template");
            Map<String, Value> metadata = MappingEngine.getStructValue(template,"metadata");
            Map <String, Value> annotations = MappingEngine.getStructValue(metadata,"annotations");
            String sqlInstance = MappingEngine.getStringValue(annotations,"run.googleapis.com/cloudsql-instances");
            String [] sqlInstanceParts = sqlInstance.split(":");
            sqlInstance = sqlInstanceParts[0] + "/instances/" + sqlInstanceParts[2];
            String networkInformation = MappingEngine.getStringValue(annotations,"run.googleapis.com/network-interfaces");
            String cpu = template.get("spec").getStructValue().getFieldsMap().get("containers").getListValue().getValuesList().get(0).getStructValue().getFieldsMap().get("resources").getStructValue().getFieldsMap().get("limits").getStructValue().getFieldsMap().get("cpu").getStringValue();
            String memory = template.get("spec").getStructValue().getFieldsMap().get("containers").getListValue().getValuesList().get(0).getStructValue().getFieldsMap().get("resources").getStructValue().getFieldsMap().get("limits").getStructValue().getFieldsMap().get("memory").getStringValue();
            //String sqlInstance = template.get("spec").getStructValue().getFieldsMap().get("containers").getListValue().getValuesList().get(0).getStructValue().getFieldsMap().get("env").getListValue().getValuesList().get(3).getStructValue().getFieldsMap().get("value").getStringValue();

            String networkPart = networkInformation.isEmpty()  ? "\"networkInformation\": \"" + networkInformation + "\"," : "\"networkInformation\": " + networkInformation + ",";

            String urlREST = Config.baseUrl + "classes/CloudRun/cards";
            String body = "{"
                    + "\"name\": \"" + name + "\","
                    + "\"region\": \"" + location + "\","
                    + "\"url\": \"" + url + "\","
                    + "\"cpu\": \"" + cpu + "\","
                    + networkPart
                    + "\"ram\": \"" + memory + "\""
                    + "}";

            Integer cardId = HttpUtil.getCardId("CloudRun","name",name);

            HttpUtil.saveOrUpdate(name,urlREST,body,cardId);
            return CloudRun.builder()
                    .name(name)
                    .region(location)
                    .url(url)
                    .cpu(cpu)
                    .ram(memory)
                    .cloudSQLInstance(sqlInstance)
                    .networkInformations(networkInformation)
                    .build();
        }
        return null;
    }
}
