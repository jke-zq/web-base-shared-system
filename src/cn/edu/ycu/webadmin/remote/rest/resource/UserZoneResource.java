package cn.edu.ycu.webadmin.remote.rest.resource;


import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import cn.edu.ycu.webadmin.remote.rest.bean.UserInfo;
import cn.edu.ycu.webadmin.remote.rest.service.UserInfoService;
import cn.edu.ycu.webadmin.remote.rest.utils.RepResult;

public class UserZoneResource extends ServerResource {
	private volatile Reference reference = new Reference("http://localhost:8080");
	@Autowired
	private UserInfoService userServ;
	private String name;
	private String passwd;
	@Post
	public Representation post(Representation entity) {
		System.out.println("entering into UserZoneResource.post");
		Representation rep = null;
		try {
			// Form form = getRequest().getResourceRef().getQueryAsForm();
			// name = (String)getRequest().getAttributes().get("userName");
			System.out.println(getRequest().getEntityAsText());
			name = getRequest().getEntityAsText().split("&")[0];
			passwd = getRequest().getEntityAsText().split("&")[1];
			if (name == null || "".equals(name) || passwd == null
					|| "".equals(passwd)) {
				throw new NullPointerException();
			}
		} catch (Exception e) {
			return RepResult.respResult(this, Status.CLIENT_ERROR_BAD_REQUEST,
					e.getMessage(), e);
		}

		UserInfo user = new UserInfo(name, passwd);
		if (userServ.checkPasswd(user)) {
        	String url = "/rest/zone/" + user.getName().trim();
        	System.out.println("url:" + url);
        	redirectSeeOther(new Reference(reference, url));
        	System.out.println("redirectSeeOther");
        	redirectPermanent(new Reference(reference, url));
        	System.out.println("redirectPermanent");
        	redirectTemporary(new Reference(reference, url));
        	System.out.println("redirectTemporary");
			return null;
		} else {
			rep = RepResult.respResult(this, Status.CLIENT_ERROR_BAD_REQUEST,
					"userName or password is invalid!", null);
		}
		return rep;

	}

	@Get
	public Representation get() {
		Representation rep = null;
		System.out.println("entering into UserZoneResource.get");
		System.out.println((String) getRequest().getAttributes().get("loginId"));
		try {
			String userName = (String) getRequest().getAttributes().get("loginId");
			if (userName == null || "".equals(userName)) {
				throw new Exception(
						"loginId can not be null! There is something wrong in the server.");
			}
			String url = "/default.html?username=" + userName.trim();
			if("admin".equals(userName)){
				url = "/management.html?username=" + userName.trim();
			}
			
//			redirectSeeOther(new Reference(reference, url));
			System.out.println(new Reference(reference, url).toUri().toString());
//			redirectPermanent(new Reference(reference, url).toUri().toString());
			rep = RepResult.respResult(this, Status.SUCCESS_OK, new Reference(reference, url).toUri().toString(), null);
			redirectPermanent(new Reference(reference, url));
		} catch (Exception e) {
			this.setStatus(Status.SERVER_ERROR_INTERNAL);
		}
		return rep;

	}

}
