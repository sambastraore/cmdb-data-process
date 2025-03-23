package sn.intouch.pfe.cmdbdataprocess.entities.network;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Subnet {
    private String range;
    private String region;
    private String purpose;

    //relationship
    private String VPCName;
}
