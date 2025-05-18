package sn.intouch.pfe.cmdbdataprocess.entities.wildfly;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.configurationprocessor.json.JSONException;
import sn.intouch.pfe.cmdbdataprocess.utils.HttpUtil;
import sn.intouch.pfe.cmdbdataprocess.utils.RelationshipMapping;

import java.io.IOException;
import java.util.Objects;


@Log4j2
public class WildflyRelation {
    public static void updateWildflyRelation (String nameValue) throws JSONException, IOException {
        //String name = Objects.requireNonNull(HttpUtil.getCard("Wildfly", nameValue)).getString("name");
        String datasources = Objects.requireNonNull(HttpUtil.getCard("Wildfly", nameValue)).getString("datasources");
        //String deployments = Objects.requireNonNull(HttpUtil.getCard("Wildfly", nameValue)).getString("deployments");
        //String subsystems = Objects.requireNonNull(HttpUtil.getCard("Wildfly", nameValue)).getString("subsystems");

        //wildfly for vm relation
        Integer relationId = HttpUtil.getRelationId(RelationshipMapping.WILDFLY_FOR_VM.split("Map_")[1],"VirtualMachine","name",nameValue.split("_")[1],"Wildfly","name",nameValue);
        HttpUtil.saveOrUpdateRelation(RelationshipMapping.WILDFLY_FOR_VM.split("Map_")[1],relationId,HttpUtil.getCardId("Wildfly","name",nameValue),"Wildfly",HttpUtil.getCardId("VirtualMachine","name",nameValue.split("_")[1]),"VirtualMachine");

        // mysql or cloudsql for wildfly relation
        String [] urls = HttpUtil.getDatasourceUrl(datasources);
        for (String url : urls) {
            String[] ipAndPort = HttpUtil.extractIpAndPort(url);
            String ip = ipAndPort != null ? ipAndPort[0] : null;
            String port = ipAndPort != null ? ipAndPort[1] : null;
            String VMName = HttpUtil.getVMFromIP(ip);
            String CloudSQLName = HttpUtil.getCloudSQLFromIP(ip);
            String mySQLPort = HttpUtil.getPortFromMySQL("mysql_" + VMName);
            if (Objects.equals(ip, "127.0.0.1") || Objects.equals(ip, "localhost")) {
                Integer relationId1 = HttpUtil.getRelationId(RelationshipMapping.WILDFLY_FOR_MYSQL.split("Map_")[1], "Wildfly", "name", nameValue, "MySQL", "name", "mysql_" + nameValue.split("_")[1]);
                HttpUtil.saveOrUpdateRelation(RelationshipMapping.WILDFLY_FOR_MYSQL.split("Map_")[1], relationId1, HttpUtil.getCardId("MySQL", "name", "mysql_" + nameValue.split("_")[1]), "MySQL", HttpUtil.getCardId("Wildfly", "name", nameValue), "Wildfly");
            } else if (VMName != null && Objects.equals(mySQLPort, port)) {
                Integer relationId1 = HttpUtil.getRelationId(RelationshipMapping.WILDFLY_FOR_MYSQL.split("Map_")[1], "Wildfly", "name", nameValue, "MySQL", "name", "mysql_" + VMName);
                HttpUtil.saveOrUpdateRelation(RelationshipMapping.WILDFLY_FOR_MYSQL.split("Map_")[1], relationId1, HttpUtil.getCardId("MySQL", "name", "mysql_" + VMName), "MySQL", HttpUtil.getCardId("Wildfly", "name", nameValue), "Wildfly");
            } else if (CloudSQLName != null) {
                Integer relationId1 = HttpUtil.getRelationId(RelationshipMapping.WILDFLY_FOR_CLOUDSQL.split("Map_")[1], "Wildfly", "name", nameValue, "CloudSQL", "name", CloudSQLName);
                HttpUtil.saveOrUpdateRelation(RelationshipMapping.WILDFLY_FOR_CLOUDSQL.split("Map_")[1], relationId1, HttpUtil.getCardId("CloudSQL", "name", CloudSQLName), "CloudSQL", HttpUtil.getCardId("Wildfly", "name", nameValue), "Wildfly");
            }
        }

        // wildfly for apache





    }




    public static void main(String[] args) throws JSONException, IOException {
        updateWildflyRelation("wildfly_apidist-template");





        //log.info(HttpUtil.extractIps(vhosts).get("localhost"));

    }

}
