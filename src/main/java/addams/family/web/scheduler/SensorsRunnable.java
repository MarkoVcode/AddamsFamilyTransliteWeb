package addams.family.web.scheduler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.LoggerFactory;

import addams.family.web.config.Properties;
import addams.family.web.config.SensorsProperty;
import addams.family.web.db.DB;

public class SensorsRunnable implements Runnable {
	
	private static org.slf4j.Logger LOG;
	private Map<String, String> sensorsPaths;
	private Properties prop;
	private DB db;
	private Pattern p;
	
	public SensorsRunnable(SensorsProperty sp) {
		LOG = LoggerFactory.getLogger(SensorsRunnable.class);
		db = DB.getInstance();
		prop = Properties.getInstance();
		sensorsPaths = formSensorPaths(sp);
		p = Pattern.compile("([0-9-]{4,6})");
	}
	
	private Map<String, String> formSensorPaths(SensorsProperty sp) {
		Map<String, String> sensorsPaths = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : sp.getSensors().entrySet()) {
			String sPath = prop.get1WSensorPath() + "/" + entry.getKey() + "/" + prop.get1WSensorValueFile();
			sensorsPaths.put(sPath, entry.getValue());
		}
		return sensorsPaths;	
	}
	
/*
 	/sys/bus/w1/devices/28-0215629dc5ff/w1_slave
	d1 01 4b 01 7f ff 0c 10 81 : crc=81 YES
	d1 01 4b 01 7f ff 0c 10 81 t=29062
*/
	
	private String readFileAsString(String filePath){
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = null;
        	try {
				reader = new BufferedReader(new FileReader(filePath));
		        char[] buf = new char[1024];
		        int numRead=0;
		        while((numRead=reader.read(buf)) != -1) {
		            String readData = String.valueOf(buf, 0, numRead);
		            fileData.append(readData);
		        }
            	LOG.info(filePath + ": " +fileData.toString());
			} catch (IOException e) {
				LOG.error(filePath + ": " +e.getMessage());
			} finally {
		        try {
		        	if(null != reader)
		        		reader.close();
				} catch (IOException e) {
					LOG.error(filePath + ": " +e.getMessage());
				}
			}
        return fileData.toString();
    }
	
	private String readValue(String sensor) {
		String value = null;
		if(null != sensor) {
			Matcher m = p.matcher(sensor);
			if (m.find()) {
			    value = m.group(1);
			}
		}
		return value;
	}
	
	@Override
	public void run() {
		for (Map.Entry<String, String> entry : sensorsPaths.entrySet()) {
			String value = readValue(readFileAsString(entry.getKey()));
			String name = entry.getValue();
			LOG.info("Temperature: " + name + ": " + value);
			try {
				db.saveSensorValue(name, value);
			} catch (SQLException e) {
				LOG.error("Could not save: " + name + ": " + value + e.getMessage());
			}
		}
	}
}
