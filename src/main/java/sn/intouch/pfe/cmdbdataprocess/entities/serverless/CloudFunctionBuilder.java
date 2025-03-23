package sn.intouch.pfe.cmdbdataprocess.entities.serverless;

import com.google.cloud.asset.v1.Asset;
import com.google.cloud.asset.v1.Resource;
import com.google.protobuf.Value;
import sn.intouch.pfe.cmdbdataprocess.mapping.MappingEngine;

import java.util.Map;

public class CloudFunctionBuilder {

    public static CloudFunction cloudFunctionBuilder (Asset asset){
        String name = MappingEngine.getName(asset);
        String realName = MappingEngine.getRealValue(name);
        Resource resource = MappingEngine.getResource(asset);
        String location = MappingEngine.getLocation(resource);
        Map<String, Value> fields = MappingEngine.getFields(resource);
        String url = MappingEngine.getStringValue(fields,"url");
        Map<String, Value> serviceConfig = MappingEngine.getStructValue(fields,"serviceConfig");
        String ingressSettings = MappingEngine.getStringValue(serviceConfig,"ingressSettings");
        String egressSettings = MappingEngine.getStringValue(serviceConfig,"vpcConnectorEgressSettings");
        String appId = MappingEngine.getFunctionsAppId(serviceConfig);

        return CloudFunction.builder()
                .name(realName)
                .region(location)
                .url(url)
                .ingress(ingressSettings)
                .egress(egressSettings)
                .appID(appId)
                .build();
    }
}
