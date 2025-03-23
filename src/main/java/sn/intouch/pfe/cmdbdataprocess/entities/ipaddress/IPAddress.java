package sn.intouch.pfe.cmdbdataprocess.entities.ipaddress;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IPAddress {
    private String value;
    private Boolean ephemeral;
    private String url;
}
