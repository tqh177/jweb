package cn.tqhweb.jweb.plugin.datasource;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

public class Druid extends DataSourcePlugin {
	private static Druid self;

	private static DruidDataSource dataSource;
	// 保证最后一个应用退出时注销
	private static int num = 0;

	public static synchronized Druid getInstance() {
		if (self == null) {
			return self = new Druid();
		}
		return self;
	}

	private Druid() {
	}

	@Override
	public synchronized boolean start() {
		num++;
		if (dataSource != null) {
			return true;
		}
		try {
			dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
			register(this);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public synchronized boolean stop() {
		num--;
		if (num > 0) {
			return true;
		}
		dataSource.close();
		register(null);
		dataSource = null;
		return true;
	}

	@Override
	protected DataSource _getDataSource() {
		return dataSource;
	}

}
