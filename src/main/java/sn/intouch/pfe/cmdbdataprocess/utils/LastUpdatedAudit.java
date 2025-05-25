package sn.intouch.pfe.cmdbdataprocess.utils;

import com.google.api.gax.paging.Page;
import com.google.cloud.audit.AuditLog;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.Logging.EntryListOption;
import com.google.cloud.logging.Logging.SortingField;
import com.google.cloud.logging.Logging.SortingOrder;
import com.google.cloud.logging.Payload;
import com.google.protobuf.Any;
import lombok.extern.log4j.Log4j2;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class LastUpdatedAudit {

    public static Map<String, Map<String, String>> getLastUpdatedAudits(String project) {
        log.info("Fetching audit logs for project: " + project);
        Map<String, Map<String, String>> result = new HashMap<>();

        try (Logging logging = LoggingUtil.createLoggingClient()) {
            String filter = String.format("""
                    logName = "%s/logs/cloudaudit.googleapis.com%%2Factivity"
                    AND (protoPayload.methodName:"update" OR protoPayload.methodName:"patch" OR protoPayload.methodName:"set")
                    AND protoPayload.authorizationInfo.granted = true
                    AND (
                          protoPayload.resourceName:"/instances/" OR
                          protoPayload.resourceName:"/instanceGroups/" OR
                          protoPayload.resourceName:"/services/" OR
                          protoPayload.resourceName:"/cloudsql.instances/"
                        )
                    AND timestamp >= "2015-01-01T00:00:00Z"
                    """, project);

            Page<LogEntry> entries = logging.listLogEntries(
                    EntryListOption.filter(filter),
                    EntryListOption.pageSize(1000),
                    EntryListOption.sortOrder(SortingField.TIMESTAMP, SortingOrder.DESCENDING)
            );

            while (entries != null) {
                for (LogEntry logEntry : entries.iterateAll()) {
                    try {
                        Payload.ProtoPayload protoPayload = logEntry.getPayload();
                        Any anyPayload = protoPayload.getData();
                        AuditLog auditLog = anyPayload.unpack(AuditLog.class);

                        String resource = auditLog.getResourceName();
                        Instant logInstant = Instant.ofEpochSecond(
                                auditLog.getRequestMetadata().getRequestAttributes().getTime().getSeconds()
                        );
                        String user = auditLog.getAuthenticationInfo().getPrincipalEmail();
                        String method = auditLog.getMethodName();

                        Map<String, String> currentData = result.get(resource);
                        if (currentData == null || Instant.parse(currentData.get("logTime")).isBefore(logInstant)) {
                            Map<String, String> mapAudit = new HashMap<>();
                            mapAudit.put("logTime", logInstant.toString());
                            mapAudit.put("date", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date.from(logInstant)));
                            mapAudit.put("user", user);
                            mapAudit.put("method", method);
                            mapAudit.put("resource", resource);
                            result.put(resource, mapAudit);
                        }

                    } catch (Exception inner) {
                        log.warn("Error processing log entry: " + inner.getMessage());
                    }
                }
                entries = entries.getNextPage();
            }

        } catch (Exception e) {
            log.error("Error while fetching logs: " + e.getMessage(), e);
        }

        return result;
    }

    public static void main(String[] args) {
        Map<String, Map<String, String>> auditInfo = getLastUpdatedAudits("projects/eme-iacc");
        auditInfo.forEach((resource, details) -> {
            log.info("Resource: " + resource);
            log.info("User: " + details.get("user"));
            log.info("Date: " + details.get("date"));
            log.info("Method: " + details.get("method"));
            log.info("-------------------------------------------------");
        });
    }
}
