package sn.intouch.pfe.cmdbdataprocess.entities.serverless;

import com.google.cloud.asset.v1.Asset;
import com.google.cloud.asset.v1.Resource;
import com.google.protobuf.Value;
import sn.intouch.pfe.cmdbdataprocess.mapping.MappingEngine;

import java.util.List;
import java.util.Map;

public class CloudRunBuilder {
    public static CloudRun cloudRunBuilder(Asset asset){
        String name = MappingEngine.getName(asset);
        name = MappingEngine.getRealValue(name);
        Resource resource = MappingEngine.getResource(asset);
        String location = MappingEngine.getLocation(resource);
        Map<String, Value> fields = MappingEngine.getFields(resource);
        Map<String, Value> status = MappingEngine.getStructValue(fields, "status");
        String url = MappingEngine.getStringValue(status,"logUri");
        Map<String, Value> metadata = MappingEngine.getStructValue(fields,"metadata");
        List<Value> ownerReferences = MappingEngine.getListValue(metadata,"ownerReferences");
        String kind = ownerReferences.get(0).getStructValue().getFieldsMap().get("kind").getStringValue();
        Map<String,Value> spec = MappingEngine.getStructValue(fields,"spec");
        Map<String,Value> template = MappingEngine.getStructValue(spec,"template");
        Double maxRetries = template.get("spec").getStructValue().getFieldsMap().get("maxRetries").getNumberValue();
        String cpu = template.get("spec").getStructValue().getFieldsMap().get("containers").getListValue().getValuesList().get(0).getStructValue().getFieldsMap().get("resources").getStructValue().getFieldsMap().get("limits").getStructValue().getFieldsMap().get("cpu").getStringValue();
        String memory = template.get("spec").getStructValue().getFieldsMap().get("containers").getListValue().getValuesList().get(0).getStructValue().getFieldsMap().get("resources").getStructValue().getFieldsMap().get("limits").getStructValue().getFieldsMap().get("memory").getStringValue();
        String sqlInstance = template.get("spec").getStructValue().getFieldsMap().get("containers").getListValue().getValuesList().get(0).getStructValue().getFieldsMap().get("env").getListValue().getValuesList().get(3).getStructValue().getFieldsMap().get("value").getStringValue();
        return CloudRun.builder()
                .name(name)
                .region(location)
                .url(url)
                .kind(kind)
                .maxRetries(maxRetries)
                .cpu(cpu)
                .ram(memory)
                .cloudSQLInstance(sqlInstance)
                .build();
    }
}
