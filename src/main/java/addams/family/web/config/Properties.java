package addams.family.web.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;

public class Properties {
	
	private Configuration config;
	private static volatile Properties INSTANCE;
	private static final String CONFIG_FILE = "config/config.xml";
	private DefaultConfigurationBuilder factory;
	
	private Properties() {
		try {
			factory = new DefaultConfigurationBuilder(CONFIG_FILE);
			config = factory.getConfiguration();
		} catch (ConfigurationException e) {
			e.printStackTrace();
			System.exit(1);
		}	
	}

	public static Properties getInstance() {
		if(null == INSTANCE) {
			synchronized (Properties.class) {
				if(null == INSTANCE) {
					INSTANCE = new Properties();
				}
			}
		}
		return INSTANCE;
	}
	
	public String getTOCKey()
	{
		return config.getString("integration.toc.access.key");
	}
	
	public String getTOCSecret()
	{
		return config.getString("integration.toc.access.secret");
	}

	public String getLogConfig()
	{
		return config.getString("af.log.config");
	}
	
	public String get1WSensorPath()
	{
		return config.getString("af.sensor.1w.path");
	}	
	
	public String get1WSensorValueFile()
	{
		return config.getString("af.sensor.1w.value.file");
	}

	public int getMAXBrightness()
	{
		return config.getInt("af.backlight.max.value", 200);
	}

	public int getMINBrightness()
	{
		return config.getInt("af.backlight.min.value", 10);
	}
	
	public int getOTTBrightness()
	{
		return config.getInt("af.backlight.ott.value", 255);
	}

	public String getBehaviourSuspensionReset()
	{
		return config.getString("af.susp.reset.cron", "10 * * * *");
	}
	
	public String getDBPath() {
		return config.getString("af.db.path");
	}

	public String getDBName() {
		return config.getString("af.db.name");
	}
	
	public String getDB() {
		return getDBPath() + "/" + getDBName();
	}
	
	
	public List<AudioProperty> getAudioProperties() {
		reload();
		List<AudioProperty> properties = new ArrayList<AudioProperty>();
		String track = ""; 
		int configCounter = 1;
		while(track != null) {
			track = config.getString("af.soundplay.track." + configCounter);
			if(null != track) {
				int volume = config.getInt("af.soundplay.volume." + configCounter);
				int start = config.getInt("af.soundplay.start." + configCounter);
				int stop = config.getInt("af.soundplay.stop." + configCounter);
				String knocks = config.getString("af.soundplay.knock." + configCounter);
				String cron = config.getString("af.soundplay.cron." + configCounter);
				String light = config.getString("af.soundplay.light." + configCounter);
				if(null != track) {
					AudioProperty ap = new AudioProperty();
					ap.setVolume(volume);
					ap.setCron(cron);
					ap.setStart(start);
					ap.setStop(stop);
					ap.setTrack(track);
					ap.setKnocks(knocks);
					ap.setLight(light);
					properties.add(ap);
				}
				configCounter++;
			}
		}
		return properties;
	}

	public SensorsProperty get1WSensorsProperty()
	{
		reload();
		Map<String,String> map = new HashMap<String,String>();
		String sensorId = "";
		String sensorName = "";
		int configCounter = 1;
		while(sensorId != null) {
			sensorId = config.getString("af.sensor.1w.id." + configCounter);
			sensorName = config.getString("af.sensor.1w.name." + configCounter);
			if(null != sensorId && null != sensorName) {
				map.put(sensorId, sensorName);
			}
			configCounter++;
		}
		SensorsProperty sp = null;
		if(!map.isEmpty()) {
			sp = new SensorsProperty();
			sp.setSensors(map);
			sp.setCron(config.getString("af.sensor.cron"));
		}
		return sp;
	}
	
	public void reload() {
		try {
			factory.refresh();
			config = factory.getConfiguration();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
}
