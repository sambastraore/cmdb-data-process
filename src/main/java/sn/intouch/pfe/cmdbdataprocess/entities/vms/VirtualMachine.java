package sn.intouch.pfe.cmdbdataprocess.entities.vms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import sn.intouch.pfe.cmdbdataprocess.entities.network.Subnet;

import java.util.Set;

@Getter
@Setter
@Builder
public class VirtualMachine {
    private String name;
    private String zone;
    private String machineType;
    private String image;
    private String networkTag;
    //private String startupScript;
    private String privateIP;
    private String status;
    private String natIP;

    //relationship
    //private Subnet subnet;
    private String subnet;


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
