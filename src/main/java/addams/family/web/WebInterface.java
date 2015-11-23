package addams.family.web;

import static spark.Spark.get;
import static spark.Spark.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import addams.family.web.config.Properties;
import addams.family.web.db.DB;
import addams.family.web.scheduler.SensorsSchedulerThread;
import addams.family.web.scheduler.SoundSchedulerThread;
import spark.servlet.SparkApplication;

public class WebInterface  implements SparkApplication {
//TODO reload on properties change
	private Properties prop;
	private DB db;
	private SoundSchedulerThread soundt;
	private SensorsSchedulerThread senst;
	
	private static Logger LOG;
	
	public WebInterface() {
		prop = Properties.getInstance();
		System.setProperty("logback.configurationFile", prop.getLogConfig());
		LOG = LoggerFactory.getLogger(WebInterface.class);
		LOG.info("Starting Addams Family Screen");
		db = DB.getInstance();
		soundt = new SoundSchedulerThread(prop.getAudioProperties());
		soundt.start();
		senst = new SensorsSchedulerThread(prop.get1WSensorsProperty());
		senst.start();
	}
	
	@Override
	public void init() {
		
		post("/extLightMax", (request, response) -> {
			db.setExternalBrightness("E1", 255);
			db.setExternalBrightness("E2", 255);
			db.setExternalBrightness("E3", 255);
			db.setExternalBrightness("E4", 255);
			return "Ext Light is MAX: " + 255;
		});
		
		post("/extLightMin", (request, response) -> {
			db.setExternalBrightness("E1", 0);
			db.setExternalBrightness("E2", 0);
			db.setExternalBrightness("E3", 0);
			db.setExternalBrightness("E4", 0);
			return "Ext Light is Min: " + 0;
		});
		
		post("/lightMax", (request, response) -> {
			db.setBrightness(prop.getMAXBrightness());
			return "Light is MAX: " + db.getBrithtness();
		});
		
		post("/lightMin", (request, response) -> {
			db.setBrightness(prop.getMINBrightness());
			return "Light is MIN: " + db.getBrithtness();
		});

		post("/lightOTT", (request, response) -> {
			db.setBrightness(prop.getOTTBrightness());
			return "Light is OTT: " + db.getBrithtness();
		});
		
		post("/lightAuto", (request, response) -> {
			db.setBrightness(0);
			return "Light is Off: " + db.getBrithtness();
		});
		
		post("/suspend", (request, response) -> {
			db.setBrightness(-1);
			return "Behaviour suspended: " + db.getBrithtness();
		});
		
		post("/cronReload", (request, response) -> {
			reloadScheduler();
			return "Reloaded";
		});
		
		post("/eBrightness/:channel/:value", (request, response) -> {
			Integer val = Integer.parseInt(request.params(":value"));
			if(val >= 0 && val < 256) {
				db.setExternalBrightness(request.params(":channel"), val);
			}
			return "eBrightness (" + request.params(":channel") + "): " + db.getExternalBrightness(request.params(":channel"));
		});
		
		get("/eBrightness", "application/json", (request, response) -> {
			return db.getExternalBrightnessJSON();
		});
		
/*		get("/eBrightness", "application/json", (request, response) -> {
		    return db.getExternalBrightnessBean();
		}, new JsonTransformer());*/
		
		get("/lightCurrent", (request, response) -> {
			return "Light is: " + db.getBrithtness();
		});
		
		get("/values", (request, response) -> {
			return "Values: " + db.getValues();
		});
				
		get("/", (request, response) -> {
            return "Addams Family Translite Project";
        });
	}
	
	private void reloadScheduler() {
		soundt.reload(prop.getAudioProperties());
	}
	
	public static void main(String[] args) {
		WebInterface wa = new WebInterface();		
		wa.init();
	}
}
