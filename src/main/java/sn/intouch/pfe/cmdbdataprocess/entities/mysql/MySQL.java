package sn.intouch.pfe.cmdbdataprocess.entities.mysql;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MySQL {
    private Integer numberOfConnections;
    private Integer numberOfThreads;
    private String logsExpirations;
    private String version;
    private Integer port;
    private Integer activeConnections;

    //retrieve all information in the .conf file and eventually add fields

    //relationship
    private String VMName;
}
