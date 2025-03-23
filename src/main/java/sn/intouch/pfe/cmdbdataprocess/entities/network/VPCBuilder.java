package sn.intouch.pfe.cmdbdataprocess.entities.network;

import com.google.cloud.asset.v1.Asset;
import com.google.cloud.asset.v1.Resource;
import com.google.protobuf.Value;
import sn.intouch.pfe.cmdbdataprocess.mapping.MappingEngine;

import java.util.Map;

public class VPCBuilder {
    public static VirtualPrivateCloud vpcBuilder (Asset asset){
        String name = MappingEngine.getName(asset);
        String realName = MappingEngine.getRealValue(name);
        Resource resource = MappingEngine.getResource(asset);
        Map<String, Value> fields = MappingEngine.getFields(resource);
        String description = MappingEngine.getStringValue(fields, "description");
        String firewallRules = MappingEngine.getStringValue(fields,"networkFirewallPolicyEnforcementOrder");
        Map<String,Value> routingConfigs = MappingEngine.getStructValue(fields, "routingConfig");
        String routingMode = MappingEngine.getStringValue(routingConfigs,"routingMode");
        String projectName = MappingEngine.getProjectFromName(name);

        return VirtualPrivateCloud.builder()
                .description(description)
                .name(realName)
                .firewallRules(firewallRules)
                .routingMode(routingMode)
                .projectName(projectName)
                .build();

    }
}
