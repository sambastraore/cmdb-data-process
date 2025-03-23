package sn.intouch.pfe.cmdbdataprocess.entities.network;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class VirtualPrivateCloud {
    private String name;
    //private Map<String,String> firewallRules;
    private String firewallRules;
    private String description;
    private String routingMode;

    //relationship
    private String projectName;
}
