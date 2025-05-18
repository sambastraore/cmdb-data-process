package sn.intouch.pfe.cmdbdataprocess.utils;

public class RelationshipMapping {
    public static final String BACKEND_SERVICE_TO_INSTANCE_GROUP = "COMPUTE_BACKEND_SERVICE_TO_INSTANCE_GROUP";
    public static final String BACKEND_SERVICE_TO_URLMAP = "COMPUTE_BACKEND_SERVICE_TO_URLMAP";
    public static final String HTTP_PROXY_TO_FORWARDING_RULE = "COMPUTE_TARGET_HTTP_PROXY_TO_FORWARDING_RULE";
    public static final String HTTPS_PROXY_TO_FORWARDING_RULE = "COMPUTE_TARGET_HTTPS_PROXY_TO_FORWARDING_RULE";
    public static final String HTTP_PROXY_TO_URLMAP = "COMPUTE_TARGET_HTTP_PROXY_TO_URLMAP";
    public static final String HTTPS_PROXY_TO_URLMAP = "COMPUTE_TARGET_HTTPS_PROXY_TO_URLMAP";
    public static final String HTTPS_PROXY_TO_CERTIFICATE = "COMPUTE_TARGET_HTTPS_PROXY_TO_SSL_CERTIFICATE";
    public static final String URLMAP_TO_HTTP_PROXY = "COMPUTE_URLMAP_TO_TARGET_HTTP_PROXY";
    public static final String URLMAP_TO_HTTPS_PROXY = "COMPUTE_URLMAP_TO_TARGET_HTTPS_PROXY";
    public static final String URLMAP_TO_BACKEND_SERVICE = "COMPUTE_URLMAP_TO_BACKEND_SERVICE";
    public static final String INSTANCE_TO_GROUP = "INSTANCE_TO_INSTANCEGROUP";
    public static final String GROUP_TO_INSTANCE = "COMPUTE_INSTANCE_GROUP_TO_INSTANCE";
    public static final String BACKEND_SERVICE_TO_GROUP = "COMPUTE_BACKEND_SERVICE_TO_INSTANCE_GROUP";
    public static final String GROUP_TO_MANAGER = "INSTANCEGROUP_TO_INSTANCEGROUPMANAGER";
    public static final String EUROPE_EME_IACC_CONNECTION_CLOUDSQL = "eme-iacc:europe-west1:eme-iacc";
    public static final String US_USER_CONNECTION_CLOUDSQL = "eme-iacc:us-east1:user";
    public static final String DISK_VM_RELATION = "Map_VMs_for_disk";
    public static final String CLOUDRUN_CLOUDSQL_RELATION = "Map_cloudsql_for_cloudrun";
    public static final String CLOUDSQL_VPC_RELATION = "Map_vpc_for_cloud_sqls";
    public static final String IG_SUBNET_RELATION = "Map_subnet_for_instance_groups";
    public static final String IG_VPC_RELATION = "Map_vpc_for_instance_groups";
    public static final String SUBNET_VPC_RELATION = "Map_vpc_for_subnets";
    public static final String VM_IG_RELATION = "Map_instance_group_for_vms";
    public static final String VPC_PROJECT_RELATION = "Map_project_for_vpcs";

    public static final String VM_FOR_DISK = "Map_disk_for_vms";

    public static final String SUBNET_FOR_VM = "Map_subnet_for_vms";

    public static final String APACHE_FOR_VM = "Map_apache_for_vm";
    public static final String MYSQL_FOR_VM = "Map_mysql_for_vm";
    public static final String WILDFLY_FOR_VM = "Map_wildfly_for_vm";
    public static final String APACHE_FOR_WILDFLY = "Map_apache_for_wildfly";
    public static String WILDFLY_FOR_MYSQL = "Map_wildfly_for_mysql";
    public static final String WILDFLY_FOR_CLOUDSQL = "Map_wildfly_for_cloudsql";

    public static final String VHOST_FOR_APACHE = "Map_vhost_for_apache";

    public static final String VHOST_FOR_WILDFLY = "Map_wildfly_for_vhost";














}
