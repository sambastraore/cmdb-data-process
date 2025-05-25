package sn.intouch.pfe.cmdbdataprocess.controllers;

import com.google.cloud.asset.v1.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.intouch.pfe.cmdbdataprocess.dtos.ResponseWrapper;
import sn.intouch.pfe.cmdbdataprocess.entities.apache.ApacheRelation;
import sn.intouch.pfe.cmdbdataprocess.entities.cloudsql.CloudSQL;
import sn.intouch.pfe.cmdbdataprocess.entities.cloudsql.CloudSQLBuilder;
import sn.intouch.pfe.cmdbdataprocess.entities.cloudsql.CloudSQLRelation;
import sn.intouch.pfe.cmdbdataprocess.entities.disk.Disk;
import sn.intouch.pfe.cmdbdataprocess.entities.disk.DiskBuilder;
import sn.intouch.pfe.cmdbdataprocess.entities.loadbalancer.LoadBalancer;
import sn.intouch.pfe.cmdbdataprocess.entities.loadbalancer.LoadBalancerBuilder;
import sn.intouch.pfe.cmdbdataprocess.entities.mysql.MySQLRelation;
import sn.intouch.pfe.cmdbdataprocess.entities.network.*;
import sn.intouch.pfe.cmdbdataprocess.entities.project.Project;
import sn.intouch.pfe.cmdbdataprocess.entities.project.ProjectBuilder;
import sn.intouch.pfe.cmdbdataprocess.entities.serverless.*;
import sn.intouch.pfe.cmdbdataprocess.entities.vms.*;
import sn.intouch.pfe.cmdbdataprocess.entities.wildfly.WildflyRelation;
import sn.intouch.pfe.cmdbdataprocess.services.CmdbService;
import sn.intouch.pfe.cmdbdataprocess.services.PurgeService;
import sn.intouch.pfe.cmdbdataprocess.utils.CloudAssetAuthUtil;
import sn.intouch.pfe.cmdbdataprocess.utils.AssetMapping;
import sn.intouch.pfe.cmdbdataprocess.utils.HttpUtil;

import java.io.IOException;
import java.util.*;

@Log4j2
@RestController
@RequestMapping("/cmdb")
@RequiredArgsConstructor
public class PurgeTrigger {

    private final PurgeService purgeService;
    @PostMapping("/purge")
    public ResponseEntity<ResponseWrapper<Integer>> purgeCmdb(){
        try{
            purgeAssets();
            ResponseWrapper<Integer> responseWrapper = new ResponseWrapper<>("Success while purging CMDB !",200,0);
            return ResponseEntity.ok(responseWrapper);
        } catch (Exception e) {
            ResponseWrapper<Integer> responseWrapper = new ResponseWrapper<>("Error while purging CMDB ! : " + e,500,-1);
            return ResponseEntity.internalServerError().body(responseWrapper);
        }
    }




    public void purgeAssets() throws IOException, IllegalArgumentException, JSONException, InterruptedException {
        //String projectId = "eme-iacc";
        //String projectId = "dev-top20";
        String [] projectIds = {"eme-iacc",
                "dev-top20"
        };
        String[] assetTypes = {
                //AssetMapping.IP_ADDRESSES,
                AssetMapping.DISK,
                //AssetMapping.FORWARDING_RULE,
                AssetMapping.CLOUD_RUN_EXECUTION,
                AssetMapping.VPC,
                AssetMapping.CLOUD_SQL,
                AssetMapping.VM_INSTANCE,
                AssetMapping.PROJECT,
                AssetMapping.SUBNETWORK,
                //AssetMapping.VM_IMAGE,
                //AssetMapping.TARGET_HTTPS_PROXY,
                AssetMapping.CLOUD_FUNCTION,
                //AssetMapping.BACKEND_SERVICE,
                //AssetMapping.APP_ENGINE_APPLICATION,
                //AssetMapping.ClOUD_SQL_BACKUP,
                AssetMapping.CLOUD_RUN_SERVICE,
                //AssetMapping.TARGET_HTTP_PROXY,
                //AssetMapping.INSTANCE_TEMPLATE,
                //AssetMapping.ROUTER,
                AssetMapping.URLMAP,
                AssetMapping.INSTANCE_GROUP,
                //AssetMapping.VPN_GATEWAY,
                //AssetMapping.VPN_TUNNEL
        };
        ContentType contentType = ContentType.RESOURCE;
        purgeService.deleteAssets(projectIds, assetTypes, contentType);
    }
//pour faire purge  vm resources mieux vaut fixer une date "lastSeen" et supprimer. Au pire des cas le script les recrée
}
