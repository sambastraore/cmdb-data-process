package sn.intouch.pfe.cmdbdataprocess.entities.project;

import com.google.cloud.asset.v1.Asset;
import com.google.cloud.asset.v1.Resource;
import com.google.protobuf.Value;
import sn.intouch.pfe.cmdbdataprocess.mapping.MappingEngine;

import java.util.Map;

public class ProjectBuilder {

    public static Project projectBuilder (Asset asset){
        Resource resource = MappingEngine.getResource(asset);
        Map<String, Value> fields = MappingEngine.getFields(resource);
        Map<String, Value> struct = MappingEngine.getStructValue(fields,"parent");
        String projectId = MappingEngine.getStringValue(struct,"id");
        String projectName = MappingEngine.getStringValue(fields,"name");
        return Project.builder()
                .projectID(projectId)
                .projectName(projectName)
                .build();
    }
}
