package sn.intouch.pfe.cmdbdataprocess.entities.disk;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Disk {
    private String name;
    private String architecture;
    private String size;
    private String region;
}
