package cn.tqhweb.jweb.plugin.datasource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import cn.tqhweb.jweb.plugin.Plugin;

public abstract class DataSourcePlugin implements Plugin {
	private static DataSourcePlugin dataSourcePlugin;
	protected static Properties properties = new Properties();
	static {
		try {
			properties.load(DataSourcePlugin.class.getClassLoader().getResourceAsStream("sql.properties"));
		} catch (IOException e) {
		}
	}
	public static DataSourcePlugin getDataSourcePlugin() {
		return dataSourcePlugin;
	}
	protected static void register(DataSourcePlugin dataSourcePlugin) {
		DataSourcePlugin.dataSourcePlugin = dataSourcePlugin;
	}
	public static DataSource getDataSource() {
		if (dataSourcePlugin == null) {
			return null;
		}
		return dataSourcePlugin._getDataSource();
	}
	private static boolean jdbcRegistered = false;
	public static Connection getConnection() throws SQLException {
		if (dataSourcePlugin == null) {
			if (!jdbcRegistered) {
				try {
					Class.forName(properties.getProperty("driverClassName"));
					jdbcRegistered = true;
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
			return DriverManager.getConnection(properties.getProperty("url"), properties);
		}
		return dataSourcePlugin._getDataSource().getConnection();
	}
	protected abstract DataSource _getDataSource();
}
