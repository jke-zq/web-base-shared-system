package com.polycom.edge.webadmin.remote.rest;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;

public interface UploadThreadPoolService extends ServletContextAware{

	public Boolean copyAndDel(String fileTempName, Boolean isSaved);
	
	public ServletContext getServCtx();

}