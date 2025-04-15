package sn.intouch.pfe.cmdbdataprocess.entities.network;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subnet {
    private String name;
    private String range;
    private String region;
    private String purpose;

    //relationship
    private String VPCName;
}
