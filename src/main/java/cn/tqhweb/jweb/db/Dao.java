package cn.tqhweb.jweb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.tqhweb.jweb.App;
import cn.tqhweb.jweb.util.log.Logger;

public class Dao {
	static private Logger logger = Logger.factory(Dao.class);

	static public interface SQLRunnable {
		public void run(Connection connection) throws SQLException;
	}

	static public Connection getConnection() {
		return App.getApp().getConnection();
	}

	static public void addBatch(String sql, Object... param) throws SQLException {
		List<String> sqls = App.getApp().getBatchList();
		StringBuilder builder = new StringBuilder();
		int fromIndex = 0;
		for (int i = 0; i < param.length; i++) {
			int end = sql.indexOf('?', fromIndex);
			if (end == -1) {
				throw new RuntimeException("the number of '?' is not enough for params");
			}
			builder.append(sql, fromIndex, end);
			if (param[i] instanceof Number) {
				builder.append(param[i].toString());
			} else {
				builder.append('\'').append(param[i].toString()).append('\'');
			}
			fromIndex = end + 1;
		}
		sql = builder.append(sql, fromIndex, sql.length()).toString();
		sqls.add(sql);
	}

	static private PreparedStatement prepare(String sql, Object... param) throws SQLException {
		logger.debug("[SQL]=>" + sql);
		PreparedStatement statement = getConnection().prepareStatement(sql);
		for (int i = 0; i < param.length; i++) {
			statement.setObject(i + 1, param[i]);
		}
		return statement;
	}

	/**
	 * 查询数据
	 * 
	 * @return List<Record>(不返回null)
	 */
	static public List<Record> select(String sql, Object... param) {
		List<Record> list = new ArrayList<>();
		try {
			PreparedStatement statement = prepare(sql, param);
			ResultSet resultSet = statement.executeQuery();
			ResultSetMetaData meta = resultSet.getMetaData();
			int count = meta.getColumnCount();
			while (resultSet.next()) {
				Record map = new Record();
				for (int i = 1; i <= count; i++) {
					map.put(meta.getColumnName(i), resultSet.getObject(i));
				}
				list.add(map);
			}
			resultSet.close();
			statement.close();
			return list;
		} catch (SQLException e) {
			logger.fatal("查询异常 [%s]", e.getMessage());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	static public int update(boolean delay, String sql, Object... param) throws SQLException {
		if (delay) {
			addBatch(sql, param);
			return 1;
		} else {
			return update(sql, param);
		}
	}

	static public int update(String sql, Object... param) throws SQLException {
		PreparedStatement statement = prepare(sql, param);
		int n = statement.executeUpdate();
		statement.close();
		return n;
	}

	static public void execute(boolean delay, String sql, Object... param) throws SQLException {
		if (delay) {
			addBatch(sql, param);
		} else {
			execute(sql, param);
		}
	}

	static public void execute(String sql, Object... param) throws SQLException {
		PreparedStatement statement = prepare(sql, param);
		statement.addBatch();
		statement.addBatch();
		statement.execute();
		statement.close();
	}

	static public void insert(boolean delay, String sql, Object... param) throws SQLException {
		execute(delay, sql, param);
	}

	static public void insert(String sql, Object... param) throws SQLException {
		execute(sql, param);
	}

	static public void delete(boolean delay, String sql, Object... param) throws SQLException {
		execute(delay, sql, param);
	}

	static public void delete(String sql, Object... param) throws SQLException {
		execute(sql, param);
	}

	/**
	 * 事务处理(自动回滚)
	 * 
	 * @param runnable
	 * @throws SQLException
	 */
	static public void transact(SQLRunnable runnable) throws SQLException {
		Connection connection = getConnection();
		try {
			connection.setAutoCommit(false);
			runnable.run(connection);
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			logger.warn("SQLException :%s", e.getMessage());
			connection.rollback();
		}
	}

	static public String whereBuild(List<String> list) {
		StringBuilder sql = new StringBuilder();
		for (String string : list) {
			sql.append(string).append("=? AND ");
		}
		return sql.substring(0, sql.length() - 5);
	}

	static public String whereBuild(Map<String, Object> map) {
		StringBuilder sql = new StringBuilder(" where ");
		for (Entry<String, Object> entry : map.entrySet()) {
			sql.append(entry.getKey()).append("?=").append(entry.getValue()).append(" AND ");
		}
		return sql.substring(0, sql.length() - 5);
	}

	static public String whereOrBuild(Collection<String> list) {
		StringBuilder sql = new StringBuilder();
		for (String string : list) {
			sql.append(string).append("=? OR ");
		}
		return sql.substring(0, sql.length() - 4);
	}

	static public String whereOrBuild(Map<String, Object> map) {
		StringBuilder sql = new StringBuilder(" where ");
		for (Entry<String, Object> entry : map.entrySet()) {
			sql.append(entry.getKey()).append("?=").append(entry.getValue()).append(" OR ");
		}
		return sql.substring(0, sql.length() - 4);
	}
}
