package sn.intouch.pfe.cmdbdataprocess.mapping;

import com.google.cloud.asset.v1.*;
import com.google.protobuf.Descriptors;
import lombok.extern.log4j.Log4j2;
import sn.intouch.pfe.cmdbdataprocess.entities.disk.Disk;
import sn.intouch.pfe.cmdbdataprocess.entities.disk.DiskBuilder;
import sn.intouch.pfe.cmdbdataprocess.entities.network.Subnet;
import sn.intouch.pfe.cmdbdataprocess.entities.network.SubnetBuilder;
import sn.intouch.pfe.cmdbdataprocess.entities.network.VPCBuilder;
import sn.intouch.pfe.cmdbdataprocess.entities.network.VirtualPrivateCloud;
import sn.intouch.pfe.cmdbdataprocess.entities.project.Project;
import sn.intouch.pfe.cmdbdataprocess.entities.project.ProjectBuilder;
import sn.intouch.pfe.cmdbdataprocess.entities.serverless.CloudFunction;
import sn.intouch.pfe.cmdbdataprocess.entities.serverless.CloudFunctionBuilder;
import sn.intouch.pfe.cmdbdataprocess.utils.CloudAssetAuthUtil;
import sn.intouch.pfe.cmdbdataprocess.utils.AssetMapping;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@Log4j2
public class GCPTestv1 {

    public static void listAssets() throws IOException, IllegalArgumentException {
        String projectId = "eme-iacc";
        String[] assetTypes = {
                //AssetMapping.IP_ADDRESSES,
                AssetMapping.DISK,
                //AssetMapping.FORWARDING_RULE,
                //AssetMapping.CLOUD_RUN_EXECUTION,
                AssetMapping.VPC,
                //AssetMapping.CLOUD_SQL,
                //AssetMapping.VM_INSTANCE,
                AssetMapping.PROJECT,
                AssetMapping.SUBNETWORK,
                //AssetMapping.VM_IMAGE,
                //AssetMapping.TARGET_HTTPS_PROXY,
                AssetMapping.CLOUD_FUNCTION,
                //AssetMapping.BACKEND_SERVICE,
                //AssetMapping.APP_ENGINE_APPLICATION,
                //AssetMapping.ClOUD_SQL_BACKUP,
                //AssetMapping.TARGET_TCP_PROXY,
                //AssetMapping.TARGET_HTTP_PROXY,
                //AssetMapping.INSTANCE_TEMPLATE,
                //AssetMapping.ROUTER,
                //AssetMapping.URLMAP,
                //AssetMapping.INSTANCE_GROUP,
                //AssetMapping.VPN_GATEWAY,
                //AssetMapping.VPN_TUNNEL
        };

        ContentType contentType = ContentType.RESOURCE;
        listAssets(projectId, assetTypes, contentType);
    }

    public static void listAssets(String projectId, String[] assetTypes, ContentType contentType)
            throws IOException, IllegalArgumentException {
        try (AssetServiceClient client = CloudAssetAuthUtil.getAssetServiceClient()) {
            ProjectName parent = ProjectName.of(projectId);

            ListAssetsRequest request =
                    ListAssetsRequest.newBuilder()
                            .setParent(parent.toString())
                            .addAllAssetTypes(Arrays.asList(assetTypes))
                            .setContentType(contentType)
                            .setPageSize(312) //312

                            .build();
            AssetServiceClient.ListAssetsPagedResponse response = client.listAssets(request);

            for (Asset asset : response.getPage().getValues()) {
                String type = MappingEngine.getAssetType(asset);
                String realType = MappingEngine.getRealValue(type);
                log.info("Asset type : " + realType);
                if (Objects.equals(realType, "Function")){
                    CloudFunction cloudFunction = CloudFunctionBuilder.cloudFunctionBuilder(asset);
                    log.info("name : " + cloudFunction.getName());
                    log.info("region : " + cloudFunction.getRegion());
                    log.info("url : " + cloudFunction.getUrl());
                    log.info("ingress : " + cloudFunction.getIngress());
                    log.info("egress : " + cloudFunction.getEgress());
                }
                if(Objects.equals(realType, "Disk")){
                    Disk disk = DiskBuilder.diskBuilder(asset);
                    log.info("name: " + disk.getName());
                    log.info("type: " + disk.getArchitecture());
                    log.info("size: " + disk.getSize());
                    log.info("region: " + disk.getRegion());
                }
                if(Objects.equals(realType, "Subnetwork")){
                    Subnet subnet = SubnetBuilder.subnetBuilder(asset);
                    log.info("range : " + subnet.getRange());
                    log.info("region : " + subnet.getRegion());
                    log.info("purpose : " + subnet.getPurpose());
                    log.info("vpcName : " + subnet.getVPCName());
                }

                if(Objects.equals(realType, "Project")){
                    Project project = ProjectBuilder.projectBuilder(asset);
                    log.info("projectId : " + project.getProjectID());
                    log.info("projectName : " + project.getProjectName());
                }

                if(Objects.equals(realType, "Network")){
                    VirtualPrivateCloud virtualPrivateCloud = VPCBuilder.vpcBuilder(asset);
                    log.info("Description: " + virtualPrivateCloud.getDescription());
                    log.info("Name: " + virtualPrivateCloud.getName());
                    log.info("firewallRule: " + virtualPrivateCloud.getFirewallRules());
                    log.info("routingMode: " + virtualPrivateCloud.getRoutingMode());
                    log.info("projectName: " + virtualPrivateCloud.getProjectName());
                }
                log.info("-------------------------------------------------");
            }
            }


        }

    public static void main(String[] args) throws IOException {
        listAssets();
    }


}
