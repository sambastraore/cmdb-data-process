package sn.intouch.pfe.cmdbdataprocess.entities.loadbalancer;

import com.google.cloud.asset.v1.Asset;
import com.google.cloud.asset.v1.Resource;
import com.google.protobuf.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import sn.intouch.pfe.cmdbdataprocess.mapping.HistoryTest;
import sn.intouch.pfe.cmdbdataprocess.mapping.MappingEngine;
import sn.intouch.pfe.cmdbdataprocess.utils.Config;
import sn.intouch.pfe.cmdbdataprocess.utils.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadBalancerBuilder {
    public static LoadBalancer loadBalancerBuilder (Asset urlMap, String projectId) throws IOException, JSONException {
        List<String> assetNames = new ArrayList<>();
        String name = MappingEngine.getName(urlMap);
        assetNames.add(name);
        Map<String, List<String>> components = MappingEngine.getLoadBalancerRelationships(projectId,urlMap);
        List<String> backends = components.get("backends");
        assetNames.addAll(backends);
        List<String> frontends = components.get("frontends");
        assetNames.addAll(frontends);
        if(HistoryTest.toUpdate(projectId,assetNames)){
            name = MappingEngine.getRealValue(name);
            Resource resource = MappingEngine.getResource(urlMap);
            String location = MappingEngine.getLocation(resource);
            Map<String, Value> fields = MappingEngine.getFields(resource);
            String description = MappingEngine.getStringValue(fields,"description");
            String body = "{"
                    + "\"name\": \"" + name + "\","
                    + "\"region\": \"" + location + "\""
                    + "}";

            String url = Config.baseUrl + "classes/LoadBalancer/cards";
            Integer cardId = HttpUtil.getCardId("LoadBalancer","name",name);

            HttpUtil.saveOrUpdate(name,url,body,cardId);

            return LoadBalancer.builder()
                    .name(name)
                    .region(location)
                    .backends(backends)
                    .targetProxies(frontends)
                    .description(description)
                    .build();
        }
        return null;
    }
}
