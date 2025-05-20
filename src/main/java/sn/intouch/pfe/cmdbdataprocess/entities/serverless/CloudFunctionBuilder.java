package sn.intouch.pfe.cmdbdataprocess.entities.serverless;

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

public class CloudFunctionBuilder {

    public static CloudFunction cloudFunctionBuilder (Asset asset, String projectId) throws IOException, JSONException {
        String name = MappingEngine.getName(asset);
        String[] assetNames = {name};
        if(History.toUpdate(projectId, List.of(assetNames))){
            String realName = MappingEngine.getRealValue(name);
            Resource resource = MappingEngine.getResource(asset);
            String location = MappingEngine.getLocation(resource);
            Map<String, Value> fields = MappingEngine.getFields(resource);
            String url = MappingEngine.getStringValue(fields,"url");
            Map<String, Value> serviceConfig = MappingEngine.getStructValue(fields,"serviceConfig");
            String ingressSettings = MappingEngine.getStringValue(serviceConfig,"ingressSettings");
            String egressSettings = MappingEngine.getStringValue(serviceConfig,"vpcConnectorEgressSettings");
            String appId = MappingEngine.getFunctionsAppId(serviceConfig);

            String urlREST = Config.baseUrl + "classes/CloudFunction/cards";
            String body = "{"
                    + "\"name\": \"" + realName + "\","
                    + "\"region\": \"" + location + "\","
                    + "\"url\": \"" + url + "\","
                    + "\"ingress\": \"" + ingressSettings + "\","
                    + "\"egress\": \"" + egressSettings + "\","
                    + "\"appId\": \"" + appId + "\""
                    + "}";

            Integer cardId = HttpUtil.getCardId("CloudFunction","name",realName);

            HttpUtil.saveOrUpdate(realName,urlREST,body,cardId);

            return CloudFunction.builder()
                    .name(realName)
                    .region(location)
                    .url(url)
                    .ingress(ingressSettings)
                    .egress(egressSettings)
                    .appID(appId)
                    .build();
        }
        return null;
    }
}
