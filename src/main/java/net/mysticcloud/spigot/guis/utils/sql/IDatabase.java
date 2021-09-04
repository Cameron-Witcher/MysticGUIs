package net.mysticcloud.spigot.guis.utils.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * The IDatabase is a simple class that adds all the utils you need to
 * communicate with an SQL server.
 */
public class IDatabase {

	public Connection connection;
	private Properties properties;
	private String url;
	private SQLDriver driver;

	public IDatabase(SQLDriver driver, String db) {
		this.url = "jdbc:sqlite:/sqlite/db/" + db;
		this.driver = driver;
	}

	public IDatabase(SQLDriver driver, String host, String database, Integer port, String username, String password) {
		this.properties = new Properties();
		this.properties.setProperty("user", username);
		this.properties.setProperty("password", password);
		this.url = "jdbc:mysql://" + host + ":" + port + "/" + database;
		this.driver = driver;
	}

	public Boolean init() throws SQLException {
//		      String url = "jdbc:mysql://localhost:3306/test";
//		      Properties info = new Properties();
//		      info.put("user", "root");
//		      info.put("password", "test");
//			

		try {
			if (driver.equals(SQLDriver.MYSQL_OLD)) {
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection(url, properties);
			}
			if (driver.equals(SQLDriver.MYSQL)) {
				Class.forName("com.mysql.cj.jdbc.Driver");
				connection = DriverManager.getConnection(url, properties);
			}
			if (driver.equals(SQLDriver.SQLITE)) {
				Class.forName("org.sqlite.JDBC");
				connection = DriverManager.getConnection(url);
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (connection != null) {
			DatabaseMetaData meta = connection.getMetaData();
			System.out.println("Successfully connected to database");
			System.out.println("Driver: " + meta.getDriverName());
			return true;
		}

//		try {
//			if (connection != null && !connection.isClosed()) {
//				return true;
//			}
//			Class.forName("com.mysql.jbdc.Driver");
//			this.connection = DriverManager.getConnection(url, user, pass);
//			return true;
//		} catch (Exception exception) {
//			exception.printStackTrace();
//			return false;
		return false;
//		}
	}

	public ResultSet query(String query, Object... values) {
		try {
			PreparedStatement statement = prepare(query, values);
			if (statement == null) {
				return null;
			}
			return statement.executeQuery();
		} catch (Exception exception) {
			return null;
		}
	}

	public Integer update(String query, Object... values) {
		try {
			PreparedStatement statement = prepare(query, values);
			if (statement == null) {
				return -1;
			}
			return statement.executeUpdate();
		} catch (Exception exception) {
			exception.printStackTrace();
			return -1;
		}

	}

	public boolean input(String query, Object... values) {
		try {
			PreparedStatement statement = prepare(query, values);
			if (statement == null) {
				return false;
			}
			return statement.execute();
		} catch (Exception exception) {
			return false;
		}

	}

	public boolean tableExist(String tableName) throws SQLException {
		boolean tExists = false;
		try (ResultSet rs = connection.getMetaData().getTables(null, null, tableName, null)) {
			while (rs.next()) {
				String tName = rs.getString("TABLE_NAME");
				if (tName != null && tName.equals(tableName)) {
					tExists = true;
					break;
				}
			}
		}
		return tExists;
	}

	private PreparedStatement prepare(String query, Object... values) {
		try {
			if (!init()) {
				return null;
			}
			PreparedStatement statement = connection.prepareStatement(query);
			for (int i = 0; i < values.length; i++) {
				statement.setObject(i + 1, values[i]);
			}
			return statement;
		} catch (Exception exception) {
			return null;
		}
	}
}
