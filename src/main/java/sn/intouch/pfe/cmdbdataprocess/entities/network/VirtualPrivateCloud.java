package sn.intouch.pfe.cmdbdataprocess.entities.network;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VirtualPrivateCloud {
    private String name;
    //private Map<String,String> firewallRules;
    private String firewallRules;
    private String description;
    private String routingMode;

    //relationship
    private String projectName;
}
