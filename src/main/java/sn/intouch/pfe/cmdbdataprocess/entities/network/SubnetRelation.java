package sn.intouch.pfe.cmdbdataprocess.entities.network;

import org.springframework.boot.configurationprocessor.json.JSONException;
import sn.intouch.pfe.cmdbdataprocess.entities.serverless.CloudRun;
import sn.intouch.pfe.cmdbdataprocess.utils.HttpUtil;
import sn.intouch.pfe.cmdbdataprocess.utils.RelationshipMapping;

import java.io.IOException;

public class SubnetRelation {
    public static void updateSubnetRelation (Subnet subnet) throws JSONException, IOException {
        Integer relationId = HttpUtil.getRelationId(RelationshipMapping.SUBNET_VPC_RELATION.split("Map_")[1],"VirtualPrivateCloud","name",subnet.getVPCName(),"Subnet","range",subnet.getRange());
        HttpUtil.saveOrUpdateRelation(RelationshipMapping.SUBNET_VPC_RELATION.split("Map_")[1],relationId,HttpUtil.getCardId("Subnet","range",subnet.getRange()),"Subnet",HttpUtil.getCardId("VirtualPrivateCloud","name",subnet.getVPCName()),"VirtualPrivateCloud");
    }
}
