package cn.edu.ycu.webadmin.remote.rest;

import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;


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


	@Get()
	public Representation get() {
		String fileId = (String)getRequest().getAttributes().get("fileid");
		if(StrUtils.isNotBlank(fileId)){
			int id = Integer.parseInt(fileId);
			FileInfo file = fileService.find(id);
			Representation rep = new FileRepresentation(file.getFilePath(),MediaType.ALL);
			Disposition attachment = rep.getDisposition();
			attachment.setType(attachment.TYPE_ATTACHMENT);
			attachment.setFilename(file.getName()); 
			return rep;
		}else{
			return RepResult.respResult(this, Status.CLIENT_ERROR_BAD_REQUEST, "The file you visit is gone!", null);
		}
		
	}

	@Post()
	public Representation post(Representation entity) {
		return null;
	}
		
}
