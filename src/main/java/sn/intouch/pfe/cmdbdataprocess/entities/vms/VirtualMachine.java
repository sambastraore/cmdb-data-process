package sn.intouch.pfe.cmdbdataprocess.entities.vms;

import lombok.Getter;
import lombok.Setter;
import sn.intouch.pfe.cmdbdataprocess.entities.network.Subnet;

import java.util.Set;

@Getter
@Setter
public class VirtualMachine {
    private String name;
    private String region;
    private String zone;
    private String machineType;
    private String image;
    private String networkTag;
    private String startupScript;
    private String privateIP;
    private String status;

    //relationship
    private Subnet subnet;

    //relationship
    private String IPAddress;

    //relationship
    private Set<String> resourcesDeployed;

    //relationship
    private Set<String> VMsLinked;

    //relationship
    private String instanceGroupName;

    //relationship
    private String diskName;
}
