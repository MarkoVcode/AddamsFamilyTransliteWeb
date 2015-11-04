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
//TODO 1w sensors values
//TODO reload on properties change
//	private static String DB;
	private Properties prop;
	private DB db;
	private SoundSchedulerThread soundt;
	private SensorsSchedulerThread senst;
	
	private static Logger LOG;
	
	public WebInterface() {
		prop = Properties.getInstance();
		db = DB.getInstance();
		System.setProperty("logback.configurationFile", prop.getLogConfig());
		LOG = LoggerFactory.getLogger(WebInterface.class);
		LOG.info("Starting Addams Family Screen");
		soundt = new SoundSchedulerThread(prop.getAudioProperties());
		soundt.start();
		senst = new SensorsSchedulerThread(prop.get1WSensorsProperty());
		senst.start();
	}
	
	@Override
	public void init() {
		
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
		
		post("/lightOff", (request, response) -> {
			db.setBrightness(0);
			return "Light is Off: " + db.getBrithtness();
		});
		
		post("/cronReload", (request, response) -> {
			reloadScheduler();
			return "Reloaded";
		});
		
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
