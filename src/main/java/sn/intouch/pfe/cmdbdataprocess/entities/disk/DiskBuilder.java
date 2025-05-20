package sn.intouch.pfe.cmdbdataprocess.entities.disk;

import com.google.cloud.asset.v1.Asset;
import com.google.cloud.asset.v1.Resource;
import com.google.protobuf.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import sn.intouch.pfe.cmdbdataprocess.mapping.History;
import sn.intouch.pfe.cmdbdataprocess.mapping.MappingEngine;
import sn.intouch.pfe.cmdbdataprocess.utils.Config;
import sn.intouch.pfe.cmdbdataprocess.utils.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DiskBuilder {
    public static Disk diskBuilder (Asset asset, String projectId) throws IOException, JSONException {
        String name = MappingEngine.getName(asset);
        List<String> assetNames = new ArrayList<>();
        assetNames.add(name);
        if (History.toUpdate(projectId,assetNames)){
            String realName = MappingEngine.getRealValue(name);
            Resource resource = MappingEngine.getResource(asset);
            Map<String, Value> fields = MappingEngine.getFields(resource);
            String size = MappingEngine.getStringValue(fields, "sizeGb");
            String type = MappingEngine.getStringValue(fields,"architecture");
            String location = MappingEngine.getLocation(resource);
            String body = "{"
                    + "\"name\": \"" + realName + "\","
                    + "\"region\": \"" + location + "\","
                    + "\"size\": " + size + ","
                    + "\"architecture\": \"" + type + "\""
                    + "}";
            String url = Config.baseUrl + "classes/Disk/cards";
            Integer cardId = HttpUtil.getCardId("Disk","name",realName);
            HttpUtil.saveOrUpdate(realName,url,body,cardId);
            return Disk.builder()
                    .name(realName)
                    .architecture(type)
                    .size(size)
                    .region(location)
                    .build();
        }
        return null;
    }
}
