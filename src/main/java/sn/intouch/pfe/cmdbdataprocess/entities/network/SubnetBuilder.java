package sn.intouch.pfe.cmdbdataprocess.entities.network;

import com.google.cloud.asset.v1.Asset;
import com.google.cloud.asset.v1.Resource;
import com.google.protobuf.Value;
import sn.intouch.pfe.cmdbdataprocess.mapping.MappingEngine;

import java.util.Map;

public class SubnetBuilder {
    public static Subnet subnetBuilder(Asset asset){
        Resource resource = MappingEngine.getResource(asset);
        Map<String, Value> fields = MappingEngine.getFields(resource);
        String range = MappingEngine.getStringValue(fields,"ipCidrRange");
        String location = MappingEngine.getLocation(resource);
        String purpose = MappingEngine.getStringValue(fields,"purpose");
        String vpcName = MappingEngine.getStringValue(fields, "network");
        return Subnet.builder()
                .range(range)
                .region(location)
                .purpose(purpose)
                .VPCName(vpcName)
                .build();
    }
}
