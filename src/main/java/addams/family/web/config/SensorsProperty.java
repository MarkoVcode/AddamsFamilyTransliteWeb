package addams.family.web.config;

import java.util.Map;

public class SensorsProperty {
	private Map<String,String> sensors;
	private String cron;
	
	public Map<String, String> getSensors() {
		return sensors;
	}
	public void setSensors(Map<String, String> sensors) {
		this.sensors = sensors;
	}
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}
}
