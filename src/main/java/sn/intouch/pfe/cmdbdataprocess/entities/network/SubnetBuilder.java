package sn.intouch.pfe.cmdbdataprocess.entities.network;

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
import java.util.List;
import java.util.Map;


@Log4j2
public class SubnetBuilder {
    public static Subnet subnetBuilder(Asset asset, String projectId) throws IOException, JSONException {
        String[] assetNames = {asset.getName()};
        if(History.toUpdate(projectId, List.of(assetNames))){
            Resource resource = MappingEngine.getResource(asset);
            Map<String, Value> fields = MappingEngine.getFields(resource);
            String range = MappingEngine.getStringValue(fields,"ipCidrRange");
            String location = MappingEngine.getLocation(resource);
            String purpose = MappingEngine.getStringValue(fields,"purpose");
            String vpcName = MappingEngine.getStringValue(fields, "network");
            String name = MappingEngine.getName(asset).split("regions/")[1];
            vpcName = MappingEngine.getRealValue(vpcName );

            String url = Config.baseUrl + "classes/Subnet/cards";

            String body = "{"
                    + "\"range\": \"" + range + "\","
                    + "\"name\": \"" + name + "\","
                    + "\"region\": \"" + location + "\","
                    + "\"purpose\": \"" + purpose + "\""
                    + "}";

            Integer cardId = HttpUtil.getCardId("Subnet","range",range);

            HttpUtil.saveOrUpdate(range,url,body,cardId);

            return Subnet.builder()
                    .range(range)
                    .region(location)
                    .purpose(purpose)
                    .VPCName(vpcName)
                    .name(name)
                    .build();
        }
        return null;
    }
}
