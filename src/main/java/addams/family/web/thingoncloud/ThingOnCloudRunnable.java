package addams.family.web.thingoncloud;

import addams.family.web.config.ThingOnCloudProperty;
import addams.family.web.db.DB;
import addams.family.web.http.HttpRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by developer on 1/6/17.
 */
public class ThingOnCloudRunnable implements Runnable {

    private static org.slf4j.Logger LOG;
    private DB db = DB.getInstance();
    private Map<String, String> valuesCache = new HashMap<>();
    private Map<String, String> valuesToBeSent = new HashMap<>();
    private List<ThingOnCloudProperty> tocp;
    private boolean logService = false;

    public ThingOnCloudRunnable(List<ThingOnCloudProperty> tocp) {
        LOG = LoggerFactory.getLogger(ThingOnCloudRunnable.class);
        logService = tocp.get(0).log;
        this.tocp = tocp;
    }

    @Override
    public void run() {
        Map<String, String> values = null;
        while(true) {
            try {
                Thread.sleep(1000);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            try {
                values = db.getAllValuesMap();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            selectUpdateValues(values);
            for(ThingOnCloudProperty property : tocp) {
                pushDirectValues(property, valuesToBeSent);
            }
        }
    }

    private void selectUpdateValues(Map<String, String> values) {
        valuesToBeSent.clear();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            if(valuesCache.containsKey(entry.getKey())) {
                if(!valuesCache.get(entry.getKey()).equals(values.get(entry.getKey()))) {
                    valuesToBeSent.put(entry.getKey(), entry.getValue());
                    valuesCache.put(entry.getKey(), entry.getValue());
                }
            } else {
                valuesToBeSent.put(entry.getKey(), entry.getValue());
                valuesCache.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private String convertToJson(Map<String, String> data) {
        JSONArray valuesItems = new JSONArray();
        JSONObject values = new JSONObject();
        values.put("values", valuesItems);
        for (Map.Entry<String, String> entry : data.entrySet()) {
            JSONObject endpoint = new JSONObject();
            endpoint.put("name", entry.getKey());
            endpoint.put("value", entry.getValue());
            valuesItems.add(endpoint);
        }
        return values.toJSONString();
    }

    private void pushDirectValues(ThingOnCloudProperty serv, Map<String, String> data) {
        if(!data.isEmpty()) {
            String body = convertToJson(data);
            if(logService) {
                LOG.debug(body);
            }
            try {
                String output = HttpRequest.post(serv.url + "/" + serv.service, body, serv.tocen);
                if(logService) {
                    LOG.debug(output);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
