package cn.tqhweb.jweb.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Table {

	static Map<String, Table> tables = new ConcurrentHashMap<String, Table>();

	private String name;
	private Map<String, String> column = new HashMap<String, String>();
	private List<String> pk = new ArrayList<String>();

	private Table(String tableName) {
		this.name = tableName;
		try {
			Connection connection = Dao.getConnection();
			DatabaseMetaData meta = connection.getMetaData();
			String catalog = connection.getCatalog();
			// 获取字段
			ResultSet resultSet = meta.getColumns(catalog, null, tableName, null);
			while (resultSet.next()) {
				String key = resultSet.getString("COLUMN_NAME");
				String value = resultSet.getString("TYPE_NAME");
				column.put(key, value);
			}
			resultSet.close();
			// 获取主键
			resultSet = meta.getPrimaryKeys(catalog, null, tableName);
			while (resultSet.next()) {
				String key = resultSet.getString("COLUMN_NAME");
				pk.add(key);
			}
			resultSet.close();
		} catch (SQLException e) {
		}
	}

	public List<String> getPk() {
		if (pk == null) {
			return null;
		} else {
			return pk;
		}
	}

	/**
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public Map<String, String> getColumn() {
		if (column == null) {
			return null;
		} else {
			return column;
		}
	}

	public boolean containsColumn(String name) {
		return column.containsKey(name);
	}

	static public Table getTable(String tableName) {
		Table table = tables.get(tableName);
		if (table != null) {
			return table;
		} else {
			table = new Table(tableName);
			tables.putIfAbsent(tableName, table);
			return table;
		}
	}
}
