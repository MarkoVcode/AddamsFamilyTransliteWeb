package addams.family.web.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

//	curl --header 'Authorization: TOCen KuHATAIKhryGJZrT4yhHw0PjYiPVYLBJFKEWItu3' --header 'Content-Type: application/json' -d '{"values":[{"name":"windSpeed","value":"6.23"},{"name":"tempOut","value":"28.7"}]}' -X POST https://testprvapi.thingoncloud.com/v1/service/direct/D9xsCC7hGrQqlS4B5T
    public List<ThingOnCloudProperty> getTOCProperty()
	{
        List<ThingOnCloudProperty> props = new ArrayList<>();
        int index = 1;
        while(null != getSingleTOCProperty(index)) {
            props.add(getSingleTOCProperty(index));
            index++;
        }
        return props;
	}

	private ThingOnCloudProperty getSingleTOCProperty(int index)
    {
        ThingOnCloudProperty prop = new ThingOnCloudProperty();
        prop.log = config.getBoolean("toc.integration.direct.log", false);
        String url = config.getString("toc.integration.direct."+index+".url");
        String service = config.getString("toc.integration.direct."+index+".service");
        String tocen = config.getString("toc.integration.direct."+index+".tocen");
        if(null == url && index == 1) {
            prop.url = "https://testprvapi.thingoncloud.com/v1/service/direct";
            prop.service = "D9xsCC7hGrQqlS4B5T";
            prop.tocen = "KuHATAIKhryGJZrT4yhHw0PjYiPVYLBJFKEWItu3";
            return prop;
        } else if (null != url) {
            prop.url = url;
            prop.service = service;
            prop.tocen = tocen;
            return prop;
        }
        return null;
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
