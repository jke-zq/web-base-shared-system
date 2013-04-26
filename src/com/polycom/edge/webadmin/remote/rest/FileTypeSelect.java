package com.polycom.edge.webadmin.remote.rest;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.polycom.edge.webadmin.remote.rest.utils.RepResult;

public class FileTypeSelect extends ServerResource {

	private final String SEP_INTER = "#";
	private final String SEP_EXTER = "*";
	
	@Autowired
	private FileTypeService FileTypeServ;
	
	@Get
	public Representation get(){
		
		int parentId = 0;
		try {
			parentId = Integer.parseInt((String) getRequest().getAttributes().get("parentId"));
		} catch (NumberFormatException e) {
	       return RepResult.respResult(this, Status.CLIENT_ERROR_BAD_REQUEST, "the parentId cant be null, please check the url", e);
	        
		}
		String jpql = " o.parentId = null ";
		Object[] params = new Object[0];
		StringBuffer sb = new StringBuffer();
		if(parentId > 0){
			jpql = " o.parentId = ?1";
			params = new Object[]{parentId};
			FileType childType = FileTypeServ.find(parentId);
			FileType parentType = childType.getParentType();
			sb.append(childType.getName() + SEP_INTER);
			while(parentType != null){
				childType = parentType;
				sb.append(childType.getName() + SEP_INTER);
				parentType = parentType.getParentType();
			}
		}
		sb.append(SEP_EXTER);
		QueryResult<FileType> fileTypes = FileTypeServ.getScrollData(-1, -1, jpql, params);
		for(FileType fileType:fileTypes.getResultlist()){
			sb.append(fileType.listValue(SEP_INTER));
			sb.append(SEP_EXTER);
		}
		sb.delete(sb.length()-SEP_EXTER.length(),sb.length()-1);
		return RepResult.respResult(this, Status.SUCCESS_OK, sb.toString(), null);
		
	}
}
