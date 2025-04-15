package sn.intouch.pfe.cmdbdataprocess.entities.cloudsql;

import org.springframework.boot.configurationprocessor.json.JSONException;
import sn.intouch.pfe.cmdbdataprocess.entities.network.Subnet;
import sn.intouch.pfe.cmdbdataprocess.utils.HttpUtil;
import sn.intouch.pfe.cmdbdataprocess.utils.RelationshipMapping;

import java.io.IOException;

public class CloudSQLRelation {
    public static void updateCloudSQLRelation (CloudSQL cloudSQL) throws JSONException, IOException {
        Integer relationId = HttpUtil.getRelationId(RelationshipMapping.CLOUDSQL_VPC_RELATION.split("Map_")[1],"VirtualPrivateCloud","name",cloudSQL.getVpc(),"CloudSQL","name",cloudSQL.getName());
        HttpUtil.saveOrUpdateRelation(RelationshipMapping.CLOUDSQL_VPC_RELATION.split("Map_")[1],relationId,HttpUtil.getCardId("CloudSQL","name",cloudSQL.getName()),"CloudSQL",HttpUtil.getCardId("VirtualPrivateCloud","name",cloudSQL.getVpc()),"VirtualPrivateCloud");
    }
}
