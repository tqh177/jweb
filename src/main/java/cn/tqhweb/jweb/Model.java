package cn.tqhweb.jweb;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.tqhweb.jweb.db.Dao;
import cn.tqhweb.jweb.db.Pagination;
import cn.tqhweb.jweb.db.Record;
import cn.tqhweb.jweb.db.Table;
import cn.tqhweb.jweb.util.json.JSON;

@SuppressWarnings("unchecked")
public abstract class Model<M extends Model<M>> implements JavaBean {

	private static final long serialVersionUID = 4744821947975870800L;
	protected String tableName;
	protected Map<String, Object> origin = new HashMap<>();
	protected Map<String, Object> data = new HashMap<>();

	public Model() {
		init();
		if (tableName == null) {
			tableName = getClass().getSimpleName().toLowerCase();
		}
	}

	private boolean isUpdate = false;
	private Thread threadLock;

	// update=true时加锁，update=false时解锁
	public synchronized void isUpdate(boolean update) {
		if (threadLock == null) {
			// 没有被标记的线程 标记当前线程 加锁
			threadLock = Thread.currentThread();
		} else if (!threadLock.equals(Thread.currentThread())) {
			// 不是被标记的线程 等待
			try {
				synchronized (threadLock) {
					while (threadLock != null) {
						threadLock.wait(3000);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				// 锁已释放 标记当前线程 加锁
				threadLock = Thread.currentThread();
			}
		}
		// 等待或判断后 当前线程获取到锁 标记加锁
		isUpdate = update;
		if (!update) {
			synchronized (threadLock) {
				threadLock.notifyAll();
			}
			threadLock = null;
		}
	}

	/**
	 * 查询数据
	 * 
	 * @return List<? extends Model>(不返回null)
	 */
	@Deprecated
	static public <M extends Model<M>> List<M> select(Class<M> clazz, String sql, Object... param) {
		return findAll(clazz, sql, param);
	}

	/**
	 * 查询数据
	 * 
	 * @return List<? extends Model>(不返回null)
	 */
	static public <M extends Model<?>> List<M> findAll(Class<M> clazz, String sql, Object... param) {
		List<M> models = new ArrayList<M>();
		for (Record record : Dao.select(sql, param)) {
			try {
				M model = clazz.newInstance();
				model.set(record);
				models.add(model);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return models;
	}

	static public <M extends Model<M>> M find(Class<M> clazz, String sql, Object... param) {
		List<M> ms = findAll(clazz, sql, param);
		if (ms.isEmpty()) {
			return null;
		}
		return ms.get(0);
	}

	static public <M extends Model<M>> Pagination<M> paginat(int count, int page, Class<M> clazz, String sql,
			Object[] param) {
		List<M> models = findAll(clazz, sql, param);
		Pagination<M> pagination = new Pagination<M>(count, page, models);
		return pagination;
	}

	private int updateNum = 0;

	public int update(boolean delay) throws SQLException {
		if (delay) {
			updateNum++;
			// 延时更新 
			App.getApp().registerFinishRequest(() -> {
				synchronized (Model.this) {
					updateNum--;
					if (updateNum == 0) {
						// 多个更新一次性进行
						try {
							_update(true);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			});
			return 1;
		}
		return update();
	}

	public int update() throws SQLException {
		return _update(false);
	}

	private int _update(boolean delay) throws SQLException {
		Table table = Table.getTable(tableName);
		List<String> pk = table.getPk();
		if (data.isEmpty() || pk.isEmpty()) {
			return 0;
		}
		int n = data.size();
		int m = pk.size();
		Object[] param = new Object[n + m];
		StringBuilder column = new StringBuilder();
		StringBuilder sql = new StringBuilder("update " + tableName + " set ");
		int i = 0;
		for (Entry<String, Object> entry : data.entrySet()) {
			String k = entry.getKey();
			if (!table.containsColumn(k)) {
				continue;
			}
			Object v = entry.getValue();
			column.append(k + "=?,");
			param[i] = v;
			i++;
		}
		column.setCharAt(column.length() - 1, ' ');
		StringBuilder where = new StringBuilder();
		for (String p : pk) {
			param[i] = get(p);
			if (param[i] == null) {
				return 0;
			}
			where.append(p + "=? and ");
			i++;
		}
		sql.append(column + "where " + where.substring(0, where.length() - 5));
		origin.putAll(data);
		data.clear();
		return Dao.update(delay, sql.toString(), param);
	}

	public boolean save() {
		return save(false);
	}

	public boolean save(boolean delay) {
		if (origin.isEmpty()) {
			return false;
		}
		int m = origin.size();
		StringBuilder sql = new StringBuilder("insert into " + tableName + "(");
		StringBuilder values = new StringBuilder(" values(");
		Object[] param = new Object[m];
		int i = 0;
		Table table = Table.getTable(tableName);
		for (Entry<String, Object> entry : origin.entrySet()) {
			String k = entry.getKey();
			if (!table.containsColumn(k)) {
				return false;
			}
			Object v = entry.getValue();
			sql.append(k);
			values.append('?');
			if (i < m - 1) {
				sql.append(',');
				values.append(',');
			}
			param[i] = v;
			i++;
		}
		values.append(')');
		sql.append(')').append(values);
		try {
			Dao.insert(delay, sql.toString(), param);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean delete() {
		return delete(false);
	}

	public boolean delete(boolean delay) {
		List<String> pk = Table.getTable(tableName).getPk();
		if (pk.isEmpty()) {
			return false;
		}
		StringBuilder sql = new StringBuilder("DELETE FROM " + tableName + " WHERE ");
		int m = pk.size();
		Object[] param = new Object[m];
		int i = 0;
		for (String string : pk) {
			Object v = get(string);
			if (v == null) {
				return false;
			}
			param[i] = v;
			sql.append(string + "=? AND ");
			i++;
		}
		try {
			Dao.delete(delay, sql.substring(0, sql.length() - 5), param);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 实例化对象时运行的方法 主要用于继承修改一些Model的属性初始值
	 */
	protected void init() {
	}

	/**
	 * 获取当前表名
	 * 
	 * @return 表名
	 */
	public String tableName() {
		return tableName;
	}

	public String json() {
		String string = JSON.stringify(this);
		return string;
	}

	/**
	 * 设置model属性.
	 * 
	 * @param attr  the attribute name of the model
	 * @param value the value of the attribute
	 * @return this model
	 */
	protected M set(String attr, Object value) {
		if (value == null) {
			if (isUpdate) {
				data.remove(attr);
			} else {
				origin.remove(attr);
			}
		} else {
			if (isUpdate) {
				data.put(attr, value);
			} else {
				origin.put(attr, value);
			}
		}
		return (M) this;
	}

	public M set(Map<String, Object> data) {
		origin = data;
		return (M) this;
	}

	/**
	 * 用于更新model
	 * 
	 * @param attr  the attribute name of the model
	 * @param value the value of the attribute
	 * @return this model
	 */
	@Deprecated
	public M put(String attr, Object value) {
		data.put(attr, value);
		return (M) this;
	}

	protected Map<String, Object> gets() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.putAll(origin);
		map.putAll(data);
		return map;
	}

	/**
	 * Get attribute of any mysql type
	 */
	protected <T> T get(String attr) {
		Object object = data.get(attr);
		return (T) (object == null ? origin.get(attr) : object);
	}

	/**
	 * Get attribute of any mysql type. Returns defaultValue if null.
	 */
	protected <T> T get(String attr, Object defaultValue) {
		Object result = get(attr);
		return (T) (result != null ? result : defaultValue);
	}

	/**
	 * Get attribute of mysql type: varchar, char, enum, set, text, tinytext,
	 * mediumtext, longtext
	 */
	protected String getStr(String attr) {
		// return (String)get(attr);
		Object s = get(attr);
		return s != null ? s.toString() : null;
	}

	/**
	 * Get attribute of mysql type: int, integer, tinyint(n) n > 1, smallint,
	 * mediumint
	 */
	protected Integer getInt(String attr) {
		Number n = (Number) get(attr);
		return n != null ? n.intValue() : null;
	}

	/**
	 * Get attribute of mysql type: bigint, unsign int
	 */
	protected Long getLong(String attr) {
		Number n = (Number) get(attr);
		return n != null ? n.longValue() : null;
	}

	/**
	 * Get attribute of mysql type: unsigned bigint
	 */
	protected java.math.BigInteger getBigInteger(String attr) {
		return (java.math.BigInteger) get(attr);
	}

	/**
	 * Get attribute of mysql type: date, year
	 */
	protected java.util.Date getDate(String attr) {
		return (java.util.Date) get(attr);
	}

	/**
	 * Get attribute of mysql type: time
	 */
	protected java.sql.Time getTime(String attr) {
		return (java.sql.Time) get(attr);
	}

	/**
	 * Get attribute of mysql type: timestamp, datetime
	 */
	protected java.sql.Timestamp getTimestamp(String attr) {
		return (java.sql.Timestamp) get(attr);
	}

	/**
	 * Get attribute of mysql type: real, double
	 */
	protected Double getDouble(String attr) {
		Number n = (Number) get(attr);
		return n != null ? n.doubleValue() : null;
	}

	/**
	 * Get attribute of mysql type: float
	 */
	protected Float getFloat(String attr) {
		Number n = (Number) get(attr);
		return n != null ? n.floatValue() : null;
	}

	protected Short getShort(String attr) {
		Number n = (Number) get(attr);
		return n != null ? n.shortValue() : null;
	}

	protected Byte getByte(String attr) {
		Number n = (Number) get(attr);
		return n != null ? n.byteValue() : null;
	}

	/**
	 * Get attribute of mysql type: bit, tinyint(1)
	 */
	protected Boolean getBoolean(String attr) {
		return (Boolean) get(attr);
	}

	/**
	 * Get attribute of mysql type: decimal, numeric
	 */
	protected java.math.BigDecimal getBigDecimal(String attr) {
		return (java.math.BigDecimal) get(attr);
	}

	/**
	 * Get attribute of mysql type: binary, varbinary, tinyblob, blob, mediumblob,
	 * longblob
	 */
	protected byte[] getBytes(String attr) {
		return (byte[]) get(attr);
	}

	/**
	 * Get attribute of any type that extends from Number
	 */
	protected Number getNumber(String attr) {
		return (Number) get(attr);
	}
}
