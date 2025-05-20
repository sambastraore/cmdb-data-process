package sn.intouch.pfe.cmdbdataprocess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CmdbDataProcessApplication {

    public static void main(String[] args) {

        SpringApplication.run(CmdbDataProcessApplication.class, args);
    }

}
