package sn.intouch.pfe.cmdbdataprocess.entities.serverless;

import org.springframework.boot.configurationprocessor.json.JSONException;
import sn.intouch.pfe.cmdbdataprocess.utils.HttpUtil;
import sn.intouch.pfe.cmdbdataprocess.utils.RelationshipMapping;

import java.io.IOException;

public class CloudRunRelation {
    public static void updateCloudRunRelation (CloudRun cloudRun) throws JSONException, IOException {
        Integer relationId = HttpUtil.getRelationId(RelationshipMapping.CLOUDRUN_CLOUDSQL_RELATION.split("Map_")[1],"CloudSQL","name",cloudRun.getCloudSQLInstance(),"CloudRun","name",cloudRun.getName());
        HttpUtil.saveOrUpdateRelation(RelationshipMapping.CLOUDRUN_CLOUDSQL_RELATION.split("Map_")[1],relationId,HttpUtil.getCardId("CloudRun","name",cloudRun.getName()),"CloudRun",HttpUtil.getCardId("CloudSQL","name",cloudRun.getCloudSQLInstance()),"CloudSQL");
    }
}
