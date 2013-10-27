package cn.edu.ycu.webadmin.remote.rest.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.edu.ycu.webadmin.remote.rest.bean.FileInfo;
import cn.edu.ycu.webadmin.remote.rest.pageview.PageView;
import cn.edu.ycu.webadmin.remote.rest.service.FileInfoService;
import cn.edu.ycu.webadmin.remote.rest.utils.StrUtils;
import cn.edu.ycu.webadmin.remote.rest.utils.ExtensionType;

public class SearchResource extends ServerResource {

	@Autowired
	private FileInfoService fileService;

	private volatile Reference baseRef = new Reference("http://localhost:8080");

	public SearchResource() {

	}

	@RequestMapping(value = "/search/{pagenum}/{typeid}", method = { RequestMethod.GET })
	public Representation get() {
		Representation rep = null;
		Map<String, Object> map = getRequest().getAttributes();
		int pageNum = 1;
		int typeid = 0;
		String keyword = null;
		String keyparam = Request.getCurrent().getResourceRef().getQuery()
				.split("&amp;")[0];
		System.out.println("queryString:"
				+ Request.getCurrent().getResourceRef().getQuery());
		System.out.println("keyparam:" + keyparam);
		if (keyparam.lastIndexOf("=") < keyparam.length() - 1) {
			System.out.println("keyword:" + keyparam.split("=")[1]);
			keyword = keyparam.split("=")[1].replaceAll("(%20)+", "$1");
			System.out.println("keyworld:" + keyword);
		}
		String typeStr = (String) map.get("typeid");
		String pageStr = (String) map.get("pagenum");
		if (StrUtils.isNotBlank(typeStr)) {
			typeid = Integer.parseInt(typeStr);
		}
		if (StrUtils.isNotBlank(pageStr)) {
			pageNum = Integer.parseInt(pageStr);
			pageNum = pageNum > 0 ? pageNum : 1;
		}
		StringBuffer jpql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		if (StrUtils.isNotBlank(keyword)) {
			for (String s : keyword.trim().split("%20")) {// which separator
				jpql.append("o.fileDescription like ");
				jpql.append("?" + (params.size() + 1) + " and ");
				params.add("%" + s.toLowerCase() + "%");
			}
			// jpql.deleteCharAt(jpql.length()-4);
		}
		jpql.append(" o.visible = ?" + (params.size() + 1) + " ");
		params.add(true);
		jpql.append(" and o.shared = ?" + (params.size() + 1) + " ");
		params.add(true);
		jpql.append(" and o.fileType.id = ?" + (params.size() + 1) + " ");
		params.add(typeid);
		String pageNumstr = (String) getRequest().getAttributes()
				.get("pageNum");
		if (StrUtils.isNotBlank(pageNumstr)) {
			pageNum = Integer.parseInt(pageNumstr);
		}
		System.out.println("jpql:" + jpql.toString());
		System.out.println("params:" + params.toString());
		// set maxresult =-1 to instate:using 12 default value
		PageView<FileInfo> pageView = new PageView<FileInfo>(-1, pageNum);
		LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();
		orderby.put("id", "desc");
		pageView.setQueryResult(fileService.getScrollData(
				pageView.getFirstResult(), pageView.getMaxresult(),
				jpql.toString(), params.toArray(), orderby));
		StringBuffer bf = new StringBuffer();
		bf.append(pageView.getNumList("&") + ";;");
		if (pageView.getRecords().size() <= 0) {
			bf.append("there is no result to show...");
		} else {

			for (FileInfo file : pageView.getRecords()) {
				bf.append("<div style='float:left;'><table border='0' cellpadding='0' cellspacing='0'><tbody><tr><td class='r1'><div class='cb'><img src='/pics/");
				bf.append(file.getExt().toLowerCase());
				bf.append(".gif' alt='");
				bf.append(ExtensionType.getAlert(file.getExt()));
				bf.append("' width='16' height='14' align='absmiddle'> <a href='/rest/download/"
						+ file.getId().toString().trim());
				bf.append("");
				bf.append("' target='_blank' class='f16'><span style='color:#C03'>"
						+ file.getName() + "</span></a> </div>");
				if (StrUtils.isNotBlank(keyword)) {
					String sb = new String();
					sb = file.getFileDescription();
					for (String s : keyword.trim().split("%20")) {// which
																	// separator
						sb = sb.replace(s.toLowerCase(),
								"<B>" + s.toLowerCase() + "</B>");
					}
					bf.append(sb);

				} else {
					bf.append(file.getFileDescription());
				}
				bf.append("<div class='c9'> 上传时间:" + file.getUploadtime()
						+ "</div>");
				bf.append("<span style='width:50em;'><span class='gray'> 下载:0 次 | 大小:51KB | 共享者："
						+ file.getUser().getName()
						+ "</a></span> <span class='pl10'></span></span><div style='display:none' id='f20413063'></div></td></tr></tbody></table></div>");
			}

		}
		getResponse().setStatus(Status.SUCCESS_OK);
		rep = new StringRepresentation(bf.toString(), MediaType.TEXT_PLAIN);
		System.out.println(bf.toString());
		return rep;
	}

}
