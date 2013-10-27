package cn.edu.ycu.webadmin.remote.rest.resource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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

import cn.edu.ycu.webadmin.remote.rest.bean.UserInfo;
import cn.edu.ycu.webadmin.remote.rest.pageview.PageView;
import cn.edu.ycu.webadmin.remote.rest.service.UserInfoService;
import cn.edu.ycu.webadmin.remote.rest.utils.StrUtils;

public class UserPropertySource extends ServerResource {

	@Autowired
	private UserInfoService userService;

	@RequestMapping(value = "/user/property/{pageNum}",
            method = { RequestMethod.GET})
	public Representation get() {
		System.out.println("entering into the get method...");
		Representation rep = null;
		StringBuffer bf = new StringBuffer();
		int pageNum = 1;
		String pageNumstr = (String) getRequest().getAttributes()
				.get("pageNum");
		if (StrUtils.isNotBlank(pageNumstr)) {
			pageNum = Integer.parseInt(pageNumstr);
		}
		// set maxresult =-1 to instate:using 12 default value
		PageView<UserInfo> pageView = new PageView<UserInfo>(-1, pageNum);
		LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();
		orderby.put("name", "desc");
		pageView.setQueryResult(userService.getScrollData(
				pageView.getFirstResult(), pageView.getMaxresult(), orderby));
		bf.append(pageView.getNumList("&") + ";");
		bf.append("<table width='98%' align = 'center'>");
		for (UserInfo user : pageView.getRecords()) {
			bf.append("<tr>");
			bf.append("<td bgcolor='f5f5f5' width='8%'> <div align='center'><INPUT TYPE='checkbox' NAME='userIds' value='"
					+ user.getUserId() + "'></div></td>");
			bf.append("<td bgcolor='f5f5f5' width='5%'> <div align='center'>"
					+ user.getUserId() + "</div></td>");
			bf.append("<td bgcolor='f5f5f5' width='10%'> <div align='center'>"
					+ user.getName() + "</div></td>");
			bf.append("<td bgcolor='f5f5f5' width='40%'> <div align='center'>"
					+ "userDesc" + "</div></td>");
			bf.append("<td bgcolor='f5f5f5' width='5%'><div align='center' ><a href=''><input type='button' value='other'/></a></div></td>");
			bf.append("<td bgcolor='f5f5f5' width='10%'> <div align='center'>"
					+ "major" + "</div></td>");
			bf.append("<td bgcolor='f5f5f5' width='22%'> <div align='center'>"
					+ "registerTime" + "</div></td>");
			bf.append("</tr>");
		}
		bf.append("</tr></table>");
		getResponse().setStatus(Status.SUCCESS_OK);
		rep = new StringRepresentation(bf.toString(), MediaType.TEXT_PLAIN);
		System.out.println(bf.toString());
		return rep;
	}
	
	@Post
	public Representation post(Representation entity){
		Representation rep = null;
		Form form = new Form(getRequest().getEntityAsText());
		String[] params = form.getValues("userIds").split(",");
		
		List<Integer> userIds = new ArrayList<Integer>(); 
		for(String param:params){
			userIds.add(Integer.parseInt(param));
		}
		userService.deleteUsers(userIds.toArray(new Integer[0]));
		String url = "/userlist.html";
		System.out.println(new Reference(getRequest().getHostRef(), url).toString());
		redirectPermanent(new Reference(getRequest().getHostRef(), url));
		return rep;
	}
}
