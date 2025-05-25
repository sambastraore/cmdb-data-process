package sn.intouch.pfe.cmdbdataprocess.entities.cloudsql;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.configurationprocessor.json.JSONException;
import sn.intouch.pfe.cmdbdataprocess.entities.network.Subnet;
import sn.intouch.pfe.cmdbdataprocess.utils.HttpUtil;
import sn.intouch.pfe.cmdbdataprocess.utils.RelationshipMapping;

import java.io.IOException;
@Log4j2
public class CloudSQLRelation {
    public static void updateCloudSQLRelation (CloudSQL cloudSQL) throws JSONException, IOException {
        HttpUtil.deleteRelation(RelationshipMapping.CLOUDSQL_VPC_RELATION.split("Map_")[1],"VirtualPrivateCloud","name",cloudSQL.getVpc().split("projects/")[1],"CloudSQL","name",cloudSQL.getName());
        Integer relationId = HttpUtil.getRelationId(RelationshipMapping.CLOUDSQL_VPC_RELATION.split("Map_")[1],"VirtualPrivateCloud","name",cloudSQL.getVpc().split("projects/")[1],"CloudSQL","name",cloudSQL.getName());
        HttpUtil.saveOrUpdateRelation(RelationshipMapping.CLOUDSQL_VPC_RELATION.split("Map_")[1],relationId,HttpUtil.getCardId("CloudSQL","name",cloudSQL.getName()),"CloudSQL",HttpUtil.getCardId("VirtualPrivateCloud","name",cloudSQL.getVpc().split("projects/")[1]),"VirtualPrivateCloud");
    }
}
