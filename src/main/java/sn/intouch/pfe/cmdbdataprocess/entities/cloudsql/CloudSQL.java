package sn.intouch.pfe.cmdbdataprocess.entities.cloudsql;

import lombok.*;
import sn.intouch.pfe.cmdbdataprocess.entities.network.Subnet;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudSQL {
    private String name;
    private String region;
    private String databaseVersion;
    private String machineType;
    private Map<String,String> characteristics;
    private Boolean backup;
    private String privateIP;
    private String replication;
    private Set<String> ingress;

    //relationship --not necessarily
    private String IPAddress;

    //relationship
    private String vpc; //ok

    //relationship
    private Set<String> serverlessNames; //ok from cloud run perspective

    //relationship
    private Set<String> VMsLinked; // okay from vm perspective
}
