package sn.intouch.pfe.cmdbdataprocess.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class HttpUtil {

    /**
    public static String getToken() throws IOException, JSONException {
        String url = Config.baseUrl + "sessions?scope=service&returnId=true";
        String body = "{\"username\":\"admin\",\"password\":\"admin\"}";
        String method = "POST";
        HttpURLConnection connection = getHttpURLConnection(url,body,null,method);

        BufferedReader in;
        int responseCode = connection.getResponseCode();
        log.info ("get token : " + responseCode);
        if (responseCode >= 200 && responseCode < 300) {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }

        String line;
        StringBuilder response = new StringBuilder();
        String token;

        while ((line = in.readLine()) != null) {
            response.append(line).append("\n");
        }
        in.close();
        JSONObject jsonObject = new JSONObject(response.toString());
        JSONObject data = jsonObject.getJSONObject("data");
        token = data.getString("_id");
        return token;
    }**/



        private static String cachedToken = null;
        private static long tokenExpirationTime = 0;
        private static final long TOKEN_VALIDITY_DURATION = 60 * 60 * 1000;

        public static String getToken() throws IOException, JSONException {
            long currentTime = System.currentTimeMillis();

            if (cachedToken != null && currentTime < tokenExpirationTime) {
                return cachedToken;
            }

            String url = Config.baseUrl + "sessions?scope=service&returnId=true";
            String body = "{\"username\":\"admin\",\"password\":\"admin\"}";
            String method = "POST";
            HttpURLConnection connection = getHttpURLConnection(url, body, null, method);

            BufferedReader in;
            int responseCode = connection.getResponseCode();
            log.info("get token : " + responseCode);
            if (responseCode >= 200 && responseCode < 300) {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            in.close();

            JSONObject jsonObject = new JSONObject(response.toString());
            JSONObject data = jsonObject.getJSONObject("data");
            cachedToken = data.getString("_id");
            tokenExpirationTime = currentTime + TOKEN_VALIDITY_DURATION;

            return cachedToken;
        }



    private static HttpURLConnection getHttpURLConnection(String url, String body, String auth, String method) throws IOException {
        URL tokenURL = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) tokenURL.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Cmdbuild-authorization", auth);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        if (body != null){
            try (OutputStream os = connection.getOutputStream()){
                byte[] input = body.getBytes(StandardCharsets.UTF_8);
                os.write(input,0, input.length);
            }
        }
        return connection;
    }

    public static void saveOrUpdate (String name, String url, String body, Integer cardId) throws JSONException, IOException {
        String token = getToken();
        HttpURLConnection connection;
        if (cardId == null){
            connection = getHttpURLConnection(url,body,token,"POST");
        } else {
            url = url + "/" +cardId;
            connection = getHttpURLConnection(url,body,token,"PUT");
        }
        int responseCode = connection.getResponseCode();
        BufferedReader in;

        if (responseCode >= 200 && responseCode < 300) {
            log.info("response code while saving/updating " + name + " : " + responseCode);
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            log.error("error code while saving/updating " + name + " : " + responseCode);
            in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }

        String line;
        StringBuilder response = new StringBuilder();

        while ((line = in.readLine()) != null) {
            response.append(line).append("\n");
        }
        in.close();

        log.info("response : " + response);

    }

    public static Integer getCardId (String theClass, String attribute, String value) throws JSONException, IOException {
        String url = Config.baseUrl + "classes/" + theClass + "/cards";
        HttpURLConnection connection = getHttpURLConnection(url,null,getToken(),"GET");
        int responseCode = connection.getResponseCode();
        if (responseCode >= 200 && responseCode<300){
            log.info("response code while getting " + theClass + " : " + responseCode);
            JSONArray data = getTheResponse(connection);
            for (int i = 0; i < data.length(); i++) {
                JSONObject card = data.getJSONObject(i);
                if (Objects.equals(card.getString(attribute), value)){
                    return card.getInt("_id");
                }
            }
        }else {
            log.error("error code while saving " + theClass + " : " + responseCode);
        }
        return null;
    }


    public static Integer getRelationId (String theDomain, String destClass, String destAttribute, String destValue, String sourceClass, String sourceAttribute, String sourceValue) throws JSONException, IOException {
        String url = Config.baseUrl + "classes/" + sourceClass + "/cards/" + getCardId(sourceClass,sourceAttribute,sourceValue) + "/relations";
        HttpURLConnection connection = getHttpURLConnection(url,null,getToken(),"GET");
        int responseCode = connection.getResponseCode();
        if (responseCode >= 200 && responseCode<300){
            log.info("response code while getting relations of " + sourceClass + " : " + responseCode);
            JSONArray data = getTheResponse(connection);
            for (int i = 0; i < data.length(); i++) {
                JSONObject relation = data.getJSONObject(i);
                if (Objects.equals(relation.getString("_type"), theDomain) && Objects.equals(relation.getInt("_destinationId"),getCardId(destClass,destAttribute,destValue))){
                    return relation.getInt("_id");
                }
            }
        }else {
            log.error("error code while getting relation id " + theDomain + " : " + responseCode);
        }
        return null;
    }

    private static JSONArray getTheResponse(HttpURLConnection connection) throws IOException, JSONException {
        BufferedReader in;
        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();

        while ((line = in.readLine()) != null) {
            response.append(line).append("\n");
        }
        in.close();

        JSONObject jsonObject = new JSONObject(response.toString());
        return jsonObject.getJSONArray("data");
    }

    public static void saveOrUpdateRelation (String relationType, Integer relationId, Integer sourceId, String sourceType, Integer destinationId, String destinationType) throws JSONException, IOException {
        String body = "{"
                + "\"_type\": \"" + relationType + "\","
                + "\"_sourceType\": \"" + sourceType + "\","
                + "\"_sourceId\": " + sourceId + ","
                + "\"_destinationId\": " + destinationId + ","
                + "\"_destinationType\": \"" + destinationType + "\""
                + "}";
        String url;
        if (relationId == null){
            url = Config.baseUrl + "classes/" + sourceType + "/cards/" + sourceId + "/relations";
            log.info("saving : " + relationType );
            HttpURLConnection connection = getHttpURLConnection(url,body,getToken(),"POST");
            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300)
                log.info("saving successful");
            else
                log.info("saving failed : " + responseCode);

        } else{
            url = Config.baseUrl + "classes/" + sourceType + "/cards/" + sourceId + "/relations/" + relationId;
            log.info("updating : " + relationType );
            HttpURLConnection connection = getHttpURLConnection(url,body,getToken(),"PUT");
            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300)
                log.info("updating successful");
            else
                log.info("updating failed");
        }

    }


    public static JSONObject getCard(String className, String nameValue) throws JSONException, IOException {
        Integer cardId = getCardId(className,"name",nameValue);
        String url = Config.baseUrl + "classes/" + className + "/cards/" + cardId;
        HttpURLConnection connection = getHttpURLConnection(url,null,getToken(),"GET");
        int responseCode = connection.getResponseCode();
        if (responseCode >= 200 && responseCode<300){
            log.info ("response code : " + responseCode);
            BufferedReader in;
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            in.close();

            JSONObject jsonObject = new JSONObject(response.toString());
            return jsonObject.getJSONObject("data");

        }else {
            log.error("error code while getting card from " + className + " : " + responseCode);
        }
        return null;
    }


    public static List<String> getNames (String className) throws JSONException, IOException {
        List<String> names = new ArrayList<>();
        String url = Config.baseUrl + "classes/" + className +"/cards";
        HttpURLConnection connection = getHttpURLConnection(url,null,getToken(),"GET");
        int responseCode = connection.getResponseCode();
        if (responseCode >=200 && responseCode<300){
            JSONArray data = getTheResponse(connection);
            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.getJSONObject(i);
                names.add(object.getString("name"));
            }
        }
        else
            log.error("error code while getting data for names : " + responseCode);

        return names.isEmpty() ? null : names;
    }

    public static String [] getDatasourceUrl (String datasource){
        String [] urls = datasource.replace("[","").replace("]","").split("},");
        for (int i = 0; i < urls.length; i++) {
            urls[i] = urls[i].trim().replace("{","").split("url=")[1];
        }
        return urls;
    }


    public static String[] extractIpAndPort(String jdbcUrl) {
        Pattern pattern = Pattern.compile("jdbc:mysql://([^:/]+):(\\d+)/.*");
        Matcher matcher = pattern.matcher(jdbcUrl);

        if (matcher.matches()) {
            String ip = matcher.group(1);
            String port = matcher.group(2);
            return new String[]{ip, port};
        } else {
            return null;
        }
    }

    public static List<String> getRanges () throws JSONException, IOException {
        List<String> ranges = new ArrayList<>();
        String url = Config.baseUrl + "classes/Subnet/cards";
        HttpURLConnection connection = getHttpURLConnection(url,null,getToken(),"GET");
        int responseCode = connection.getResponseCode();
        if (responseCode >=200 && responseCode<300){
            JSONArray data = getTheResponse(connection);
            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.getJSONObject(i);
                ranges.add(object.getString("range"));
            }
        }
        else
            log.error("error code while getting data for names : " + responseCode);

        return ranges.isEmpty() ? null : ranges;
    }

    //public static String

    public static String getVMFromIP(String ip) throws JSONException, IOException {
        String url = Config.baseUrl + "classes/VirtualMachine/cards";
        HttpURLConnection connection = getHttpURLConnection(url,null,getToken(),"GET");
        int responseCode = connection.getResponseCode();
        if (responseCode >=200 && responseCode<300){
            JSONArray data = getTheResponse(connection);
            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.getJSONObject(i);
                if (Objects.equals(object.getString("privateIP"), ip))
                    return object.getString("name");
            }
        }
        else
            log.error("error code while mapping names to IPs for VMs : " + responseCode);

        return null;
    }

    public static String getPortFromMySQL (String mySQL) throws JSONException, IOException {
        JSONObject mysqlJson = getCard("MySQL",mySQL);
        return mysqlJson != null ? mysqlJson.getString("port") : null;
    }

    public static String getCloudSQLFromIP (String ip) throws JSONException, IOException {
        String url = Config.baseUrl + "classes/CloudSQL/cards";
        HttpURLConnection connection = getHttpURLConnection(url,null,getToken(),"GET");
        int responseCode = connection.getResponseCode();
        if (responseCode >=200 && responseCode<300){
            JSONArray data = getTheResponse(connection);
            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.getJSONObject(i);
                if (Objects.equals(object.getString("privateIP"), ip))
                    return object.getString("name");
            }
        }
        else
            log.error("error code while mapping names to IPs for VMs : " + responseCode);

        return null;
    }

    public static Map<String, String> extractIps(String configString) {
        Map<String, String> ipsAndPorts = new HashMap<>();

        if (configString == null || configString.isEmpty()) {
            return ipsAndPorts;
        }

        Pattern pattern = Pattern.compile(
                "(?:(?<!ProxyPass)BalancerMember|(?<!Reverse)ProxyPass)\\s+(?:/\\S*\\s+)?(?:\"|')?http://([a-zA-Z0-9.-]+)(?::(\\d+))?"
        );

        Matcher matcher = pattern.matcher(configString);
        while (matcher.find()) {
            String hostname = matcher.group(1);
            String port = matcher.group(2) != null ? matcher.group(2) : "";
            if (!hostname.equals("balancer")) {
                ipsAndPorts.put(hostname, port);
            }
        }

        return ipsAndPorts;
    }


    public static void main(String[] args) throws IOException, JSONException {
        log.info(getToken());
    }
}
