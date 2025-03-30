package sn.intouch.pfe.cmdbdataprocess.entities.cloudsql;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import sn.intouch.pfe.cmdbdataprocess.entities.network.Subnet;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Builder
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

    //relationship
    private String IPAddress;

    //relationship
    private String vpc;

    //relationship
    private Set<String> serverlessNames;

    //relationship
    private Set<String> VMsLinked;
}
