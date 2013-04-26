package com.polycom.edge.webadmin.remote.rest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.polycom.edge.webadmin.remote.rest.utils.RepResult;

public class FileTypeResource extends ServerResource {

	private final String SEP_INTER = "#";
	private final String SEP_EXTER = "*";

	@Autowired
	private FileTypeService fileTypeServ;

	@Get
	public Representation get() {

		int parentId = 0;
		int pageNum = 0;
		try {
			ConcurrentMap<String, Object> params = getRequest().getAttributes();
			parentId = Integer.parseInt((String) params.get("parentid"));
			pageNum = Integer.parseInt((String) params.get("pagenum"));
		} catch (NumberFormatException e) {
			RepResult.respResult(this, Status.CLIENT_ERROR_BAD_REQUEST,
					"something wrong with the server, please check the url", e);
		}
		if (pageNum < 1) {
			pageNum = 1;
		}
		StringBuffer jpql = new StringBuffer("o.visible = ?1");
		List<Object> params = new ArrayList<Object>();
		params.add(true);
		System.out.println("the parentType is " + parentId);
		
		if (parentId >= 0) {
			jpql.append(" and o.parentType.id = ?2 ");
			params.add(parentId);
		} else {
			jpql.append(" and o.parentType.id  is ?2 ");
			params.add(0);
		}
		PageView<FileType> pv = new PageView<FileType>(12, pageNum);
		@SuppressWarnings("serial")
		QueryResult<FileType> qr = fileTypeServ.getScrollData(
				pv.getFirstResult(), pv.getMaxresult(), jpql.toString(),
				params.toArray(),
				new LinkedHashMap<String, String>() {
					{
						put("id", "desc");
					}
				});
		pv.setQueryResult(qr);
		if (pv.getRecords().size() <= 0) {
			return new StringRepresentation("null,there is not any one!",
					MediaType.TEXT_PLAIN);
		}
		StringBuffer strValu = new StringBuffer();
		strValu.append(pv.getNumList(SEP_INTER) + ";");
		for (FileType o : pv.getRecords()) {
			strValu.append(o.listValue(SEP_INTER) + SEP_EXTER);
		}
		strValu.deleteCharAt(strValu.length()-1);
		System.out.println("the result is:" + strValu.toString());
		return new StringRepresentation(strValu.toString(),
				MediaType.TEXT_PLAIN);
	}

	@Post
	public Representation post(Representation entity) {
		Representation rep = null;
		int parentId = 0;
		try {
			parentId = Integer.parseInt((String) getRequest().getAttributes()
					.get("parentId"));
		} catch (NumberFormatException e) {

		}
		// parentId:==0? >0? or whether exits in the database;
		if (parentId >= 0) {
			Form form = new Form(entity);
			String typeName = null;
			if ((typeName = form.getFirstValue("name")) != null) {
				FileType fileType = new FileType(typeName);
				fileType.setTypeDesc(form.getFirstValue("desc"));
				fileType.setParentType(new FileType(parentId));
				fileTypeServ.save(fileType);
				RepResult.respResult(this, Status.SUCCESS_OK, "Successfully!",
						null);
			} else {
				// number:400
				RepResult.respResult(this, Status.CLIENT_ERROR_BAD_REQUEST,
						"this name of FileType cant be null!", null);
			}
		} else {
			RepResult.respResult(this, Status.CLIENT_ERROR_BAD_REQUEST,
					"the parentId is invalid!", null);

		}

		return rep;
	}
}
