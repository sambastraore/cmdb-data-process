package sn.intouch.pfe.cmdbdataprocess.entities.vms;

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

public class InstanceGroupBuilder {
    public static InstanceGroup instanceGroupBuilder (Asset asset, String projectId ) throws IOException, JSONException {
        String name = MappingEngine.getName(asset);
        String manager = MappingEngine.getInstanceGroupRelationships(projectId,asset);
        String[] assetNames = {name,manager};
        if(History.toUpdate(projectId, List.of(assetNames))){
            name = name.split("projects/")[1];
            Resource resource = MappingEngine.getResource(asset);
            Map<String, Value> fields = MappingEngine.getFields(resource);
            String description = MappingEngine.getStringValue(fields,"description");
            String region = MappingEngine.getLocation(resource);
            String subnet = MappingEngine.getStringValue(fields,"subnetwork");
            String vpc = MappingEngine.getStringValue(fields,"network");

            String url = Config.baseUrl + "classes/InstanceGroup/cards";
            String body = "{"
                    + "\"name\": \"" + name + "\","
                    + "\"region\": \"" + region + "\","
                    + "\"manager\": \"" + manager + "\""
                    + "}";

            Integer cardId = HttpUtil.getCardId("InstanceGroup","name",name);

            HttpUtil.saveOrUpdate(name,url,body,cardId);
            return InstanceGroup.builder()
                    .name(name)
                    .description(description)
                    .region(region)
                    .vpc(vpc)
                    .subnetwork(subnet)
                    .manager(manager)
                    .build();
        }
        return null;
    }
}
