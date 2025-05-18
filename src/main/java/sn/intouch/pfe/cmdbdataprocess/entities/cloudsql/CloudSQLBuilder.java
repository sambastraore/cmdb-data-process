package sn.intouch.pfe.cmdbdataprocess.entities.cloudsql;

import com.google.cloud.asset.v1.Asset;
import com.google.cloud.asset.v1.Resource;
import com.google.protobuf.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import sn.intouch.pfe.cmdbdataprocess.mapping.HistoryTest;
import sn.intouch.pfe.cmdbdataprocess.mapping.MappingEngine;
import sn.intouch.pfe.cmdbdataprocess.utils.Config;
import sn.intouch.pfe.cmdbdataprocess.utils.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloudSQLBuilder {
    public static CloudSQL cloudSQLBuilder (Asset asset, String projectId) throws IOException, JSONException {
        String name = MappingEngine.getName(asset);
        List<String> assetNames = new ArrayList<>();
        assetNames.add(name);
        if (HistoryTest.toUpdate(projectId,assetNames)){
            Resource resource = MappingEngine.getResource(asset);
            Map<String, Value> fields = MappingEngine.getFields(resource);
            name = MappingEngine.getRealValue(name);
            String region = MappingEngine.getLocation(resource);
            String databaseVersion = MappingEngine.getStringValue(fields,"databaseInstalledVersion");
            List<Value> ipAddresses = MappingEngine.getListValue(fields,"ipAddresses");
            String ipAddress = ipAddresses.get(0).getStructValue().getFieldsMap().get("ipAddress").getStringValue();
            String privateIP = "";
            if (ipAddresses.size()>1)
                privateIP = ipAddresses.get(1).getStructValue().getFieldsMap().get("ipAddress").getStringValue();
            String replica = MappingEngine.getStructValue(fields,"replicationCluster").toString();
            Map<String,Value> settings = MappingEngine.getStructValue(fields,"settings");
            Boolean backup = settings.get("backupConfiguration").getStructValue().getFieldsMap().get("enabled").getBoolValue();
            Map<String, Value> ipConfigs = MappingEngine.getStructValue(settings, "ipConfiguration");
            String vpc = ipConfigs.get("privateNetwork") != null ? ipConfigs.get("privateNetwork").getStringValue() : "";
            vpc = MappingEngine.getRealValue(vpc);
            String availabilityType = settings.get("availabilityType").getStringValue();
            String activationPolicy = settings.get("activationPolicy").getStringValue();
            String diskSize = settings.get("dataDiskSizeGb").getStringValue();
            String diskType = settings.get("dataDiskType").getStringValue();
            String maxConnections = settings.containsKey("databaseFlags") ? settings.get("databaseFlags").getListValue().getValuesList().get(0).getStructValue().getFieldsMap().get("value").getStringValue() : ""; // avec ce style, on obtient aussi pour innodb_log_buffer_size, tmp_table_size et max_heap_table_size
            Map<String,String> characteristics = new HashMap<>();
            characteristics.put("availabiltyType",availabilityType);
            characteristics.put("activationPolicy",activationPolicy);
            characteristics.put("diskSize",diskSize);
            characteristics.put("diskType",diskType);
            characteristics.put("maxConnections",maxConnections);


            String url = Config.baseUrl + "classes/CloudSQL/cards";
            String body = "{"
                    + "\"name\": \"" + name + "\","
                    + "\"region\": \"" + region + "\","
                    + "\"databaseVersion\": \"" + databaseVersion + "\","
                    + "\"backup\": " + backup + ","
                    + "\"replication\": \"" + replica + "\","
                    + "\"IPAddress\": \"" + ipAddress + "\","
                    + "\"characteristics\": \"" + characteristics + "\","
                    + "\"privateIP\": \"" + privateIP + "\""
                    + "}";

            Integer cardId = HttpUtil.getCardId("CloudSQL","name",name);

            HttpUtil.saveOrUpdate(name,url,body,cardId);
            return CloudSQL.builder()
                    .name(name)
                    .vpc(vpc)
                    .backup(backup)
                    .replication(replica)
                    .privateIP(privateIP)
                    .IPAddress(ipAddress)
                    .databaseVersion(databaseVersion)
                    .region(region)
                    .characteristics(characteristics)
                    .build();
        }
        return null;
        }
}
