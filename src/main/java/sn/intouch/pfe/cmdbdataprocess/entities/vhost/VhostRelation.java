package sn.intouch.pfe.cmdbdataprocess.entities.vhost;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.configurationprocessor.json.JSONException;
import sn.intouch.pfe.cmdbdataprocess.utils.HttpUtil;
import sn.intouch.pfe.cmdbdataprocess.utils.RelationshipMapping;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;


@Log4j2
public class VhostRelation {
    public static void updateVhostRelation(String nameValue) throws JSONException, IOException {


        //vhost for apache relation
        Integer relationId = HttpUtil.getRelationId(RelationshipMapping.VHOST_FOR_APACHE.split("Map_")[1],"Apache","name",nameValue.split("_vhost[0-9]+")[0],"Vhost","name",nameValue);
        HttpUtil.saveOrUpdateRelation(RelationshipMapping.VHOST_FOR_APACHE.split("Map_")[1],relationId,HttpUtil.getCardId("Vhost","name",nameValue),"Vhost",HttpUtil.getCardId("Apache","name",nameValue.split("_vhost[0-9]+")[0]),"Apache");

        //vhost for wildfly
        String proxy = Objects.requireNonNull(HttpUtil.getCard("Vhost", nameValue)).getString("proxy");
        String lb = Objects.requireNonNull(HttpUtil.getCard("Vhost", nameValue)).getString("lb");


        Map<String,String> ips = HttpUtil.extractIps(lb);
        if (ips.isEmpty()){
            ips.putAll(HttpUtil.extractIps(proxy));
        }

        for (Map.Entry<String, String> entry : ips.entrySet()) {
            String VMName = HttpUtil.getVMFromIP(entry.getKey());


            if (Objects.equals(entry.getKey(), "127.0.0.1") || Objects.equals(entry.getKey(), "localhost")){
                Integer relationId1 = HttpUtil.getRelationId(RelationshipMapping.VHOST_FOR_WILDFLY.split("Map_")[1], "Wildfly", "name", "wildfly_"+nameValue.split("_vhost[0-9]+")[0].split("_")[1], "Vhost", "name", nameValue);
                HttpUtil.saveOrUpdateRelation(RelationshipMapping.VHOST_FOR_WILDFLY.split("Map_")[1], relationId1, HttpUtil.getCardId("Vhost", "name", nameValue), "Vhost", HttpUtil.getCardId("Wildfly", "name", "wildfly_"+nameValue.split("_vhost[0-9]+")[0].split("_")[1]), "Wildfly");
            }
            else if (VMName != null){
                log.info(VMName);
                Integer relationId1 = HttpUtil.getRelationId(RelationshipMapping.VHOST_FOR_WILDFLY.split("Map_")[1], "Wildfly", "name", "wildfly_"+VMName, "Vhost", "name", nameValue);
                HttpUtil.saveOrUpdateRelation(RelationshipMapping.VHOST_FOR_WILDFLY.split("Map_")[1], relationId1, HttpUtil.getCardId("Vhost", "name", nameValue), "Vhost", HttpUtil.getCardId("Wildfly", "name", "wildfly_"+VMName), "Wildfly");
            }
        }








        }

    public static void main(String[] args) throws JSONException, IOException {
        updateVhostRelation("apache_apidist-template_vhost1");

        //for (Map.Entry<String, String> entry : ips.entrySet()) {
        //    System.out.println("Key : " + entry.getKey() + ", Value : " + entry.getValue());
        //}

    }
}
