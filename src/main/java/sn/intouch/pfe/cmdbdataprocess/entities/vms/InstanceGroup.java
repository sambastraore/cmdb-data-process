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
    private String template; // dispo avec .relationship
    private String region;
    private Set<String> zones;
    private Integer minInstances; //non dispo
    private Integer maxInstances; //non dispo
    private String status; // non dispo
    private Boolean managed; // dispo avec .relationship
    private Boolean autoscaling;
    private Boolean autohealing;

    //relationship
    private String vpc;

    //relationship
    private String subnetwork;

}
