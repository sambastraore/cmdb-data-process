package sn.intouch.pfe.cmdbdataprocess.entities.cloudsql;

import lombok.Getter;
import lombok.Setter;
import sn.intouch.pfe.cmdbdataprocess.entities.network.Subnet;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class CloudSQL {
    private String name;
    private String region;
    private String databaseVersion;
    private String machineType;
    private Map<String,String> characteristics;
    private Boolean backup;
    private String privateIP;
    private String replicaName;
    private Set<String> ingress;

    //relationship
    private String IPAddress;

    //relationship
    private Subnet subnet;

    //relationship
    private Set<String> serverlessNames;

    //relationship
    private Set<String> VMsLinked;
}
