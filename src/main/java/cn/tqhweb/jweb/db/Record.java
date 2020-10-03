package cn.tqhweb.jweb.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.tqhweb.jweb.Model;

@SuppressWarnings("serial")
public class Record extends HashMap<String, Object> {

	/**
	 * Record转<? extends Model>
	 * 
	 * @return 不为null
	 */
	public <M extends Model<M>> M toModel(Class<M> clazz) {
		try {
			return (M) clazz.newInstance().set(this);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * List<Record>转List<? extends Model>
	 * 
	 * @return List 不为null
	 */
	static public <M extends Model<M>> List<M> toModel(Class<M> clazz, List<Record> list) {
		List<M> models = new ArrayList<M>();
		if (list == null) {
			return models;
		}
		for (Record record : list) {
			try {
				models.add(((M) clazz.newInstance()).set(record));
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return models;
	}
}
