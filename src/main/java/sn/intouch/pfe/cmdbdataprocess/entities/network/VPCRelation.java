package sn.intouch.pfe.cmdbdataprocess.entities.network;

import org.springframework.boot.configurationprocessor.json.JSONException;
import sn.intouch.pfe.cmdbdataprocess.entities.vms.VirtualMachine;
import sn.intouch.pfe.cmdbdataprocess.utils.HttpUtil;
import sn.intouch.pfe.cmdbdataprocess.utils.RelationshipMapping;

import java.io.IOException;

public class VPCRelation {
    public static void updateProjectRelation (VirtualPrivateCloud vpc) throws JSONException, IOException {
        HttpUtil.deleteRelation(RelationshipMapping.VPC_PROJECT_RELATION.split("Map_")[1],"Project","projectName",vpc.getProjectName(),"VirtualPrivateCloud","name",vpc.getName());
        Integer relationId = HttpUtil.getRelationId(RelationshipMapping.VPC_PROJECT_RELATION.split("Map_")[1],"Project","projectName",vpc.getProjectName(),"VirtualPrivateCloud","name",vpc.getName());
        HttpUtil.saveOrUpdateRelation(RelationshipMapping.VPC_PROJECT_RELATION.split("Map_")[1],relationId,HttpUtil.getCardId("VirtualPrivateCloud","name",vpc.getName()),"VirtualPrivateCloud",HttpUtil.getCardId("Project","projectName", vpc.getProjectName()),"Project");
    }
}
