package sn.intouch.pfe.cmdbdataprocess.entities.vms;

import com.google.cloud.asset.v1.Asset;
import com.google.cloud.asset.v1.Resource;
import com.google.protobuf.Value;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.configurationprocessor.json.JSONException;
import sn.intouch.pfe.cmdbdataprocess.mapping.History;
import sn.intouch.pfe.cmdbdataprocess.mapping.MappingEngine;
import sn.intouch.pfe.cmdbdataprocess.utils.Config;
import sn.intouch.pfe.cmdbdataprocess.utils.HttpUtil;

import java.io.IOException;
import java.util.*;

@Log4j2
public class VMBuilder {
    public static VirtualMachine vmBuilder (Asset asset, String projectId) throws IOException, JSONException {
        String name = MappingEngine.getName(asset);
        String instanceGroupName = MappingEngine.getInstanceRelationships(projectId,asset);
        List<String> assetNames = new ArrayList<>();
        assetNames.add(name);
        if (!Objects.equals("",instanceGroupName))
            assetNames.add(instanceGroupName);
        if (History.toUpdate(projectId,assetNames)){
            name = name.split("projects/")[1];
            Resource resource = MappingEngine.getResource(asset);
            Map<String, Value> fields = MappingEngine.getFields(resource);
            String zone = MappingEngine.getStringValue(fields,"zone");
            zone = MappingEngine.getRealValue(zone);
            String machineType = MappingEngine.getStringValue(fields,"machineType");
            machineType = MappingEngine.getRealValue(machineType);
            List<Value> networkInterfaces = MappingEngine.getListValue(fields,"networkInterfaces");
            List<Value> accessConfigs = networkInterfaces.get(0).getStructValue().getFieldsMap().get("accessConfigs") !=null ? networkInterfaces.get(0).getStructValue().getFieldsMap().get("accessConfigs").getListValue().getValuesList() : Collections.emptyList();
            Map<String,Value> fieldsMap = !accessConfigs.isEmpty() ? accessConfigs.get(0).getStructValue().getFieldsMap():null;
            Value natValue = fieldsMap != null ? fieldsMap.get("natIP") : null;
            String natIP = natValue != null ? natValue.getStringValue() : "";
            String privateIP = networkInterfaces.get(0).getStructValue().getFieldsMap().get("networkIP").getStringValue();
            String subnet = networkInterfaces.get(0).getStructValue().getFieldsMap().get("subnetwork").getStringValue();
            String status = MappingEngine.getStringValue(fields,"status");
            List<Value> disks = MappingEngine.getListValue(fields,"disks");
            String disk = disks.get(0).getStructValue().getFieldsMap().get("source").getStringValue();
            //disk = MappingEngine.getRealValue(disk);

            String url = Config.baseUrl + "classes/VirtualMachine/cards";
            String body = "{"
                    + "\"name\": \"" + name + "\","
                    + "\"zone\": \"" + zone + "\","
                    + "\"machineType\": \"" + machineType + "\","
                    + "\"privateIP\": \"" + privateIP + "\","
                    + "\"status\": \"" + status + "\","
                    + "\"natIP\": \"" + natIP + "\""
                    + "}";

            Integer cardId = HttpUtil.getCardId("VirtualMachine","name",name);

            HttpUtil.saveOrUpdate(name,url,body,cardId);

            return VirtualMachine.builder()
                    .name(name)
                    .zone(zone)
                    .machineType(machineType)
                    .privateIP(privateIP)
                    .status(status)
                    .natIP(natIP)
                    .subnet(subnet)
                    .diskName(disk)
                    .instanceGroupName(instanceGroupName)
                    .build();
        }
        return null;
    }

    public static void main(String[] args) {

    }
}
