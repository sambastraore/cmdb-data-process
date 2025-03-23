package sn.intouch.pfe.cmdbdataprocess.entities.loadbalancer;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoutingRule {
    List<String> frontendIPs;
    List<String> backends;
}
