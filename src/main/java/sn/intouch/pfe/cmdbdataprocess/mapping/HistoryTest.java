package sn.intouch.pfe.cmdbdataprocess.mapping;

// Imports the Google Cloud client library

import com.google.cloud.asset.v1.AssetServiceClient;
import com.google.cloud.asset.v1.BatchGetAssetsHistoryRequest;
import com.google.cloud.asset.v1.BatchGetAssetsHistoryResponse;
import com.google.cloud.asset.v1.ContentType;
import com.google.cloud.asset.v1.ProjectName;
import com.google.cloud.asset.v1.TimeWindow;
import sn.intouch.pfe.cmdbdataprocess.utils.AssetMapping;
import sn.intouch.pfe.cmdbdataprocess.utils.CloudAssetAuthUtil;

import java.util.Arrays;

public class HistoryTest {

    // Use the default project Id.
    private static final String projectId = "eme-iacc";

    // Export assets for a project.
    // @param args path where the results will be exported to.
    public static void main(String... args) throws Exception {
        // Asset names, e.g.: "//storage.googleapis.com/[BUCKET_NAME]"
        String[] assetNames  = {
                "//run.googleapis.com/projects/eme-iacc/locations/europe-west1/executions/export-csv-job-z9zdg",
        };
        try (AssetServiceClient client = CloudAssetAuthUtil.getAssetServiceClient()) {
            ProjectName parent = ProjectName.of(projectId);
            ContentType contentType = ContentType.CONTENT_TYPE_UNSPECIFIED;
            TimeWindow readTimeWindow = TimeWindow.newBuilder().build();
            BatchGetAssetsHistoryRequest request =
                    BatchGetAssetsHistoryRequest.newBuilder()
                            .setParent(parent.toString())
                            .addAllAssetNames(Arrays.asList(assetNames))
                            .setContentType(contentType)
                            .setReadTimeWindow(readTimeWindow)
                            .build();
            BatchGetAssetsHistoryResponse response = client.batchGetAssetsHistory(request);
            System.out.println(response);
        }
    }
}
