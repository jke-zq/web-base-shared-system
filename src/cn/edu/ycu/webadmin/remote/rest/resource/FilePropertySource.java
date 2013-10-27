package cn.edu.ycu.webadmin.remote.rest.resource;

import java.util.ArrayList;
import java.util.List;

import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import cn.edu.ycu.webadmin.remote.rest.service.FileInfoService;
import cn.edu.ycu.webadmin.remote.rest.utils.StrUtils;

public class FilePropertySource extends ServerResource {

	@Autowired
	    private FileInfoService  fileServ;
	@Post
	public Representation post(Representation entity){
		Representation rep = null;
		Form form = new Form(getRequest().getEntityAsText());
		String[] params = form.getValues("fileIds").split(",");
		String userName = form.getFirstValue("username");
		System.out.println("userName:" + userName);
		if(StrUtils.isBlank(userName)){
			return rep;
		}
		List<Integer> fileIds = new ArrayList<Integer>(); 
		for(String param:params){
			fileIds.add(Integer.parseInt(param));
		}
		if("admin".equals(userName)){
			fileServ.deleteIds(fileIds.toArray(new Integer[0]));
		}else{
			fileServ.setShared(fileIds.toArray(new Integer[0]));
		}
		String url = "/uploadfilelist.html?username="
				+ userName;
		System.out.println(new Reference(getRequest().getHostRef(), url).toString());
		redirectPermanent(new Reference(getRequest().getHostRef(), url));
		return rep;
	}
}
