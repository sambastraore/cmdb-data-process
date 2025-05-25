package sn.intouch.pfe.cmdbdataprocess.entities.network;

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

public class VPCBuilder {
    public static VirtualPrivateCloud vpcBuilder (Asset asset, String projectId) throws IOException, JSONException {
        String name = MappingEngine.getName(asset);
        String[] assetNames = {name};
        if (History.toUpdate(projectId, List.of(assetNames))){
            String realName = name.split("projects/")[1]; // take the original name
            Resource resource = MappingEngine.getResource(asset);
            Map<String, Value> fields = MappingEngine.getFields(resource);
            String description = MappingEngine.getStringValue(fields, "description");
            String firewallRules = MappingEngine.getStringValue(fields,"networkFirewallPolicyEnforcementOrder");
            Map<String,Value> routingConfigs = MappingEngine.getStructValue(fields, "routingConfig");
            String routingMode = MappingEngine.getStringValue(routingConfigs,"routingMode");
            String projectName = name.split("/")[0].toLowerCase();

            String url = Config.baseUrl + "classes/VirtualPrivateCloud/cards";
            String body = "{"
                    + "\"name\": \"" + realName + "\","
                    + "\"firewallRules\": \"" + firewallRules + "\","
                    + "\"routingMode\": \"" + routingMode + "\""
                    + "}";

            Integer cardId = HttpUtil.getCardId("VirtualPrivateCloud","name",realName);

            HttpUtil.saveOrUpdate(realName,url,body,cardId);

            return VirtualPrivateCloud.builder()
                    .description(description)
                    .name(realName)
                    .firewallRules(firewallRules)
                    .routingMode(routingMode)
                    .projectName(projectName)
                    .build();
        }
        return null;
    }
}
