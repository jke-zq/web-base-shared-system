package cn.edu.ycu.webadmin.remote.rest.resource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.edu.ycu.webadmin.remote.rest.bean.FileType;
import cn.edu.ycu.webadmin.remote.rest.pageview.PageView;
import cn.edu.ycu.webadmin.remote.rest.pageview.QueryResult;
import cn.edu.ycu.webadmin.remote.rest.service.FileTypeService;
import cn.edu.ycu.webadmin.remote.rest.utils.RepResult;
import cn.edu.ycu.webadmin.remote.rest.utils.StrUtils;

public class FileTypeResource extends ServerResource {

	private final String SEP_INTER = "#";
	private final String SEP_EXTER = "*";
	public FileTypeResource(){
		getMetadataService().addExtension("application", MediaType.APPLICATION_ALL);
//		getMetadataService().addExtension("wwwForm", MediaType.APPLICATION_WWW_FORM);
	}
	@Autowired
	private FileTypeService fileTypeServ;
	private volatile Reference reference = new Reference(
			"http://localhost:8080");

	@RequestMapping(value = "/filetype/{parentid}/{pagenum}",
            method = { RequestMethod.GET})
	public Representation get() {
		System.out.println("entering into FileTypeResource.get method");
		System.out.println(getRequest().getEntity().getMediaType().toString());
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
			return new StringRepresentation("Cant get enogh parameters!",MediaType.TEXT_PLAIN);
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
		QueryResult<FileType> qr = fileTypeServ.getScrollData(
				pv.getFirstResult(), pv.getMaxresult(), jpql.toString(),
				params.toArray(), new LinkedHashMap<String, String>() {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

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
	public Representation post(Representation entity){
		System.out.println("entering into FileTypeResource.post method");
		System.out.println(getRequest().getEntity().toString());
		Representation rep = null;
		int parentId = 0;
		Object[] argums = null;
		try {
			Form form = new Form(entity);
			argums = form.getValuesMap().values().toArray();
			if (StrUtils.areNotBlank(new CharSequence[] { (String)argums[0], (String)argums[1] })) {
				parentId = Integer.parseInt((String)argums[0]);
				System.out.println(parentId);
				if (parentId < 0) {
					parentId = 0;
				}
			}
		} catch (NumberFormatException e) {
			rep = RepResult.respResult(this, Status.CLIENT_ERROR_BAD_REQUEST,"The address is invalid!", null);
		}
		FileType fileType = new FileType((String)argums[1]);
		fileType.setTypeDesc((String)argums[2]);
		fileType.setParentType(fileTypeServ.find(parentId));
		fileTypeServ.save(fileType);
		rep = RepResult.respResult(this, Status.SUCCESS_OK, "Successfully!",
				null);
		String url = "/filetypelist.html";
		redirectPermanent(new Reference(reference, url));
		return rep;
	}
	
}
