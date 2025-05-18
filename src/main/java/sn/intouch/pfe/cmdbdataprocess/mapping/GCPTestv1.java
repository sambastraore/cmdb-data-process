package sn.intouch.pfe.cmdbdataprocess.mapping;

import com.google.cloud.asset.v1.*;
import com.google.protobuf.Descriptors;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.configurationprocessor.json.JSONException;
import sn.intouch.pfe.cmdbdataprocess.entities.cloudsql.CloudSQL;
import sn.intouch.pfe.cmdbdataprocess.entities.cloudsql.CloudSQLBuilder;
import sn.intouch.pfe.cmdbdataprocess.entities.cloudsql.CloudSQLRelation;
import sn.intouch.pfe.cmdbdataprocess.entities.disk.Disk;
import sn.intouch.pfe.cmdbdataprocess.entities.disk.DiskBuilder;
import sn.intouch.pfe.cmdbdataprocess.entities.loadbalancer.LoadBalancer;
import sn.intouch.pfe.cmdbdataprocess.entities.loadbalancer.LoadBalancerBuilder;
import sn.intouch.pfe.cmdbdataprocess.entities.network.*;
import sn.intouch.pfe.cmdbdataprocess.entities.project.Project;
import sn.intouch.pfe.cmdbdataprocess.entities.project.ProjectBuilder;
import sn.intouch.pfe.cmdbdataprocess.entities.serverless.*;
import sn.intouch.pfe.cmdbdataprocess.entities.vms.*;
import sn.intouch.pfe.cmdbdataprocess.utils.CloudAssetAuthUtil;
import sn.intouch.pfe.cmdbdataprocess.utils.AssetMapping;

import java.io.IOException;
import java.util.*;

@Log4j2
public class GCPTestv1 {

    static List<CloudFunction> cloudFunctions = new ArrayList<>();
    static List<Disk> disks = new ArrayList<>();
    static List<Subnet> subnets = new ArrayList<>();
    static List<Project> projects = new ArrayList<>();
    static List<VirtualPrivateCloud> vpcs = new ArrayList<>();
    static List<LoadBalancer> loadBalancers = new ArrayList<>();
    static List<VirtualMachine> virtualMachines = new ArrayList<>();
    static List<InstanceGroup> instanceGroups = new ArrayList<>();
    static List<CloudSQL> cloudSQLs = new ArrayList<>();
    static List<CloudRun> cloudRuns = new ArrayList<>();
    static Map<String, List<?>> assetsByType = new HashMap<>();


    public static void listAssets() throws IOException, IllegalArgumentException {
        String projectId = "eme-iacc";
        //String projectId = "dev-top20";
        String[] assetTypes = {
                //AssetMapping.IP_ADDRESSES,
                //AssetMapping.DISK,
                //AssetMapping.FORWARDING_RULE,
                //AssetMapping.CLOUD_RUN_EXECUTION,
                //AssetMapping.VPC,
                //AssetMapping.CLOUD_SQL,
                //AssetMapping.VM_INSTANCE,
                //AssetMapping.PROJECT,
                //AssetMapping.SUBNETWORK,
                //AssetMapping.VM_IMAGE,
                //AssetMapping.TARGET_HTTPS_PROXY,
                //AssetMapping.CLOUD_FUNCTION,
                //AssetMapping.BACKEND_SERVICE,
                //AssetMapping.APP_ENGINE_APPLICATION,
                //AssetMapping.ClOUD_SQL_BACKUP,
                //AssetMapping.TARGET_HTTP_PROXY,
                AssetMapping.INSTANCE_TEMPLATE,
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
                            .setPageSize(10)

                            .build();
            AssetServiceClient.ListAssetsPagedResponse response = client.listAssets(request);
            responseProcessing(response,projectId);

            while (!response.getNextPageToken().isEmpty()) {
                request = request.toBuilder().setPageToken(response.getNextPageToken()).build();
                response = client.listAssets(request);
                responseProcessing(response,projectId);
            }

            relationProcessing(assetsByType);
            } catch (InterruptedException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        listAssets();
    }

    private static void responseProcessing(AssetServiceClient.ListAssetsPagedResponse response, String projectId) throws InterruptedException, IOException, JSONException {
        for (Asset asset : response.getPage().getValues()) {
            String type = MappingEngine.getAssetType(asset);
            log.info("type : " + type);
           switch (type){
                case AssetMapping.CLOUD_FUNCTION:
                    CloudFunction cloudFunction = CloudFunctionBuilder.cloudFunctionBuilder(asset,projectId);
                    cloudFunctions.add(cloudFunction);
                    log.info("name : " + (cloudFunction!= null ? cloudFunction.getName():""));
                    log.info("region : " + (cloudFunction!= null ? cloudFunction.getRegion():""));
                    log.info("url : " + (cloudFunction!= null ? cloudFunction.getUrl():""));
                    log.info("ingress : " + (cloudFunction!= null ? cloudFunction.getIngress():""));
                    log.info("egress : " + (cloudFunction!= null ? cloudFunction.getEgress():""));
                    break;
                case AssetMapping.DISK:
                    Disk disk = DiskBuilder.diskBuilder(asset,projectId);
                    disks.add(disk);
                    log.info("name: " + (disk != null ? disk.getName():""));
                    log.info("type: " + (disk != null ? disk.getArchitecture():""));
                    log.info("size in Gb: " + (disk != null ? disk.getSize():""));
                    log.info("region: " + (disk != null ? disk.getRegion():""));
                    break;
                case AssetMapping.SUBNETWORK:
                    Subnet subnet = SubnetBuilder.subnetBuilder(asset,projectId);
                    subnets.add(subnet);
                    log.info("range : " + (subnet != null ? subnet.getRange():""));
                    log.info("region : " + (subnet != null ? subnet.getRegion():""));
                    log.info("purpose : " + (subnet != null ? subnet.getPurpose():""));
                    log.info("vpcName : " + (subnet != null ? subnet.getVPCName():""));
                    break;
                case AssetMapping.PROJECT:
                    Project project = ProjectBuilder.projectBuilder(asset,projectId);
                    projects.add(project);
                    log.info("projectId : " + (project != null ? project.getProjectID():""));
                    log.info("projectName : " + (project != null ? project.getProjectName():""));
                    break;
                case AssetMapping.VPC:
                    VirtualPrivateCloud virtualPrivateCloud = VPCBuilder.vpcBuilder(asset,projectId);
                    vpcs.add(virtualPrivateCloud);
                    log.info("Description: " + (virtualPrivateCloud != null ? virtualPrivateCloud.getDescription():""));
                    log.info("Name: " + (virtualPrivateCloud != null ? virtualPrivateCloud.getName():""));
                    log.info("firewallRule: " + (virtualPrivateCloud != null ? virtualPrivateCloud.getFirewallRules():""));
                    log.info("routingMode: " + (virtualPrivateCloud != null ? virtualPrivateCloud.getRoutingMode():""));
                    log.info("projectName: " + (virtualPrivateCloud != null ? virtualPrivateCloud.getProjectName():""));
                    break;
                case AssetMapping.URLMAP:
                    LoadBalancer loadBalancer = LoadBalancerBuilder.loadBalancerBuilder(asset,projectId);
                    loadBalancers.add(loadBalancer);
                    log.info("Name : " + (loadBalancer != null ? loadBalancer.getName():""));
                    log.info("Region : " + (loadBalancer != null ? loadBalancer.getRegion():""));
                    log.info("description : " + (loadBalancer != null ? loadBalancer.getDescription():""));
                    if (loadBalancer != null){
                        log.info("Target Proxies : " + (loadBalancer.getTargetProxies() != null && !loadBalancer.getTargetProxies().isEmpty()  ? loadBalancer.getTargetProxies().get(0) : ""));
                        log.info("Backends : " + (loadBalancer.getBackends() != null && !loadBalancer.getBackends().isEmpty()  ? loadBalancer.getBackends().get(0) : ""));
                    }
                    break;
                case AssetMapping.VM_INSTANCE:
                    VirtualMachine virtualMachine = VMBuilder.vmBuilder(asset,projectId);
                    virtualMachines.add(virtualMachine);
                    log.info("Name : " + (virtualMachine != null ? virtualMachine.getName():""));
                    log.info("zone : " + (virtualMachine != null ? virtualMachine.getZone():""));
                    log.info("Machine Type : " + (virtualMachine != null ? virtualMachine.getMachineType():""));
                    log.info("private IP : " + (virtualMachine != null ? virtualMachine.getPrivateIP():""));
                    log.info("status : " + (virtualMachine != null ? virtualMachine.getStatus():""));
                    log.info("natIP : " + (virtualMachine != null ? virtualMachine.getNatIP():""));
                    log.info("subnet : " + (virtualMachine != null ? virtualMachine.getSubnet():""));
                    log.info("Disk : " + (virtualMachine != null ? virtualMachine.getDiskName():""));
                    log.info("IG : " + (virtualMachine != null ? virtualMachine.getInstanceGroupName():""));
                    log.info("potentialSqlInstances : " + (virtualMachine != null ? virtualMachine.getSameSubnetSQLInstances().size():""));
                    break;
                case AssetMapping.INSTANCE_GROUP:
                    InstanceGroup instanceGroup = InstanceGroupBuilder.instanceGroupBuilder(asset,projectId);
                    instanceGroups.add(instanceGroup);
                    log.info("name : " + (instanceGroup != null ? instanceGroup.getName():""));
                    log.info("description : " + (instanceGroup != null ? instanceGroup.getDescription():""));
                    log.info("vpc : " + (instanceGroup != null ? instanceGroup.getVpc():""));
                    log.info("subnet : " + (instanceGroup != null ? instanceGroup.getSubnetwork():""));
                    log.info("region : " + (instanceGroup != null ? instanceGroup.getRegion():""));
                    log.info("manager : " + (instanceGroup != null ? instanceGroup.getManager():""));
                    break;
                case AssetMapping.CLOUD_SQL:
                    CloudSQL cloudSQL = CloudSQLBuilder.cloudSQLBuilder(asset,projectId);
                    cloudSQLs.add(cloudSQL);
                    log.info("name : " + (cloudSQL != null ? cloudSQL.getName():""));
                    log.info("region : " + (cloudSQL != null ? cloudSQL.getRegion():""));
                    log.info("databaseVersion : " + (cloudSQL != null ? cloudSQL.getDatabaseVersion():""));
                    log.info("characteristics : " + (cloudSQL != null ? cloudSQL.getCharacteristics().toString():""));
                    log.info("backup : " + (cloudSQL != null ? cloudSQL.getBackup():""));
                    log.info("privateIP : " + (cloudSQL != null ? cloudSQL.getPrivateIP():""));
                    log.info("replication : " + (cloudSQL != null ? cloudSQL.getReplication():""));
                    log.info("IP : " + (cloudSQL != null ? cloudSQL.getIPAddress():""));
                    log.info("vpc : " + (cloudSQL != null ? cloudSQL.getVpc():""));
                    break;
                case AssetMapping.CLOUD_RUN_EXECUTION:
                    CloudRun cloudRun = CloudRunBuilder.cloudRunBuilder(asset,projectId);
                    cloudRuns.add(cloudRun);
                    log.info ("name : " + (cloudRun != null ? cloudRun.getName():""));
                    log.info ("region : " + (cloudRun != null ? cloudRun.getRegion():""));
                    log.info ("url : " + (cloudRun != null ? cloudRun.getUrl():""));
                    log.info ("ram : " + (cloudRun != null ? cloudRun.getRam():""));
                    log.info ("cpu : " + (cloudRun != null ? cloudRun.getCpu():""));
                    log.info ("maxRetries : " + (cloudRun != null ? cloudRun.getMaxRetries():""));
                    log.info ("kind : " + (cloudRun != null ? cloudRun.getKind():""));
                    log.info ("sqlInstance : " + (cloudRun != null ? cloudRun.getCloudSQLInstance():""));
                    break;
                default:
                    log.info("not processed asset");
            }
            Map< Descriptors.FieldDescriptor,Object> fields = asset.getAllFields();
            Resource resource = (Resource) fields.get(Asset.getDescriptor().findFieldByName("resource"));
            //Map<String, Value> fields1 = resource.getData().getFieldsMap();
            //List<Value> list = fields1.get("disks").getListValue().getValuesList();
            //String test = list.get(0).getStructValue().getFieldsMap();
            //System.out.println(list.get(0).getStructValue().getFieldsMap().get("architecture"));
            System.out.println(resource.getData().getFieldsMap());
            //System.out.println(asset.getAllFields());
            System.out.println("-------------------------------------------------");
        }
        assetsByType.put(AssetMapping.CLOUD_FUNCTION, cloudFunctions);
        assetsByType.put(AssetMapping.DISK, disks);
        assetsByType.put(AssetMapping.SUBNETWORK, subnets);
        assetsByType.put(AssetMapping.PROJECT, projects);
        assetsByType.put(AssetMapping.VPC, vpcs);
        assetsByType.put(AssetMapping.URLMAP, loadBalancers);
        assetsByType.put(AssetMapping.VM_INSTANCE, virtualMachines);
        assetsByType.put(AssetMapping.INSTANCE_GROUP, instanceGroups);
        assetsByType.put(AssetMapping.CLOUD_SQL, cloudSQLs);
        assetsByType.put(AssetMapping.CLOUD_RUN_EXECUTION, cloudRuns);
    }


    private static void relationProcessing(Map<String, List<?>> map) throws InterruptedException, IOException, JSONException {
        for (Map.Entry<String, List<?>> entry : map.entrySet()) {
            String assetType = entry.getKey();
            List<?> assets = entry.getValue();

            log.info("Adding relations by type : " + assetType);

            for (Object asset : assets) {
                switch (assetType) {
                    case AssetMapping.VM_INSTANCE :
                        VirtualMachine vm = (VirtualMachine) asset;
                        VMRelation.updateDiskRelation(vm);
                        VMRelation.updateInstanceGroupRelation(vm);
                        VMRelation.updateSubnetRelation(vm);
                        break;
                    case AssetMapping.CLOUD_RUN_EXECUTION:
                        CloudRun cloudRun = (CloudRun) asset;
                        CloudRunRelation.updateCloudRunRelation(cloudRun);
                        break;
                    case AssetMapping.CLOUD_SQL:
                        CloudSQL cloudSQL = (CloudSQL) asset;
                        CloudSQLRelation.updateCloudSQLRelation(cloudSQL);
                        break;
                    case AssetMapping.SUBNETWORK:
                        Subnet subnet = (Subnet) asset;
                        SubnetRelation.updateSubnetRelation(subnet);
                        break;
                    case AssetMapping.VPC:
                        VirtualPrivateCloud vpc = (VirtualPrivateCloud) asset;
                        VPCRelation.updateProjectRelation(vpc);
                        break;
                    case AssetMapping.INSTANCE_GROUP:
                        InstanceGroup instanceGroup = (InstanceGroup) asset;
                        InstanceGroupRelation.updateInstanceGroupRelationForVPC(instanceGroup);
                        InstanceGroupRelation.updateInstanceGroupRelationForSubnet(instanceGroup);
                        break;
                    default:
                        log.info("this is a default treatment");
               }
           }
    }
    }
}
