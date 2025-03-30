package sn.intouch.pfe.cmdbdataprocess.entities.loadbalancer;

import com.google.cloud.asset.v1.Asset;
import com.google.cloud.asset.v1.Resource;
import com.google.protobuf.Value;
import sn.intouch.pfe.cmdbdataprocess.mapping.MappingEngine;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class LoadBalancerBuilder {
    public static LoadBalancer loadBalancerBuilder (Asset urlMap, String projectId) throws IOException {
        String name = MappingEngine.getName(urlMap);
        name = MappingEngine.getRealValue(name);
        Resource resource = MappingEngine.getResource(urlMap);
        String location = MappingEngine.getLocation(resource);
        Map<String, Value> fields = MappingEngine.getFields(resource);
        Map<String, List<String>> components = MappingEngine.getLoadBalancerRelationships(projectId,urlMap);
        List<String> backends = components.get("backends");
        List<String> frontends = components.get("frontends");
        String description = MappingEngine.getStringValue(fields,"description");

        return LoadBalancer.builder()
                .name(name)
                .region(location)
                .backends(backends)
                .targetProxies(frontends)
                .description(description)
                .build();
    }

}
