package cn.edu.ycu.webadmin.remote.rest;

import java.io.Serializable;
import java.util.LinkedHashMap;




public interface DTO<T> {
	
	public long getCount();
	
	public void save(T entity);

	public void delete(Serializable... entityId);

	public T find(Serializable entityId);

	public void update(T entity);

	public QueryResult<T> getScrollData(int firstindex, int maxresult,
			String wherejpql, Object[] queryParams,
			LinkedHashMap<String, String> orderby);

	public QueryResult<T> getScrollData(int firstindex, int maxresult,
			String wherejpql, Object[] queryParams);

	public QueryResult<T> getScrollData(int firstindex, int maxresult,
			LinkedHashMap<String, String> orderby);

	public QueryResult<T> getScrollData(int firstindex, int maxresult);

	public QueryResult<T> getScrollData();

}
