package sn.intouch.pfe.cmdbdataprocess.entities.apache;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class Apache {
    private Set<String> modulesEnabled;
    private Map<String,String> generalConfigs;
    //sites enabled

    //relationship
    private String VMName;
}
