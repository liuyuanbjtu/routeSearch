package com.hdxinfo.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import oracle.jdbc.pool.OracleDataSource;

public class DbManager {
	public static OracleDataSource ods;
	private static String url;

	private static String username;

	private static String password;
	static {
		try {
			String path = DbManager.class.getPackage().getName().replaceAll(
					"\\.", "/")
					+ "/dbpool.properties";

			InputStream input = DbManager.class.getClassLoader()
					.getResourceAsStream(path);
			Properties props = new Properties();
			props.load(input);
			username = props.getProperty("username");
			password = props.getProperty("password");
			url = props.getProperty("url");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("��ȡ�����ļ����?");
		}

		try {
			ods = new OracleDataSource();
			ods.setImplicitCachingEnabled(true);

			ods.setURL(url);
			ods.setUser(username);
			ods.setPassword(password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection() {
		Connection connection = null;
		try {
			connection = DbManager.ods.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return connection;
	}

	public static void closeAll(ResultSet rs, Statement stmt, Connection conn) {
		try {
			if (rs != null) {
				rs.close();
			}
			stmt.close();
			conn.close();
		} catch (Exception e) {
		}
	}
}
