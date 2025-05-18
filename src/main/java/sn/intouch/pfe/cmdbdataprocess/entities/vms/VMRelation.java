package sn.intouch.pfe.cmdbdataprocess.entities.vms;

import org.springframework.boot.configurationprocessor.json.JSONException;
import sn.intouch.pfe.cmdbdataprocess.utils.HttpUtil;
import sn.intouch.pfe.cmdbdataprocess.utils.RelationshipMapping;

import java.io.IOException;
import java.util.Objects;

public class VMRelation {
    public static void updateDiskRelation (VirtualMachine vm) throws JSONException, IOException {
        Integer relationId = HttpUtil.getRelationId(RelationshipMapping.VM_FOR_DISK.split("Map_")[1],"Disk","name",vm.getDiskName(),"VirtualMachine","name",vm.getName());
        HttpUtil.saveOrUpdateRelation(RelationshipMapping.VM_FOR_DISK.split("Map_")[1],relationId,HttpUtil.getCardId("VirtualMachine","name",vm.getName()),"VirtualMachine",HttpUtil.getCardId("Disk","name",vm.getDiskName()),"Disk");
    }

    public static void updateInstanceGroupRelation (VirtualMachine vm) throws JSONException, IOException {
        Integer relationId = HttpUtil.getRelationId(RelationshipMapping.VM_IG_RELATION.split("Map_")[1],"InstanceGroup","name", !Objects.equals(vm.getInstanceGroupName(), "") ? vm.getInstanceGroupName().split("instanceGroups/")[1]:"","VirtualMachine","name",vm.getName());
        HttpUtil.saveOrUpdateRelation(RelationshipMapping.VM_IG_RELATION.split("Map_")[1],relationId,HttpUtil.getCardId("VirtualMachine","name",vm.getName()),"VirtualMachine",HttpUtil.getCardId("InstanceGroup","name", !Objects.equals(vm.getInstanceGroupName(), "") ? vm.getInstanceGroupName().split("instanceGroups/")[1]:""),"InstanceGroup");
    }

    public static void updateSubnetRelation (VirtualMachine vm) throws JSONException, IOException {
        System.out.println("testtesttest : " + vm.getSubnet());
        Integer relationId = HttpUtil.getRelationId(RelationshipMapping.SUBNET_FOR_VM.split("Map_")[1],"Subnet","name", (vm.getSubnet() != null && !vm.getSubnet().isEmpty()) ? vm.getSubnet().split("regions/")[1]:"","VirtualMachine","name",vm.getName());
        HttpUtil.saveOrUpdateRelation(RelationshipMapping.SUBNET_FOR_VM.split("Map_")[1],relationId,HttpUtil.getCardId("VirtualMachine","name",vm.getName()),"VirtualMachine",HttpUtil.getCardId("Subnet","name",  (vm.getSubnet() != null && !vm.getSubnet().isEmpty()) ? vm.getSubnet().split("regions/")[1]:""),"Subnet");
    }
}
