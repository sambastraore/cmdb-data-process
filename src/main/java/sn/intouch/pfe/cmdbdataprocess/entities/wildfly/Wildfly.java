package sn.intouch.pfe.cmdbdataprocess.entities.wildfly;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Wildfly {
    private List<Map<String,String>> datasources;
    private String version;
    private Integer port;
    private List<String> apps;
    //io
    //jca
    //jvmConfig

    //relationship
    private String VMName;



}
