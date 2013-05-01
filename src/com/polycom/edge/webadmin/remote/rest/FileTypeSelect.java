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
		System.out.println("entreing into the FileTypeSelect.get method");
		int parentId = 0;
		try {
			parentId = Integer.parseInt((String) getRequest().getAttributes().get("parentId"));
		} catch (NumberFormatException e) {
	       return RepResult.respResult(this, Status.CLIENT_ERROR_BAD_REQUEST, "the parentId cant be null, please check the url", e);
	        
		}
		String jpql = " o.parentType.id = ?1 ";
		Object[] params = new Object[0];
		StringBuffer sb = new StringBuffer(" ");
		if(parentId < 0){
			parentId=0;
		}
		params = new Object[]{parentId};
		FileType childType = FileTypeServ.find(parentId);
		FileType parentType = childType.getParentType();
		sb.insert(0, "&gt;&gt;<a href='/rest/filetypesele/" + childType.getId()+ "'>" + childType.getName() + "</a>");
		while(parentType != null){
			childType = parentType;
			sb.insert(0, "&gt;&gt;<a href='/rest/filetypesele/" + childType.getId()+ "'>" + childType.getName() + "</a>");
			parentType = parentType.getParentType();
		}
		System.out.println(sb.toString());
		sb.append(SEP_EXTER);
		
//		<tr>
//	    <td >
//		<a href="<html:rewrite action="/control/product/manage"/>?method=selectUI&typeid=${type.typeid}"><b>${type.name}</b></a></c:if>
//		<INPUT TYPE="radio" NAME="type" onclick="getDicName('${type.typeid}','${type.name}')">${type.name}</c:if>
//		</td>
//</tr>u
        sb.append("<table width=\"100%\" border=\"0\"><tr>");
		int column = 0;
		QueryResult<FileType> fileTypes = FileTypeServ.getScrollData(-1, -1, jpql, params);
		for(FileType fileType:fileTypes.getResultlist()){
			if(column % 5 ==0 && column > 0){
				sb.append("</tr><tr>");
			}
			if(fileType.getChildType().size()>0){
				sb.append("<td ><a href='/rest/filetypesele/" + fileType.getId()+ "'><b>" + fileType.getName() + "</b></a></td>");
			}else{
				sb.append("<INPUT TYPE=\"radio\" NAME=\"type\" onclick=\"getDicName('"+fileType.getId()+"','"+fileType.getName()+"')\">" + fileType.getName() + "</td>");
			}
			column++;
		}
		sb.append("</tr></table>");
		System.out.println(sb.toString());
		return RepResult.respResult(this, Status.SUCCESS_OK, sb.toString(), null);
		
	}
}
