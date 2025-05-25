package sn.intouch.pfe.cmdbdataprocess.entities.serverless;

import lombok.*;
import sn.intouch.pfe.cmdbdataprocess.entities.nat.NAT;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CloudRun {
    private String name;
    private String region;
    private String url;
    private String ram;
    private String cpu;
    private String appID;
    private String networkInformations;
    //relationship
    private NAT nat;

    //relationship
    private String cloudSQLInstance;
}
