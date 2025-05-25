package sn.intouch.pfe.cmdbdataprocess.controllers;

import com.google.cloud.asset.v1.Asset;
import com.google.cloud.asset.v1.AssetServiceClient;
import com.google.cloud.asset.v1.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.intouch.pfe.cmdbdataprocess.dtos.ResponseWrapper;
import sn.intouch.pfe.cmdbdataprocess.services.AuditService;
import sn.intouch.pfe.cmdbdataprocess.services.CmdbService;
import sn.intouch.pfe.cmdbdataprocess.utils.AssetMapping;
import sn.intouch.pfe.cmdbdataprocess.utils.LastUpdatedAudit;

import java.io.IOException;

@Log4j2
@RestController
@RequestMapping("/cmdb")
@RequiredArgsConstructor
public class AuditTrigger {
    private final AuditService auditService;



    @PostMapping("/audit")
    public ResponseEntity<ResponseWrapper<Integer>> auditCmdb(){
        try{
            auditAssets();
            ResponseWrapper<Integer> responseWrapper = new ResponseWrapper<>("Success while linking CMDB with principals !",200,0);
            return ResponseEntity.ok(responseWrapper);
        } catch (Exception e) {
            ResponseWrapper<Integer> responseWrapper = new ResponseWrapper<>("Error while linking CMDB principals ! : " + e,500,-1);
            return ResponseEntity.internalServerError().body(responseWrapper);
        }
    }




    String [] projectIds = {"eme-iacc",
            //"dev-top20"
    };
    public void auditAssets() throws IOException {
        String[] assetTypes = {
                //AssetMapping.IP_ADDRESSES,
                //AssetMapping.DISK,
                //AssetMapping.FORWARDING_RULE,
                //AssetMapping.CLOUD_RUN_EXECUTION,
                //AssetMapping.CLOUD_RUN_JOB
                AssetMapping.CLOUD_RUN_SERVICE,
                //AssetMapping.VPC,
                AssetMapping.CLOUD_SQL,
                AssetMapping.VM_INSTANCE,
                //AssetMapping.PROJECT,
                //AssetMapping.SUBNETWORK,
                //AssetMapping.VM_IMAGE,
                //AssetMapping.TARGET_HTTPS_PROXY,
                //AssetMapping.CLOUD_FUNCTION,
                //AssetMapping.BACKEND_SERVICE,
                //AssetMapping.APP_ENGINE_APPLICATION,
                //AssetMapping.ClOUD_SQL_BACKUP,
                //AssetMapping.TARGET_HTTP_PROXY,
                //AssetMapping.INSTANCE_TEMPLATE,
                //AssetMapping.ROUTER,
                //AssetMapping.URLMAP,
                AssetMapping.INSTANCE_GROUP,
                //AssetMapping.VPN_GATEWAY,
                //AssetMapping.VPN_TUNNEL
        };
        ContentType contentType = ContentType.RESOURCE;
        auditService.auditAssets(projectIds, assetTypes, contentType);
    }


}
