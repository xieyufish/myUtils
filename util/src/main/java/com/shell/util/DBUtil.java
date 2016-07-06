package com.shell.util;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {

	private Connection conn = null;

	private PreparedStatement preparedStatement;

	private Properties pro = new Properties();

	private String driver = "com.mysql.jdbc.Driver";

	private String url;

	private String userName;

	private String password;

	public DBUtil() {
		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream("config/properties/db.properties");
			pro.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public PreparedStatement getPreparedStatementBySql(String sql) {
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(getUrl(), getUserName(), getPassword());// 获取连接
			preparedStatement = conn.prepareStatement(sql);// 准备执行语句
		} catch (Exception e) {
			e.printStackTrace();
		}
		return preparedStatement;
	}
	
	public Connection getConnection() {
		if (this.conn == null) {
			try {
				Class.forName(driver);
				this.conn = DriverManager.getConnection(getUrl(), getUserName(), getPassword());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return this.conn;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		if (null == url) {
			url = pro.getProperty("db.url");
		}
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		if (null == userName) {
			userName = pro.getProperty("db.username");
		}
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		if (null == password) {
			password = pro.getProperty("db.password");
		}
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public static void main(String[] args) throws SQLException {
		DBUtil dbUtil = new DBUtil();
		PreparedStatement preparedStatement = dbUtil.getPreparedStatementBySql("select id, time from tttt");
		ResultSet resultSet = preparedStatement.executeQuery();
		while(resultSet.next()) {
			String time = resultSet.getString("time");
			System.out.println(time);
		}
		
		
	}

}
