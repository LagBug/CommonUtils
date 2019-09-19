package me.lagbug.common.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import me.lagbug.common.CommonUtils;

public class MySQL {

	private String host, database, table, username, password, statement;
	private int port;
	private Connection connection;

	public MySQL(String host, String database, String table, String username, String password, String statement, int port) {
		this.host = host;
		this.database = database;
		this.table = table;
		this.username = username;
		this.password = password;
		this.statement = statement;
		this.port = port;
	}

	public boolean connect() {
		CommonUtils.forceLog("Attempting to connect to the MySQL database");

		try {
			if (connection != null) {
				connection.close();
			}

			connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + statement,
					username, password);

			CommonUtils.forceLog("Successfully connected to the MySQL database");
			prepareStatement("CREATE TABLE IF NOT EXISTS " + table + "  (player_uuid VARCHAR(36), verify_date TEXT, PRIMARY KEY(player_uuid))").execute();
			return true;
		} catch (SQLException ex) {
			CommonUtils.forceLog("[CaptchaX] Could not connect to the MySQL database");
			ex.printStackTrace();
			return false;
		}
	}
	
	public Connection getConnection() {
		return connection;
	}

	public PreparedStatement prepareStatement(String query) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(query);
			ps.setQueryTimeout(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ps;
	}
}