package sn.intouch.pfe.cmdbdataprocess.entities.mysql;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.configurationprocessor.json.JSONException;
import sn.intouch.pfe.cmdbdataprocess.utils.HttpUtil;
import sn.intouch.pfe.cmdbdataprocess.utils.RelationshipMapping;

import java.io.IOException;
import java.util.Objects;

@Log4j2
public class MySQLRelation {
    public static void updateMySQLRelation (String nameValue) throws JSONException, IOException {
        //String name = Objects.requireNonNull(HttpUtil.getCard("MySQL", nameValue)).getString("name");
        //String port = Objects.requireNonNull(HttpUtil.getCard("MySQL", nameValue)).getString("port");
        //String socket = Objects.requireNonNull(HttpUtil.getCard("MySQL", nameValue)).getString("socket");
        //String datadir = Objects.requireNonNull(HttpUtil.getCard("MySQL", nameValue)).getString("datadir");
        //String slowQueryLogFile = Objects.requireNonNull(HttpUtil.getCard("MySQL", nameValue)).getString("slowQueryLogFile");
        //String maxConnections = Objects.requireNonNull(HttpUtil.getCard("MySQL", nameValue)).getString("maxConnections");
        //String maxUserConnections = Objects.requireNonNull(HttpUtil.getCard("MySQL", nameValue)).getString("maxUserConnections");
        //String threadCacheSize = Objects.requireNonNull(HttpUtil.getCard("MySQL", nameValue)).getString("threadCacheSize");
        //String readBufferSize = Objects.requireNonNull(HttpUtil.getCard("MySQL", nameValue)).getString("readBufferSize");
        //String maxAllowedPacket = Objects.requireNonNull(HttpUtil.getCard("MySQL", nameValue)).getString("maxAllowedPacket");


        //mysql for vm relation
        Integer relationId = HttpUtil.getRelationId(RelationshipMapping.MYSQL_FOR_VM.split("Map_")[1],"VirtualMachine","name",nameValue.split("_").length>1 ? nameValue.split("_")[1] : "","MySQL","name",nameValue);
        HttpUtil.saveOrUpdateRelation(RelationshipMapping.MYSQL_FOR_VM.split("Map_")[1],relationId,HttpUtil.getCardId("MySQL","name",nameValue),"MySQL",HttpUtil.getCardId("VirtualMachine","name",nameValue.split("_").length>1 ? nameValue.split("_")[1] : ""),"VirtualMachine");
    }

    public static void main(String[] args) throws JSONException, IOException {
        updateMySQLRelation("mysql_apidist-template");
    }
}
