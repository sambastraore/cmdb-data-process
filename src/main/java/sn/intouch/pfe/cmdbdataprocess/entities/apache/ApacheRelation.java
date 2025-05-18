package sn.intouch.pfe.cmdbdataprocess.entities.apache;

import org.springframework.boot.configurationprocessor.json.JSONException;
import sn.intouch.pfe.cmdbdataprocess.utils.HttpUtil;
import sn.intouch.pfe.cmdbdataprocess.utils.RelationshipMapping;

import java.io.IOException;
import java.util.Objects;

public class ApacheRelation {
    public static void updateApacheRelation (String nameValue) throws JSONException, IOException {
        //String name = Objects.requireNonNull(HttpUtil.getCard("Apache", nameValue)).getString("name");
        //String serverName = Objects.requireNonNull(HttpUtil.getCard("Apache", nameValue)).getString("serverName");
        //String port = Objects.requireNonNull(HttpUtil.getCard("Apache", nameValue)).getString("port");
        //String user = Objects.requireNonNull(HttpUtil.getCard("Apache", nameValue)).getString("user");
        //String group = Objects.requireNonNull(HttpUtil.getCard("Apache", nameValue)).getString("group");
        //String modulesEnabled = Objects.requireNonNull(HttpUtil.getCard("Apache", nameValue)).getString("modulesEnabled");
        //String vhosts = Objects.requireNonNull(HttpUtil.getCard("Apache", nameValue)).getString("vhosts");

        //apache for vm relation
        Integer relationId = HttpUtil.getRelationId(RelationshipMapping.APACHE_FOR_VM.split("Map_")[1],"VirtualMachine","name",nameValue.split("_")[1],"Apache","name",nameValue);
        HttpUtil.saveOrUpdateRelation(RelationshipMapping.APACHE_FOR_VM.split("Map_")[1],relationId,HttpUtil.getCardId("Apache","name",nameValue),"Apache",HttpUtil.getCardId("VirtualMachine","name",nameValue.split("_")[1]),"VirtualMachine");
    }

    public static void main(String[] args) throws JSONException, IOException {
        updateApacheRelation("apache_apidist-template");
    }
}
