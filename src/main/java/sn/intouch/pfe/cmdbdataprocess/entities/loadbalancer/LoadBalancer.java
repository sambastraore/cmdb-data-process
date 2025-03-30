package sn.intouch.pfe.cmdbdataprocess.entities.loadbalancer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class LoadBalancer {
    private String name;
    private String region;
    private Boolean ssl;
    //private Map<String,String> firewallRules;

    private List<String> targetProxies;
    private List<String> backends;
    private String description;


    //relationship
    //List<RoutingRule> routingRules;
}
