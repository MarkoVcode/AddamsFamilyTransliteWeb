package addams.family.web;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import addams.family.web.config.AudioProperty;
import addams.family.web.config.Properties;
import addams.family.web.scheduler.SchedulerThread;
//import org.sqlite.JDBC;
import spark.servlet.SparkApplication;

public class WebInterface  implements SparkApplication {

	private static String DB;
	
	private static String VAL_TEMP_PWS = "tempPWS";
	private static String VAL_TEMP_EXT = "tempEXT";
	private static String VAL_LIGHT_EXT = "lightEXT";
	private static String VAL_LIGHT_INT = "lightINT";
	
	private static String OVRR_BACKLIGHT = "backlightBrightness";
	
	private static String TABLE_OVERRIDES = "af_overrides";
	private static String TABLE_VALUES = "af_values";
	
	private static int BACKLIGHT_MIN = 10;
	private static int BACKLIGHT_MAX = 200;
	private static int BACKLIGHT_OTT = 255;
	
	private static Connection con;
	private Properties prop;
	private SchedulerThread st;
	
	public WebInterface() {
		prop = new Properties();
		DB = prop.getDBPath() + "/" + prop.getDBName();
		System.out.println(prop.getTOCKey());
		List<AudioProperty> ap = prop.getAudioProperties();
		System.out.println(prop.getTOCKey());
		st = new SchedulerThread(ap);
		st.start();
	}
	
	@Override
	public void init() {
		
		post("/lightMax", (request, response) -> {
			setBrightness(BACKLIGHT_MAX);
			return "Light is MAX: " + getBrithtness();
		});
		
		post("/lightMin", (request, response) -> {
			setBrightness(BACKLIGHT_MIN);
			return "Light is MIN: " + getBrithtness();
		});

		post("/lightOTT", (request, response) -> {
			setBrightness(BACKLIGHT_OTT);
			return "Light is OTT: " + getBrithtness();
		});
		
		post("/lightOff", (request, response) -> {
			setBrightness(0);
			return "Light is Off: " + getBrithtness();
		});
		
		post("/cronReload", (request, response) -> {
			reloadScheduler();
			return "Reloaded";
		});
		
		get("/lightCurrent", (request, response) -> {
			return "Light is: " + getBrithtness();
		});
		
		get("/values", (request, response) -> {
			return "Values: " + getValues();
		});
				
		get("/", (request, response) -> {
            return "Addams Family Translite Project";
        });
	}
	
	private void reloadScheduler() {
		st.reload(prop.getAudioProperties());
	}
	
	public static void main(String[] args) {
	//System.setProperty("java.io.tmpdir", "/home/pi/display/controll/tmp");
		WebInterface wa = new WebInterface();
		
		try {
			if(!wa.dbExists()) {
				System.out.println("Creating DB.");
				wa.dbInit();
			} else {
				Class.forName("org.sqlite.JDBC");
				con = DriverManager.getConnection("jdbc:sqlite:" + DB);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		wa.init();
	}
	
	private String getBrithtness() throws SQLException {
		Statement stat = con.createStatement();
		String value = null;
		ResultSet res = stat.executeQuery("select * from " + TABLE_OVERRIDES);
		while (res.next()) {
			value = res.getString("value");
		}
		return value;
	}

	private String getValues() throws SQLException {
		Statement stat = con.createStatement();
		String value = "";
		ResultSet res = stat.executeQuery("select * from " + TABLE_VALUES);
		while (res.next()) {
			value = value + res.getString("paramName") + ": " + res.getString("value") + "; ";
		}
		return value;
	}

	private boolean dbExists() {
		File fdir = new File(prop.getDBPath());
		if(fdir.exists() && fdir.isDirectory()) { 
			File fdb = new File(DB);
			if(fdb.exists() && !fdb.isDirectory()) { 
			    return true;
			}
		} else {
			new File(prop.getDBPath()).mkdir();
			return false;
		}
		return false;
	}
	
	private void setBrightness(int brightness) throws SQLException {
		//  Statement stat = con.createStatement();
		  PreparedStatement prep = con
				    .prepareStatement("update " + TABLE_OVERRIDES + " set value=? where paramName=?;");
				  prep.setString(1, ""+brightness);
				  prep.setString(2, OVRR_BACKLIGHT);
				  prep.execute();
	}
	
	private void dbInit() throws ClassNotFoundException, SQLException {
		  Class.forName("org.sqlite.JDBC");
		  con = DriverManager.getConnection("jdbc:sqlite:" + DB);		  
		  Statement stat = con.createStatement();
		  stat.executeUpdate("drop table if exists " + TABLE_OVERRIDES);
		  stat.executeUpdate("drop table if exists " + TABLE_VALUES);
		  //creating table
		  stat.executeUpdate("create table " + TABLE_OVERRIDES + "(id integer, paramName varchar(30), value INT, primary key (id));");
		  stat.executeUpdate("create table " + TABLE_VALUES + "(id integer, paramName varchar(30), value varchar(30), primary key (id));");
		  // inserting data
		  PreparedStatement prepOverrides = con.prepareStatement("insert into " + TABLE_OVERRIDES + " values(?,?,?);");
		  prepOverrides.setString(2, OVRR_BACKLIGHT);
		  prepOverrides.setString(3, ""+BACKLIGHT_MIN);
		  prepOverrides.execute();
		  
		  PreparedStatement prep = con.prepareStatement("insert into " + TABLE_VALUES + " values(?,?,?);");
		  prep.setString(2, VAL_TEMP_PWS);
		  prep.setString(3, "n/a");
		  prep.execute();
		  prep.setString(2, VAL_TEMP_EXT);
		  prep.setString(3, "n/a");
		  prep.execute();
		  prep.setString(2, VAL_LIGHT_EXT);
		  prep.setString(3, "n/a");
		  prep.execute();
		  prep.setString(2, VAL_LIGHT_INT);
		  prep.setString(3, "n/a");
		  prep.execute();
	}
}
