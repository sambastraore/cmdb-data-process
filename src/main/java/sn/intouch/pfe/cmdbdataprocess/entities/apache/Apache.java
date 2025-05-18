package sn.intouch.pfe.cmdbdataprocess.entities.apache;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class Apache {
    private String name;
    private String serverName;
    private String port;
    private String user;
    private String group;
    private String modulesEnabled;
    private String vhosts;

    //relationship
    private String VMName;
}
