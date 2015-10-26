package addams.family.web;

import static spark.Spark.get;
import static spark.Spark.post;
//import org.sqlite.JDBC;
import spark.servlet.SparkApplication;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class WebInterface  implements SparkApplication {
	
	private static Connection con;
	
	@Override
	public void init() {
		
		post("/lightMax", (request, response) -> {
			setBrightness(200);
			return "Light is MAX: " + getBrithtness();
		});
		
		post("/lightMin", (request, response) -> {
			setBrightness(10);
			return "Light is MIN: " + getBrithtness();
		});
		
		post("/lightOff", (request, response) -> {
			setBrightness(0);
			return "Light is Off: " + getBrithtness();
		});
		
		get("/lightCurrent", (request, response) -> {
			return "Light is: " + getBrithtness();
		});
		
		post("/overrideLight_100_ON", (request, response) -> {
			return "overrideLight_100_ON";
		});

		post("/overrideLight_100_OFF", (request, response) -> {
			return "overrideLight_100_OFF";
		});
		
		post("/overrideLight_5_ON", (request, response) -> {
			return "overrideLight_5_ON";
		});

		post("/overrideLight_5_OFF", (request, response) -> {
			return "overrideLight_5_OFF";
		});
		
		post("/knock_1", (request, response) -> {
			return "knock_1";
		});
		
		post("/knock_2", (request, response) -> {
			return "knock_2";
		});
		
		get("/", (request, response) -> {
            return "Addams Family Translite Project";
        });


		// http://mustache.github.io/mustache.5.html
        // https://github.com/perwendel/spark
	}
	
	public static void main(String[] args) {
		System.setProperty("java.io.tmpdir", "/home/pi/display/controll/tmp");
		WebInterface wa = new WebInterface();
		try {
			wa.dbInit();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wa.init();
	}
	
	private String getBrithtness() throws SQLException {
		Statement stat = con.createStatement();
		String value = null;
		ResultSet res = stat.executeQuery("select * from af_overrides");
		while (res.next()) {
			value = res.getString("value");
		}
		return value;
	}
	
	private void setBrightness(int brightness) throws SQLException {
		  Statement stat = con.createStatement();
		  PreparedStatement prep = con
				    .prepareStatement("update af_overrides set value=? where paramName=?;");
				  prep.setString(1, ""+brightness);
				  prep.setString(2, "backlightBrightness");
				  prep.execute();
	}
	
	private void dbInit() throws ClassNotFoundException, SQLException {
		  Class.forName("org.sqlite.JDBC");
		  // database path, if it's new database,
		  // it will be created in the project folder
		  con = DriverManager.getConnection("jdbc:sqlite:/home/pi/display/controll/AddamsFamily.db");
		  Statement stat = con.createStatement();
		  stat.executeUpdate("drop table if exists af_overrides");
		 
		  //creating table
		  stat.executeUpdate("create table af_overrides(id integer, paramName varchar(30), value INT, primary key (id));");
		 
		  // inserting data
		  PreparedStatement prep = con
		    .prepareStatement("insert into af_overrides values(?,?,?);");
		  prep.setString(2, "backlightBrightness");
		  prep.setString(3, "10");
		  prep.execute();
	}
}
