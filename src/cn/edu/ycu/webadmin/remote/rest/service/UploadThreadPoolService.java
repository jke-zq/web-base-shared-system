package cn.edu.ycu.webadmin.remote.rest.service;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;

public interface UploadThreadPoolService extends ServletContextAware{

	public Boolean copyAndDel(String fileTempName, Boolean isSaved);
	
	public ServletContext getServCtx();

}