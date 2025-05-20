package sn.intouch.pfe.cmdbdataprocess.entities.project;

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

public class ProjectBuilder {

    public static Project projectBuilder (Asset asset, String projectId) throws IOException, JSONException {
        String[] assetNames = {asset.getName()};
        if (History.toUpdate(projectId, List.of(assetNames))){
            Resource resource = MappingEngine.getResource(asset);
            Map<String, Value> fields = MappingEngine.getFields(resource);
            Map<String, Value> struct = MappingEngine.getStructValue(fields,"parent");
            String idProject = MappingEngine.getStringValue(struct,"id");
            String projectName = MappingEngine.getStringValue(fields,"name").toLowerCase();

            String url = Config.baseUrl + "classes/Project/cards";
            String body = "{"
                    + "\"projectId\": \"" + idProject + "\","
                    + "\"projectName\": \"" + projectName + "\""
                    + "}";

            Integer cardId = HttpUtil.getCardId("Project","projectName",projectName);

            HttpUtil.saveOrUpdate(idProject,url,body,cardId);
            return Project.builder()
                    .projectID(idProject)
                    .projectName(projectName)
                    .build();
        }
        return null;
    }
}
