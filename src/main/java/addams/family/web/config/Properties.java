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
	
	private static final String CONFIG_FILE = "config/config.xml";
	private DefaultConfigurationBuilder factory;
	public Properties() {
		try {
			factory = new DefaultConfigurationBuilder(CONFIG_FILE);
			config = factory.getConfiguration();
		} catch (ConfigurationException e) {
			e.printStackTrace();
			System.exit(1);
		}	
	}
	
	public String getTOCKey()
	{
		return config.getString("integration.toc.access.key");
	}
	
	public String getTOCSecret()
	{
		return config.getString("integration.toc.access.secret");
	}

	public String get1WSensorPath()
	{
		return config.getString("af.sensor.1w.path");
	}	
	
	public String get1WSensorValueFile()
	{
		return config.getString("af.sensor.1w.value.file");
	}

	public String getDBPath() {
		return config.getString("af.db.path");
	}

	public String getDBName() {
		return config.getString("af.db.name");
	}
	
	public List<AudioProperty> getAudioProperties() {
		try {
			factory.refresh();
			config = factory.getConfiguration();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
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

	public Map<String,String> get1WSensors()
	{
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
		return map;
	}
}
