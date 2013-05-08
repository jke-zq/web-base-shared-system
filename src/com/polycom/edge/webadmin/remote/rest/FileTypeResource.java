package com.polycom.edge.webadmin.remote.rest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.polycom.edge.webadmin.remote.rest.utils.RepResult;
import com.polycom.edge.webadmin.remote.rest.utils.StrUtils;

public class FileTypeResource extends ServerResource {

	private final String SEP_INTER = "#";
	private final String SEP_EXTER = "*";

	@Autowired
	private FileTypeService fileTypeServ;
	private volatile Reference reference = new Reference(
			"http://localhost:8080");

	@Get
	public Representation get() {
		System.out.println("entering into FileTypeResource.get method");
		System.out.println("url:" + getRequest().getHostRef());
		System.out.println("method:" + getRequest().getMethod());
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
				params.toArray(), new LinkedHashMap<String, String>() {
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
		strValu.deleteCharAt(strValu.length() - 1);
		System.out.println("the result is:" + strValu.toString());
		return new StringRepresentation(strValu.toString(),
				MediaType.TEXT_PLAIN);
	}

	@Post
	// url:"/filetype/parent/{parentId}"
	public Representation post(Representation entity) {
		System.out.println("entering into FileTypeResource.post method");
		System.out.println("url:" + getRequest().getHostRef());
		Representation rep = null;
		int parentId = 0;
		String[] argums = null;
		try {
			System.out.println(getRequest().getEntityAsText());
			argums = getRequest().getEntityAsText().split("&");
			if (StrUtils
					.areNotBlank(new CharSequence[] { argums[0], argums[1] })) {
				parentId = Integer.parseInt(argums[0]);
				if (parentId < 0) {
					parentId = 0;
				}
			}
		} catch (NumberFormatException e) {
			rep = RepResult.respResult(this, Status.CLIENT_ERROR_BAD_REQUEST,
					"The address is invalid!", null);
		}
		FileType fileType = new FileType(argums[1]);
		fileType.setTypeDesc(argums[2]);
		fileType.setParentType(new FileType(parentId));
		fileTypeServ.save(fileType);
		rep = RepResult.respResult(this, Status.SUCCESS_OK, "Successfully!",
				null);
		String url = "/filetypelist.html";
		System.out.println(url);
		redirectSeeOther(new Reference(reference, url));
		return rep;
	}
	
}
