package sn.intouch.pfe.cmdbdataprocess.entities.mysql;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MySQL {
    private String name;
    private String port;
    private String socket;
    private String datadir;
    private String slowQueryLogFile;
    private String maxConnections;
    private String maxUserConnections;
    private String threadCacheSize;
    private String readBufferSize;
    private String maxAllowedPacket;

    //relationship
    private String VMName;
}
