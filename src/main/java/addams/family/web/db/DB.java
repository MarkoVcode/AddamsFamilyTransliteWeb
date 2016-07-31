package addams.family.web.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import addams.family.web.config.Properties;

public class DB {

	private static String VAL_LIGHT_EXT = "lightEXT";
	private static String VAL_LIGHT_INT = "lightINT";
	
	private static String OVRR_BACKLIGHT = "backlightBrightness";
	
	private static String TABLE_OVERRIDES = "af_overrides";
	public static String TABLE_ADCVALUES = "af_adcvalues";
	public static String TABLE_1WVALUES = "af_1wvalues";
	public static String TABLE_I2CVALUES = "af_i2cvalues";
	public static String TABLE_EBRIGHTVALUES = "af_ebrightvalues";
	
	private static Connection con;
	private Properties prop;
	private static volatile DB INSTANCE;
	
	private static Logger LOG;
	
	private DB() {
		LOG = LoggerFactory.getLogger(DB.class);
		prop = Properties.getInstance();
		try {
			if(!dbExists()) {
				LOG.info("Creating new DB: " + prop.getDB());
				dbInit();
			} else {
				LOG.info("Re-init DB: " + prop.getDB());
				dbInit();
				//Class.forName("org.sqlite.JDBC");
				//con = DriverManager.getConnection("jdbc:sqlite:" + prop.getDB());
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static DB getInstance() {
		if(null == INSTANCE) {
			synchronized (DB.class) {
				if(null == INSTANCE) {
					INSTANCE = new DB();
				}
			}
		}
		return INSTANCE;
	}

	private boolean dbExists() {
		File fdir = new File(prop.getDBPath());
		if(fdir.exists() && fdir.isDirectory()) { 
			File fdb = new File(prop.getDB());
			if(fdb.exists() && !fdb.isDirectory()) { 
			    return true;
			}
		} else {
			new File(prop.getDBPath()).mkdir();
			return false;
		}
		return false;
	}
	
	private void dbInit() throws ClassNotFoundException, SQLException {
		  Class.forName("org.sqlite.JDBC");
		  con = DriverManager.getConnection("jdbc:sqlite:" + prop.getDB());		  
		  Statement stat = con.createStatement();
		  stat.executeUpdate("drop table if exists " + TABLE_OVERRIDES);
		  stat.executeUpdate("drop table if exists " + TABLE_ADCVALUES);
		  stat.executeUpdate("drop table if exists " + TABLE_1WVALUES);
		  stat.executeUpdate("drop table if exists " + TABLE_EBRIGHTVALUES);		  
		  //creating table
		  stat.executeUpdate("create table " + TABLE_OVERRIDES + "(id integer, paramName varchar(30), value INT, primary key (id));");
		  stat.executeUpdate("create table " + TABLE_ADCVALUES + "(id integer, paramName varchar(30), value varchar(30), eDate varchar(30), primary key (id));");
		  stat.executeUpdate("create table " + TABLE_1WVALUES + "(id integer, paramName varchar(30), value varchar(30), eDate varchar(30), primary key (id));");
		  stat.executeUpdate("create table " + TABLE_EBRIGHTVALUES + "(id integer, paramName varchar(30), value INT, primary key (id));");
		  // inserting data
		  PreparedStatement prepOverrides = con.prepareStatement("insert into " + TABLE_OVERRIDES + " values(?,?,?);");
		  prepOverrides.setString(2, OVRR_BACKLIGHT);
		  prepOverrides.setString(3, "" + prop.getMINBrightness());
		  prepOverrides.execute();

		  PreparedStatement prep1W = con.prepareStatement("insert into " + TABLE_1WVALUES + " values(?,?,?,?);");
		  for(Map.Entry<String, String> entry : prop.get1WSensorsProperty().getSensors().entrySet()) {
			  prep1W.setString(2, entry.getValue());
			  prep1W.setString(3, "-273");
			  prep1W.setString(4, "today");		  
			  prep1W.execute();
		  }
		  
		  PreparedStatement prep = con.prepareStatement("insert into " + TABLE_ADCVALUES + " values(?,?,?,?);");
		  prep.setString(2, VAL_LIGHT_EXT);
		  prep.setString(3, "n/a");
		  prep.setString(4, "now");
		  prep.execute();
		  prep.setString(2, VAL_LIGHT_INT);
		  prep.setString(3, "n/a");
		  prep.setString(4, "now");
		  prep.execute();
		  
		  PreparedStatement prepE = con.prepareStatement("insert into " + TABLE_EBRIGHTVALUES + " values(?,?,?);");
		  prepE.setString(2, "E1");
		  prepE.setInt(3, 0);
		  prepE.execute();
		  prepE.setString(2, "E2");
		  prepE.setInt(3, 0);
		  prepE.execute();
		  prepE.setString(2, "E3");
		  prepE.setInt(3, 0);
		  prepE.execute();
		  prepE.setString(2, "E4");
		  prepE.setInt(3, 0);
		  prepE.execute();		  
	}

	public String getValues() throws SQLException {
		Statement stat = con.createStatement();
		StringBuilder sp = new StringBuilder();
		ResultSet res = stat.executeQuery("select * from " + TABLE_ADCVALUES);
		while (res.next()) {
			sp.append(res.getString("paramName") + ": " + res.getString("value") + ": " + res.getString("eDate") + "; ");
		}
		ResultSet res1W = stat.executeQuery("select * from " + TABLE_1WVALUES);
		while (res.next()) {
			sp.append(res1W.getString("paramName") + ": " + res1W.getString("value") + ": " + res1W.getString("eDate") + "; ");
		}
		return sp.toString();
	}

	public void setBrightness(int brightness) throws SQLException {
		  PreparedStatement prep = con
				    .prepareStatement("update " + TABLE_OVERRIDES + " set value=? where paramName=?;");
				  prep.setString(1, ""+brightness);
				  prep.setString(2, OVRR_BACKLIGHT);
				  prep.execute();
	}
	
	public void setExternalBrightness(String channel, int value) throws SQLException {
		  PreparedStatement prep = con
				    .prepareStatement("update " + TABLE_EBRIGHTVALUES + " set value=? where paramName=?;");
				  prep.setString(1, ""+value);
				  prep.setString(2, channel);
				  prep.execute();
	}
	
	public String getExternalBrightness(String channel) throws SQLException  {
		Statement stat = con.createStatement();
		String value = "";
		ResultSet res = stat.executeQuery("select * from " + TABLE_EBRIGHTVALUES + " where paramName='" + channel + "'");
		while (res.next()) {
			value = res.getString("value");
		}
		return value;
	}
	
	public String getExternalBrightnessJSON() throws SQLException {
		Statement stat = con.createStatement();
		StringBuilder value = new StringBuilder();
		ResultSet res = stat.executeQuery("select * from " + TABLE_EBRIGHTVALUES);
		while (res.next()) {
			value.append("\"" + res.getString("paramName") + "\":\"" + res.getString("value") + "\",");
		}
		return "{" + value.substring(0, value.length()-1) + "}";
	}
	
/*	public BrightnessBean getExternalBrightnessBean() throws SQLException {
		BrightnessBean bb = new BrightnessBean();
		Map<String, String> m = new HashMap<String, String>();
		Statement stat = con.createStatement();
		ResultSet res = stat.executeQuery("select * from " + TABLE_EBRIGHTVALUES);
		while (res.next()) {
			m.put(res.getString("paramName"), res.getString("value"));
		}
		bb.values = m;
		return bb;
	}*/
	
	public String getBrithtness() throws SQLException {
		Statement stat = con.createStatement();
		String value = null;
		ResultSet res = stat.executeQuery("select * from " + TABLE_OVERRIDES);
		while (res.next()) {
			value = res.getString("value");
		}
		return value;
		
	}
	
	public void saveValue(String table, String name, String value) throws SQLException {
		  PreparedStatement prep = con
				    .prepareStatement("update " + table + " set value=?, eDate=? where paramName=?;");
				  prep.setString(1, value);
				  prep.setString(3, name);
				  prep.setString(2, "now");
				  prep.execute();
	}
}
