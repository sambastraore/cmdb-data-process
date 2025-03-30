package sn.intouch.pfe.cmdbdataprocess.entities.vms;

import com.google.cloud.asset.v1.Asset;
import com.google.cloud.asset.v1.Resource;
import com.google.protobuf.Value;
import sn.intouch.pfe.cmdbdataprocess.mapping.MappingEngine;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class VMBuilder {
    public static VirtualMachine vmBuilder (Asset asset, String projectId) throws IOException {
        String name = MappingEngine.getName(asset);
        name = MappingEngine.getRealValue(name);
        Resource resource = MappingEngine.getResource(asset);
        String location = MappingEngine.getLocation(resource);
        Map<String, Value> fields = MappingEngine.getFields(resource);
        String zone = MappingEngine.getStringValue(fields,"zone");
        zone = MappingEngine.getRealValue(zone);
        String machineType = MappingEngine.getStringValue(fields,"machineType");
        machineType = MappingEngine.getRealValue(machineType);
        List<Value> networkInterfaces = MappingEngine.getListValue(fields,"networkInterfaces");
        List<Value> accessConfigs = networkInterfaces.get(0).getStructValue().getFieldsMap().get("accessConfigs") !=null ? networkInterfaces.get(0).getStructValue().getFieldsMap().get("accessConfigs").getListValue().getValuesList() : Collections.emptyList();
        String natIP = !accessConfigs.isEmpty() ? accessConfigs.get(0).getStructValue().getFieldsMap().get("natIP").getStringValue() : "";
        String privateIP = networkInterfaces.get(0).getStructValue().getFieldsMap().get("networkIP").getStringValue();
        String subnet = networkInterfaces.get(0).getStructValue().getFieldsMap().get("subnetwork").getStringValue();
        String status = MappingEngine.getStringValue(fields,"status");
        List<Value> disks = MappingEngine.getListValue(fields,"disks");
        String disk = disks.get(0).getStructValue().getFieldsMap().get("source").getStringValue();
        return VirtualMachine.builder()
                .name(name)
                .zone(zone)
                .machineType(machineType)
                .privateIP(privateIP)
                .status(status)
                .natIP(natIP)
                .subnet(subnet)
                .diskName(disk)
                .instanceGroupName(MappingEngine.getInstanceRelationships(projectId,asset))
                .build();
    }
}
