package sn.intouch.pfe.cmdbdataprocess.entities.disk;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Disk {
    private String name;
    private String architecture;
    private String size;
    private String region;
}
