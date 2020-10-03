package cn.tqhweb.jweb.db;

import java.util.ArrayList;
import java.util.List;

import cn.tqhweb.jweb.Model;

public class Pagination<M extends Model<M>> {
	
	public static final int pageSize = 24;
	public final long count;
	public final long page;
	public final List<M> models;

	public Pagination(long count, long page, List<M> models) {
		this.count = count;
		this.page = page;
		this.models = models;
	}

	// 生成空页
	public static <M extends Model<M>> Pagination<M> createEmpty() {
		return new Pagination<M>(0, 1, new ArrayList<M>());
	}
}
