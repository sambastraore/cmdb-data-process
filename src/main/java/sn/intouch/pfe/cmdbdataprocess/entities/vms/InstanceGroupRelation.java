package sn.intouch.pfe.cmdbdataprocess.entities.vms;

import org.springframework.boot.configurationprocessor.json.JSONException;
import sn.intouch.pfe.cmdbdataprocess.entities.serverless.CloudRun;
import sn.intouch.pfe.cmdbdataprocess.utils.HttpUtil;
import sn.intouch.pfe.cmdbdataprocess.utils.RelationshipMapping;

import java.io.IOException;

public class InstanceGroupRelation {
    public static void updateInstanceGroupRelationForVPC (InstanceGroup instanceGroup) throws JSONException, IOException {
        HttpUtil.deleteRelation(RelationshipMapping.IG_VPC_RELATION.split("Map_")[1],"VirtualPrivateCloud","name",instanceGroup.getVpc(),"InstanceGroup","name",instanceGroup.getName());
        Integer relationId = HttpUtil.getRelationId(RelationshipMapping.IG_VPC_RELATION.split("Map_")[1],"VirtualPrivateCloud","name",instanceGroup.getVpc(),"InstanceGroup","name",instanceGroup.getName());
        HttpUtil.saveOrUpdateRelation(RelationshipMapping.IG_VPC_RELATION.split("Map_")[1],relationId,HttpUtil.getCardId("InstanceGroup","name",instanceGroup.getName()),"InstanceGroup",HttpUtil.getCardId("VirtualPrivateCloud","name",instanceGroup.getVpc()),"VirtualPrivateCloud");
    }

    public static void updateInstanceGroupRelationForSubnet (InstanceGroup instanceGroup) throws JSONException, IOException {
        HttpUtil.deleteRelation(RelationshipMapping.IG_SUBNET_RELATION.split("Map_")[1],"Subnet","name",instanceGroup.getSubnetwork(),"InstanceGroup","name",instanceGroup.getName());
        Integer relationId = HttpUtil.getRelationId(RelationshipMapping.IG_SUBNET_RELATION.split("Map_")[1],"Subnet","name",instanceGroup.getSubnetwork(),"InstanceGroup","name",instanceGroup.getName());
        HttpUtil.saveOrUpdateRelation(RelationshipMapping.IG_SUBNET_RELATION.split("Map_")[1],relationId,HttpUtil.getCardId("InstanceGroup","name",instanceGroup.getName()),"InstanceGroup",HttpUtil.getCardId("Subnet","name",instanceGroup.getSubnetwork()),"Subnet");
    }
}
