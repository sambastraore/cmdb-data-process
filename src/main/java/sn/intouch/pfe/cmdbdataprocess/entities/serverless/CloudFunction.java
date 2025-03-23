package sn.intouch.pfe.cmdbdataprocess.entities.serverless;

import lombok.*;
import sn.intouch.pfe.cmdbdataprocess.entities.nat.NAT;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudFunction {
    private String name;
    private String region;
    private String url;
    private String appID;
    private String ingress;
    private String egress;

    //relationship
    private NAT nat;
}
