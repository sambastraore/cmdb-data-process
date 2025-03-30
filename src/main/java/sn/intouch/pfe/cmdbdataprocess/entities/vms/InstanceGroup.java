package sn.intouch.pfe.cmdbdataprocess.entities.vms;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class InstanceGroup {
    private String name;
    private String description;
    private String template;
    private String region;
    private Integer minInstances;
    private Integer maxInstances;
    private String status;
    private String manager;
    private Boolean autoscaling;

    //relationship
    private String vpc;

    //relationship
    private String subnetwork;

}
