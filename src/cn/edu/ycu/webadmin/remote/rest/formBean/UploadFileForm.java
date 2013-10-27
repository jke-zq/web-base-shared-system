package cn.edu.ycu.webadmin.remote.rest.formBean;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.EmbeddedId;

import org.restlet.data.Parameter;
import org.restlet.util.Series;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;

import cn.edu.ycu.webadmin.remote.rest.bean.UserInfo;
import cn.edu.ycu.webadmin.remote.rest.service.UserInfoService;
import cn.edu.ycu.webadmin.remote.rest.utils.StrUtils;


public class UploadFileForm extends BaseForm {
	
	@Autowired
	private UserInfoService userService;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4278006130778294905L;
	
	private String fileName;
	private String fileSize;
	private String fileType;
	private String fileDesp;
	private int slicedId = -2;// to show that cant find value from the url
	private UserInfo user;
	

	//constructor
	public UploadFileForm(ConcurrentMap<String, Object> map){
		try{
			Series<Parameter> headers = (Series<Parameter>) map.get("org.restlet.http.headers");
//			Set<String> headerNames = headers.getNames();
//			for(String headerName:headerNames)
//			{
//			    System.out.println( "name==>"+headerName+" value==>"+ headers.getFirstValue(headerName));
//			}
			String username = headers.getFirstValue("Authorization");
			//authrizate the userName
			if(StrUtils.isNotBlank(username)){
				this.user = userService.getUserByName(username);
			}
			
			DirectFieldAccessor myObjectAccessor = new DirectFieldAccessor(this);
			PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(this.getClass()).getPropertyDescriptors();
			for(PropertyDescriptor propertydesc : propertyDescriptors){
//				Method method = propertydesc.getReadMethod();
				Object o = map.get(propertydesc.getName());
				if(o != null){
					// how to match the type ??
					//?? how to deal with the case: o is null;
					myObjectAccessor.setPropertyValue(propertydesc.getName(),o);
				}
			}
			
		}catch(Exception e){
			System.out.println("Exception:" + e.getStackTrace());
		}
		
	}


	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public String getFileSize() {
		return fileSize;
	}


	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}


	public String getFileType() {
		return fileType;
	}


	public void setFileType(String fileType) {
		this.fileType = fileType;
	}


	public String getFileDesp() {
		return fileDesp;
	}


	public void setFileDesp(String fileDesp) {
		this.fileDesp = fileDesp;
	}


	public int getSlicedId() {
		return slicedId;
	}


	public void setSlicedId(int slicedId) {
		this.slicedId = slicedId;
	}


	public UserInfo getUser() {
		return user;
	}


	public void setUser(UserInfo user) {
		this.user = user;
	}
		

	
	


}
