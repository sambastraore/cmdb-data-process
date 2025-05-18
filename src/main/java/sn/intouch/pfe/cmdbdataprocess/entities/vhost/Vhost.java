package sn.intouch.pfe.cmdbdataprocess.entities.vhost;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vhost {
    private String servername;
    private String ssl;
    private String serverAlias;
    private String errorLog;
    private String lb;
    private String proxy;
    private String rewrite;
}
