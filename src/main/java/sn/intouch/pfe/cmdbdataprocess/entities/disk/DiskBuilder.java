package sn.intouch.pfe.cmdbdataprocess.entities.disk;

import com.google.cloud.asset.v1.Asset;
import com.google.cloud.asset.v1.Resource;
import com.google.protobuf.Value;
import sn.intouch.pfe.cmdbdataprocess.mapping.MappingEngine;

import java.util.Map;

public class DiskBuilder {
    public static Disk diskBuilder (Asset asset){
        String name = MappingEngine.getName(asset);
        String realName = MappingEngine.getRealValue(name);
        Resource resource = MappingEngine.getResource(asset);
        Map<String, Value> fields = MappingEngine.getFields(resource);
        String size = MappingEngine.getStringValue(fields, "sizeGb");
        String type = MappingEngine.getStringValue(fields,"architecture");
        String location = MappingEngine.getLocation(resource);
        return Disk.builder()
                .name(realName)
                .architecture(type)
                .size(size)
                .region(location)
                .build();
    }
}
