package cn.edu.ycu.webadmin.remote.rest.resource;

import java.io.File;

import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;


import cn.edu.ycu.webadmin.remote.rest.bean.FileInfo;
import cn.edu.ycu.webadmin.remote.rest.service.FileInfoService;
import cn.edu.ycu.webadmin.remote.rest.service.FileTypeService;
import cn.edu.ycu.webadmin.remote.rest.service.UploadThreadPoolService;
import cn.edu.ycu.webadmin.remote.rest.service.UserInfoService;
import cn.edu.ycu.webadmin.remote.rest.utils.RepResult;
import cn.edu.ycu.webadmin.remote.rest.utils.StrUtils;

public class DownloadFileSource extends ServerResource {

	@Autowired
	private FileInfoService fileService;
	@Autowired
	private UserInfoService userService;
	@Autowired
	private FileTypeService typeService;
	@Autowired
	private UploadThreadPoolService uploadThrePool;
	
	private String uploadPath = "/home/cheetah/Projects/AppStore/uploadFiles";

	@Get()
	public Representation get() {
		String fileId = (String)getRequest().getAttributes().get("fileid");
		FileInfo file = null;
		if(StrUtils.isNotBlank(fileId)){
			int id = Integer.parseInt(fileId);
			file = fileService.find(id);
			System.out.println(uploadPath + File.separator + file.getFilePath());
			System.out.println("exitst:" + new File(uploadPath + File.separator + file.getFilePath()).exists());
			if(new File(uploadPath + File.separator + file.getFilePath()).exists()){
			Representation rep = new FileRepresentation(uploadPath + File.separator + file.getFilePath(),MediaType.ALL);
			Disposition attachment = rep.getDisposition();
			attachment.setType(attachment.TYPE_ATTACHMENT);
			attachment.setFilename(file.getName()); 
			return rep;
			}
		}
		
		System.out.println(uploadPath + File.separator + file.getFilePath());
		return RepResult.respResult(this, Status.CLIENT_ERROR_BAD_REQUEST, "The file you visit is gone!", null);
	}

	@Post()
	public Representation post(Representation entity) {
		return null;
	}
		
}
