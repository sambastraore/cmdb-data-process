package sn.intouch.pfe.cmdbdataprocess.entities.wildfly;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Wildfly {
    private String name;
    private String datasources;
    private String deployments;
    private String subsystems;

    //relationship
    private String VMName;



}
