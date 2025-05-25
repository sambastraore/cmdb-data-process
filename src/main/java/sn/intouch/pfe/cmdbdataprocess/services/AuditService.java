package sn.intouch.pfe.cmdbdataprocess.services;

import com.google.cloud.asset.v1.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sn.intouch.pfe.cmdbdataprocess.mapping.MappingEngine;
import sn.intouch.pfe.cmdbdataprocess.utils.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Service
@Log4j2
public class AuditService {

    @Async
    public void auditAssets(String[] projectIds, String[] assetTypes, ContentType contentType)
            throws IOException, IllegalArgumentException {
        try (AssetServiceClient client = CloudAssetAuthUtil.getAssetServiceClient()) {
            for (String idProject : projectIds){
                ProjectName parent = ProjectName.of(idProject);
                ListAssetsRequest request =
                        ListAssetsRequest.newBuilder()
                                .setParent(parent.toString())
                                .addAllAssetTypes(Arrays.asList(assetTypes))
                                .setContentType(contentType)
                                .setPageSize(1000)

                                .build();
                AssetServiceClient.ListAssetsPagedResponse response = client.listAssets(request);
                responseProcessing(response,idProject);

                while (!response.getNextPageToken().isEmpty()) {
                    request = request.toBuilder().setPageToken(response.getNextPageToken()).build();
                    response = client.listAssets(request);
                    responseProcessing(response,idProject);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void responseProcessing(AssetServiceClient.ListAssetsPagedResponse response, String projectId) throws InterruptedException, IOException, JSONException {
        Map<String, Map<String, String>> mapAudit =  LastUpdatedAudit.getLastUpdatedAudits("projects/" + projectId);
        for (Asset asset : response.getPage().getValues()) {
            try {
                String type = MappingEngine.getAssetType(asset);
                log.info("type : " + type);
                String cmdbName = asset.getName().split(".com/projects/")[1];
                String name = "projects/" + cmdbName;

                type = switch (type) {
                    case (AssetMapping.CLOUD_RUN_SERVICE) -> "CloudRun";
                    case (AssetMapping.CLOUD_SQL) -> "CloudSQL";
                    case (AssetMapping.VM_INSTANCE) -> "VirtualMachine";
                    case (AssetMapping.INSTANCE_GROUP) -> "InstanceGroup";
                    default -> type;
                };
                String urlREST = Config.baseUrl + "classes/" + type + "/cards";
                String principal = mapAudit.get(name) != null ? mapAudit.get(name).get("user") : "";
                String date = mapAudit.get(name) != null ? mapAudit.get(name).get("date") : "";



                JSONObject card = HttpUtil.getCard(type,cmdbName);
                Integer cardId = card.getInt("_id");
                String body = generateRequestBody(type,cmdbName,principal,date,card);

                HttpUtil.saveOrUpdate(name, urlREST, body, cardId);


            }catch (Exception e){
                log.error("error while auditing : " + e);
            }
    }
}

    private static String generateRequestBody(String type, String cmdbName, String principal, String date, JSONObject jsonObject) throws JSONException {
        JSONObject body = new JSONObject();
        body.put("name", cmdbName);
        body.put("lastUpdatedBy", principal + ", " + date);

        switch (type) {
            case "CloudRun" -> {
                body.put("region",jsonObject.getString("region"));
                body.put("url",jsonObject.getString("url"));
                body.put("ram",jsonObject.getString("ram"));
                body.put("cpu",jsonObject.getString("cpu"));
                body.put("cloudSQLInstance",jsonObject.has("cloudSQLInstance")  ? jsonObject.getString("cloudSQLInstance") : "");
            }
            case "CloudSQL" -> {
                body.put("region",jsonObject.getString("region"));
                body.put("databaseVersion",jsonObject.getString("databaseVersion"));
                body.put("backup",jsonObject.getBoolean("backup"));
                body.put("replication",jsonObject.getString("replication"));
                body.put("IPAddress",jsonObject.getString("IPAddress"));
                body.put("characteristics",jsonObject.getString("characteristics"));
                body.put("privateIP",jsonObject.getString("privateIP"));
            }
            case "VirtualMachine" -> {
                body.put("zone",jsonObject.getString("zone"));
                body.put("machineType",jsonObject.getString("machineType"));
                body.put("privateIP",jsonObject.getString("privateIP"));
                body.put("status",jsonObject.getString("status"));
                body.put("natIP",jsonObject.getString("natIP"));
            }
            case "InstanceGroup" -> {
                body.put("region",jsonObject.getString("region"));
                body.put("manager",jsonObject.getString("manager"));
            }
        }

        return body.toString();
    }

}
