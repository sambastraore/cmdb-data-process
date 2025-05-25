package sn.intouch.pfe.cmdbdataprocess.mapping;

import com.google.api.gax.paging.Page;
import com.google.api.gax.paging.PagedListResponse;
import com.google.cloud.audit.AuditLog;
import com.google.cloud.audit.AuditLogProto;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.LoggingOptions;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Payload;
import com.google.cloud.logging.Logging.EntryListOption;
import com.google.gson.Gson;
import com.google.protobuf.Any;
import com.google.protobuf.util.JsonFormat;
import lombok.extern.log4j.Log4j2;
import sn.intouch.pfe.cmdbdataprocess.utils.LoggingUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Log4j2
public class CloudAuditLogReader {

    public static void main(String[] args) {
        // Créer le client Logging (utilise ADC, donc pas besoin de clé explicite si déployé dans Cloud Run)
        try (Logging logging = LoggingUtil.createLoggingClient()) {


            String filterCreate = """
                        logName = "projects/eme-iacc/logs/cloudaudit.googleapis.com%2Factivity"
                        AND (protoPayload.methodName:"insert" OR protoPayload.methodName:"create")
                        AND protoPayload.authorizationInfo.granted = true
                        AND protoPayload.resourceName = "projects/eme-iacc/zones/europe-west1-b/instances/server-wildfly-instance"
                        AND timestamp >= "2010-05-20T00:00:00Z"
                        
                    """;

            String filterLastModifiedBy = """
                        logName = "projects/eme-iacc/logs/cloudaudit.googleapis.com%2Factivity"
                        AND protoPayload.authorizationInfo.granted = true
                        AND resource.type=("cloud_run_service" OR "cloud_run_revision" OR "cloud_build_build")
                        AND timestamp >= "2010-05-20T00:00:00Z"
                                         
                    """;





          /**  Page<LogEntry> response = logging.listLogEntries(
                    EntryListOption.filter(filterLastModifiedBy),
                    EntryListOption.pageSize(3),
                    EntryListOption.sortOrder(Logging.SortingField.TIMESTAMP, Logging.SortingOrder.DESCENDING)
            );
            for (LogEntry logEntry : response.getValues()){
                Payload.ProtoPayload protoPayload = logEntry.getPayload();
                Any anyPayload = protoPayload.getData();
                AuditLog auditLog = anyPayload.unpack(AuditLog.class);
                long seconds = auditLog.getRequestMetadata().getRequestAttributes().getTime().getSeconds();
                Instant instant = Instant.ofEpochSecond(seconds);
                Date date = Date.from(instant);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                log.info("date : " + sdf.format(date));
                log.info("user : " + auditLog.getAuthenticationInfo().getPrincipalEmail());
                log.info("method : " + auditLog.getMethodName());
                log.info("resource : " + auditLog.getResourceName());
                log.info("--------------------------------");
            }**/


            Page<LogEntry> response1 = logging.listLogEntries(
                    EntryListOption.filter(filterLastModifiedBy),
                    EntryListOption.pageSize(3),
                    EntryListOption.sortOrder(Logging.SortingField.TIMESTAMP, Logging.SortingOrder.ASCENDING)
            );
            for (LogEntry logEntry : response1.getValues()){
                Payload.ProtoPayload protoPayload = logEntry.getPayload();
                Any anyPayload = protoPayload.getData();
                AuditLog auditLog = anyPayload.unpack(AuditLog.class);
                long seconds = auditLog.getRequestMetadata().getRequestAttributes().getTime().getSeconds();
                Instant instant = Instant.ofEpochSecond(seconds);
                Date date = Date.from(instant);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                log.info("date : " + sdf.format(date));
                log.info("user : " + auditLog.getAuthenticationInfo().getPrincipalEmail());
                log.info("method : " + auditLog.getMethodName());
                log.info("resource : " + auditLog.getResourceName());
                log.info(auditLog.getMetadata());
                log.info("--------------------------------");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

