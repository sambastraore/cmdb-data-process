package sn.intouch.pfe.cmdbdataprocess.entities.vms;

import com.google.cloud.asset.v1.Asset;
import com.google.cloud.asset.v1.Resource;
import com.google.protobuf.Value;
import sn.intouch.pfe.cmdbdataprocess.mapping.MappingEngine;

import java.io.IOException;
import java.util.Map;

public class InstanceGroupBuilder {
    public static InstanceGroup instanceGroupBuilder (Asset asset, String projectId ) throws IOException {
        String name = MappingEngine.getName(asset);
        name = MappingEngine.getRealValue(name);
        Resource resource = MappingEngine.getResource(asset);
        Map<String, Value> fields = MappingEngine.getFields(resource);
        String description = MappingEngine.getStringValue(fields,"description");
        String region = MappingEngine.getLocation(resource);
        String subnet = MappingEngine.getStringValue(fields,"subnetwork");
        String vpc = MappingEngine.getStringValue(fields,"network");
        String manager = MappingEngine.getInstanceGroupRelationships(projectId,asset);
        return InstanceGroup.builder()
                .name(name)
                .description(description)
                .region(region)
                .vpc(vpc)
                .subnetwork(subnet)
                .manager(manager)
                .build();
    }
}
