package sn.intouch.pfe.cmdbdataprocess.mapping;

// Imports the Google Cloud client library

import com.google.cloud.asset.v1.*;
import com.google.protobuf.Timestamp;
import sn.intouch.pfe.cmdbdataprocess.utils.CloudAssetAuthUtil;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class History {

    // Use the default project Id.
    private static final String projectId = "eme-iacc";

    // Export assets for a project.
    // @param args path where the results will be exported to.
    public static void main(String... args) throws Exception {
        // Asset names, e.g.: "//storage.googleapis.com/[BUCKET_NAME]"
        String[] assetNames  = {
                "//run.googleapis.com/projects/eme-iacc/locations/europe-west1/executions/export-csv-job-z9zdg",
        };
        System.out.println(toUpdate(projectId, Arrays.stream(assetNames).toList()));
    }

    public static Boolean toUpdate(String projectId, List<String> assetNames) throws IOException {
        // Asset names, e.g.: "//storage.googleapis.com/[BUCKET_NAME]"
        try (AssetServiceClient client = CloudAssetAuthUtil.getAssetServiceClient()) {
            ProjectName parent = ProjectName.of(projectId);
            ContentType contentType = ContentType.CONTENT_TYPE_UNSPECIFIED;
            Instant endTime = Instant.now();
            Instant startTime = endTime.minus(1, ChronoUnit.DAYS);
            Timestamp endTimestamp = Timestamp.newBuilder()
                    .setNanos(endTime.getNano())
                    .setSeconds(endTime.getEpochSecond())
                    .build();
            Timestamp startTimestamp = Timestamp.newBuilder()
                    .setSeconds(startTime.getEpochSecond())
                    .setNanos(startTime.getNano())
                    .build();
            TimeWindow readTimeWindow = TimeWindow.newBuilder()
                    .setEndTime(endTimestamp)
                    .setStartTime(startTimestamp)
                    .build();
            BatchGetAssetsHistoryRequest request =
                    BatchGetAssetsHistoryRequest.newBuilder()
                            //.setReadTimeWindow()
                            .setParent(parent.toString())
                            .addAllAssetNames(assetNames)
                            .setContentType(contentType)
                            .setReadTimeWindow(readTimeWindow)
                            .build();
            BatchGetAssetsHistoryResponse response = client.batchGetAssetsHistory(request);
            for (TemporalAsset asset : response.getAssetsList()){
                if(asset.getAsset().getUpdateTime().getSeconds()>startTime.getEpochSecond())
                    return true;
            }
        }
        return true;
    }
}
