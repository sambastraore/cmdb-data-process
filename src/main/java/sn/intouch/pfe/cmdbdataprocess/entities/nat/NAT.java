package sn.intouch.pfe.cmdbdataprocess.entities.nat;

import lombok.Getter;
import lombok.Setter;
import sn.intouch.pfe.cmdbdataprocess.entities.network.Subnet;

@Getter
@Setter
public class NAT {
    private String gatewayName;
    private String cloudRouter;
    private Integer numberOfPorts;

    // relationship
    private Subnet subnet;

    //relationship
    private String IPAddress;


}
