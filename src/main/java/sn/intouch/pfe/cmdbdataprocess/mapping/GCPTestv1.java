package sn.intouch.pfe.cmdbdataprocess.mapping;

import com.google.cloud.asset.v1.*;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Value;
import lombok.extern.log4j.Log4j2;
import sn.intouch.pfe.cmdbdataprocess.entities.cloudsql.CloudSQL;
import sn.intouch.pfe.cmdbdataprocess.entities.cloudsql.CloudSQLBuilder;
import sn.intouch.pfe.cmdbdataprocess.entities.disk.Disk;
import sn.intouch.pfe.cmdbdataprocess.entities.disk.DiskBuilder;
import sn.intouch.pfe.cmdbdataprocess.entities.loadbalancer.LoadBalancer;
import sn.intouch.pfe.cmdbdataprocess.entities.loadbalancer.LoadBalancerBuilder;
import sn.intouch.pfe.cmdbdataprocess.entities.network.Subnet;
import sn.intouch.pfe.cmdbdataprocess.entities.network.SubnetBuilder;
import sn.intouch.pfe.cmdbdataprocess.entities.network.VPCBuilder;
import sn.intouch.pfe.cmdbdataprocess.entities.network.VirtualPrivateCloud;
import sn.intouch.pfe.cmdbdataprocess.entities.project.Project;
import sn.intouch.pfe.cmdbdataprocess.entities.project.ProjectBuilder;
import sn.intouch.pfe.cmdbdataprocess.entities.serverless.CloudFunction;
import sn.intouch.pfe.cmdbdataprocess.entities.serverless.CloudFunctionBuilder;
import sn.intouch.pfe.cmdbdataprocess.entities.serverless.CloudRun;
import sn.intouch.pfe.cmdbdataprocess.entities.serverless.CloudRunBuilder;
import sn.intouch.pfe.cmdbdataprocess.entities.vms.InstanceGroup;
import sn.intouch.pfe.cmdbdataprocess.entities.vms.InstanceGroupBuilder;
import sn.intouch.pfe.cmdbdataprocess.entities.vms.VMBuilder;
import sn.intouch.pfe.cmdbdataprocess.entities.vms.VirtualMachine;
import sn.intouch.pfe.cmdbdataprocess.utils.CloudAssetAuthUtil;
import sn.intouch.pfe.cmdbdataprocess.utils.AssetMapping;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
                //AssetMapping.TARGET_HTTP_PROXY,
                //AssetMapping.INSTANCE_TEMPLATE,
                //AssetMapping.ROUTER,
                AssetMapping.URLMAP,
                AssetMapping.INSTANCE_GROUP,
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
                            .setPageSize(10)

                            .build();
            AssetServiceClient.ListAssetsPagedResponse response = client.listAssets(request);
            responseProcessing(response);

            while (!response.getNextPageToken().isEmpty()) {
                request = request.toBuilder().setPageToken(response.getNextPageToken()).build();
                response = client.listAssets(request);
                responseProcessing(response);
            }
            } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        listAssets();
    }

    private static void responseProcessing(AssetServiceClient.ListAssetsPagedResponse response) throws InterruptedException, IOException {
        for (Asset asset : response.getPage().getValues()) {
            String nameTest = asset.getName();
            String type = MappingEngine.getAssetType(asset);
            log.info("name : " + nameTest);
            log.info("type : " + type);
            switch (type){
                case AssetMapping.CLOUD_FUNCTION:
                    CloudFunction cloudFunction = CloudFunctionBuilder.cloudFunctionBuilder(asset);
                    log.info("name : " + cloudFunction.getName());
                    log.info("region : " + cloudFunction.getRegion());
                    log.info("url : " + cloudFunction.getUrl());
                    log.info("ingress : " + cloudFunction.getIngress());
                    log.info("egress : " + cloudFunction.getEgress());
                    break;
                case AssetMapping.DISK:
                    Disk disk = DiskBuilder.diskBuilder(asset);
                    log.info("name: " + disk.getName());
                    log.info("type: " + disk.getArchitecture());
                    log.info("size in Gb: " + disk.getSize());
                    log.info("region: " + disk.getRegion());
                    break;
                case AssetMapping.SUBNETWORK:
                    Subnet subnet = SubnetBuilder.subnetBuilder(asset);
                    log.info("range : " + subnet.getRange());
                    log.info("region : " + subnet.getRegion());
                    log.info("purpose : " + subnet.getPurpose());
                    log.info("vpcName : " + subnet.getVPCName());
                    break;
                case AssetMapping.PROJECT:
                    Project project = ProjectBuilder.projectBuilder(asset);
                    log.info("projectId : " + project.getProjectID());
                    log.info("projectName : " + project.getProjectName());
                    break;
                case AssetMapping.VPC:
                    VirtualPrivateCloud virtualPrivateCloud = VPCBuilder.vpcBuilder(asset);
                    log.info("Description: " + virtualPrivateCloud.getDescription());
                    log.info("Name: " + virtualPrivateCloud.getName());
                    log.info("firewallRule: " + virtualPrivateCloud.getFirewallRules());
                    log.info("routingMode: " + virtualPrivateCloud.getRoutingMode());
                    log.info("projectName: " + virtualPrivateCloud.getProjectName());
                    break;
                case AssetMapping.URLMAP:
                    LoadBalancer loadBalancer = LoadBalancerBuilder.loadBalancerBuilder(asset,"eme-iacc");
                    log.info("Name : " + loadBalancer.getName());
                    log.info("Region : " + loadBalancer.getRegion());
                    log.info("description : " + loadBalancer.getDescription());
                    log.info("Target Proxies : " + (loadBalancer.getTargetProxies() != null && !loadBalancer.getTargetProxies().isEmpty()  ? loadBalancer.getTargetProxies().get(0) : ""));
                    log.info("Backends : " + (loadBalancer.getBackends() != null && !loadBalancer.getBackends().isEmpty()  ? loadBalancer.getBackends().get(0) : ""));
                    break;
                case AssetMapping.VM_INSTANCE:
                    VirtualMachine virtualMachine = VMBuilder.vmBuilder(asset,"eme-iacc");
                    log.info("Name : " + virtualMachine.getName());
                    log.info("zone : " + virtualMachine.getZone());
                    log.info("Machine Type : " + virtualMachine.getMachineType());
                    log.info("private IP : " + virtualMachine.getPrivateIP());
                    log.info("status : " + virtualMachine.getStatus());
                    log.info("natIP : " + virtualMachine.getNatIP());
                    log.info("subnet : " + virtualMachine.getSubnet());
                    log.info("Disk : " + virtualMachine.getDiskName());
                    log.info("IG : " + virtualMachine.getInstanceGroupName());
                    break;
                case AssetMapping.INSTANCE_GROUP:
                    InstanceGroup instanceGroup = InstanceGroupBuilder.instanceGroupBuilder(asset,"eme-iacc");
                    log.info("name : " + instanceGroup.getName());
                    log.info("description : " + instanceGroup.getDescription());
                    log.info("vpc : " + instanceGroup.getVpc());
                    log.info("subnet : " + instanceGroup.getSubnetwork());
                    log.info("region : " + instanceGroup.getRegion());
                    log.info("manager : " + instanceGroup.getManager());
                    break;
                case AssetMapping.CLOUD_SQL:
                    CloudSQL cloudSQL = CloudSQLBuilder.cloudSQLBuilder(asset);
                    log.info("name : " + cloudSQL.getName());
                    log.info("region : " + cloudSQL.getRegion());
                    log.info("databaseVersion : " + cloudSQL.getDatabaseVersion());
                    log.info("characteristics : " + cloudSQL.getCharacteristics().toString());
                    log.info("backup : " + cloudSQL.getBackup());
                    log.info("privateIP : " + cloudSQL.getPrivateIP());
                    log.info("replication : " + cloudSQL.getReplication());
                    log.info("IP : " + cloudSQL.getIPAddress());
                    log.info("vpc : " + cloudSQL.getVpc());
                    break;
                case AssetMapping.CLOUD_RUN_EXECUTION:
                    CloudRun cloudRun = CloudRunBuilder.cloudRunBuilder(asset);
                    log.info ("name : " + cloudRun.getName());
                    log.info ("region : " + cloudRun.getRegion());
                    log.info ("url : " + cloudRun.getUrl());
                    log.info ("ram : " + cloudRun.getRam());
                    log.info ("cpu : " + cloudRun.getCpu());
                    log.info ("maxRetries : " + cloudRun.getMaxRetries());
                    log.info ("kind : " + cloudRun.getKind());
                    log.info ("sqlInstance : " + cloudRun.getCloudSQLInstance());
                    break;
                default:
                    log.info("asset non traité");
            }

            //Map< Descriptors.FieldDescriptor,Object> fields = asset.getAllFields();
            //Resource resource = (Resource) fields.get(Asset.getDescriptor().findFieldByName("resource"));
            //Map<String, Value> fields1 = resource.getData().getFieldsMap();
            //List<Value> list = fields1.get("disks").getListValue().getValuesList();
            //String test = list.get(0).getStructValue().getFieldsMap();
            //System.out.println(list.get(0).getStructValue().getFieldsMap().get("architecture"));
            //System.out.println(resource.getData().getFieldsMap());
            //System.out.println(asset.getAllFields());
            System.out.println("-------------------------------------------------");
        }
    }

}
