package cn.edu.ycu.webadmin.remote.rest.resource;

import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import cn.edu.ycu.webadmin.remote.rest.bean.UserInfo;
import cn.edu.ycu.webadmin.remote.rest.service.UserInfoService;
import cn.edu.ycu.webadmin.remote.rest.utils.RepResult;

public class UserInfoResource extends ServerResource {
	@Autowired
	UserInfoService userServ;
	private volatile Reference reference = new Reference(
			"http://localhost:8080");

	// window.location.href="/rest/userzone/"+XMLHttp.responseText.LTrim()

	@Post
	public Representation post(Representation entity) throws ResourceException {
		System.out.println("entering into UserInfoResource.post");
		Representation rep = null;
		String name;
		String passwd;
		String method;
		try {
			Form form = new Form(getRequest().getEntityAsText());
			method = form.getFirstValue("method");
			name = form.getFirstValue("userName");
			passwd = form.getFirstValue("password");
			if (name == null || "".equals(name) || passwd == null
					|| "".equals(passwd)) {
				throw new NullPointerException();
			}
		} catch (Exception e) {
			return RepResult.respResult(this, Status.CLIENT_ERROR_BAD_REQUEST,
					e.getMessage(), e);
		}
		UserInfo userInfo = new UserInfo(name, passwd);
		System.out.println(getRequest().getResourceRef().toString());
		if (method != null && method.contains("login")) {
			if (!userServ.checkPasswd(userInfo)) {
				rep = RepResult.respResult(this,
						Status.CLIENT_ERROR_BAD_REQUEST,
						"userName or password is invalid!", null);
			}
			System.out.println("redirecting:" + name.trim());
			String url = "/rest/zone/" + name.trim();
			System.out.println(url);
//			redirectSeeOther(new Reference(reference, url));
			redirectPermanent(new Reference(reference, url));
		} else {
			if (!userServ.checkUser(userInfo.getName())) {
				userServ.save(userInfo);
				String url = "/rest/zone/" + name.trim();
				redirectSeeOther(new Reference(reference, url));
				// //getResponse().setAttributes(new
				// HashMap<String,Object>(){{put("loginId",userInfo.getName());}});
				// redirectPermanent(new Reference(reference,"/404.html"));//add
				// some parameter such as name to get method
			} else {
				System.out.println("enter into else-case!");
				rep = RepResult.respResult(this, Status.SUCCESS_CREATED,
						"this is name has been used!", null);
			}
		}

		return rep;
	}

	@Get
	public Representation get() throws ResourceException {
		System.out.println("entering into the get method...");
		System.out.println(getRequest().getAttributes().get("loginId"));
		return null;
	}

}
