package com.polycom.edge.webadmin.remote.rest;

import java.util.List;

public class PageView<T> {
	private List<T> records;
	private PageIndex pageindex;
	private long totalpage = 1;
	private int maxresult = 12;
	private int currentpage = 1;
	private long totalrecord;
	private int pagecode = 10;
	
	private String SEP = "&";
	public int getFirstResult() {
		return (this.currentpage-1)*this.maxresult;
	}
	public int getPagecode() {
		return pagecode;
	}

	public void setPagecode(int pagecode) {
		this.pagecode = pagecode;
	}

	public PageView(int maxresult, int currentpage) {
		if(maxresult > 0){
			this.maxresult = maxresult;
		}
		this.currentpage = currentpage;
	}
	
	public void setQueryResult(QueryResult<T> qr){
		setTotalrecord(qr.getTotalrecord());
		setRecords(qr.getResultlist());
	}
	
	public long getTotalrecord() {
		return totalrecord;
	}
	public void setTotalrecord(long totalrecord) {
		this.totalrecord = totalrecord;
		setTotalpage(this.totalrecord%this.maxresult==0? this.totalrecord/this.maxresult : this.totalrecord/this.maxresult+1);
	}
	public List<T> getRecords() {
		return records;
	}
	public void setRecords(List<T> records) {
		this.records = records;
	}
	public PageIndex getPageindex() {
		return pageindex;
	}
	public long getTotalpage() {
		return totalpage;
	}
	public void setTotalpage(long totalpage) {
		this.totalpage = totalpage;
		this.pageindex = PageIndex.getPageIndex(pagecode, currentpage, totalpage);
	}
	public int getMaxresult() {
		return maxresult;
	}
	public int getCurrentpage() {
		return currentpage;
	}
	
	public String getNumList(final String sep){
		if(null == sep || "".equals(sep)){
			SEP = sep;
		}
		return this.currentpage + SEP + this.totalrecord + SEP + this.maxresult + SEP + this.totalpage + SEP + this.pageindex.getStartindex() + SEP + this.pageindex.getEndindex(); 
	}
	
}
