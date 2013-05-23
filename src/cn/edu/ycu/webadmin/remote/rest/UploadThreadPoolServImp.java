package cn.edu.ycu.webadmin.remote.rest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.ServletContext;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;

import cn.edu.ycu.webadmin.remote.rest.utils.runtimes.SystemUtil;


@Service
public class UploadThreadPoolServImp implements UploadThreadPoolService{
	
	private static String  commandPath = "/sh";
	private static ExecutorService exeSer = Executors.newCachedThreadPool();
	private static String cpCmd = null;
	private static String rmCmd = null;
	
	private ServletContext cxt = null;
	
	/* (non-Javadoc)
	 * @see com.polycom.edge.webadmin.remote.rest.UploadThreadPoolService#copyAndDel(java.lang.String, java.lang.Boolean)
	 */
	
	public Boolean copyAndDel(final String fileTempName ,final Boolean isSaved){
		System.out.println("this is running and this is just for check if running!");
		exeSer.execute(new Runnable(){
			public void run(){
				if(isSaved){
					//copy
					//if the dir not exits, pls creates it and cp command.
					SystemUtil.execCmd(new String[]{"/bin/bash", "-c", cpCmd + " " + fileTempName});
				}
				//delete
				SystemUtil.execCmd(new String[]{"/bin/bash", "-c", rmCmd + " " + fileTempName});
			}
		});
		
		return false;
	}

	//how and when to run this method?
	public  void setServletContext(ServletContext arg0) {
		System.out.println("the setServletContext is running now!");
		String temp = null;
		cxt = arg0;
		temp = cxt.getRealPath(commandPath);
		cpCmd= temp + "/cpFile.sh ";
		rmCmd = temp + "/rmFile.sh ";
	}

	public ServletContext getServCtx() {
		return this.cxt;
	}
	



}
