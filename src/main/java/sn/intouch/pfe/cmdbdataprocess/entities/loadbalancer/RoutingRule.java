package sn.intouch.pfe.cmdbdataprocess.entities.loadbalancer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class RoutingRule {
    List<String> frontends;
    List<String> backends;
}
