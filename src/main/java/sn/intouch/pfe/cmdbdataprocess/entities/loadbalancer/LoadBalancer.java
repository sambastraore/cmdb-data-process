package sn.intouch.pfe.cmdbdataprocess.entities.loadbalancer;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class LoadBalancer {
    private String name;
    private String type;
    private String region;
    private Boolean global;
    private Map<String,String> firewallRules;


    //relationship
    List<RoutingRule> routingRules;
}
