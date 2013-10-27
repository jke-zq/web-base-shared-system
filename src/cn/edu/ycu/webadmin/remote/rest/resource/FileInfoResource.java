package cn.edu.ycu.webadmin.remote.rest.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.edu.ycu.webadmin.remote.rest.bean.FileInfo;
import cn.edu.ycu.webadmin.remote.rest.bean.FileType;
import cn.edu.ycu.webadmin.remote.rest.bean.UserInfo;
import cn.edu.ycu.webadmin.remote.rest.formBean.UploadFileForm;
import cn.edu.ycu.webadmin.remote.rest.pageview.PageView;
import cn.edu.ycu.webadmin.remote.rest.service.FileInfoService;
import cn.edu.ycu.webadmin.remote.rest.service.FileTypeService;
import cn.edu.ycu.webadmin.remote.rest.service.UploadThreadPoolService;
import cn.edu.ycu.webadmin.remote.rest.service.UserInfoService;
import cn.edu.ycu.webadmin.remote.rest.utils.RepResult;
import cn.edu.ycu.webadmin.remote.rest.utils.StrUtils;

public class FileInfoResource extends ServerResource {

	@Autowired
	private FileInfoService fileService;
	@Autowired
	private UserInfoService userService;
	@Autowired
	private FileTypeService typeService;
	@Autowired
	private UploadThreadPoolService uploadThrePool;
	// private UploadThreadPoolServImp uploadThrePool;

	private String tempPath = "/home/cheetah/Projects/AppStore/tempFiles";
	private volatile Reference baseRef = new Reference("http://localhost:8080");

	public FileInfoResource() {
		getMetadataService().addExtension("multipart",
				MediaType.MULTIPART_FORM_DATA, true);
	}


	@RequestMapping(value = "/fileinfo/{loginId}/{pageNum}",
            method = { RequestMethod.GET})
	public Representation get() {
		Representation rep = null;
		StringBuffer bf = new StringBuffer();
		String loginId = null;
		int pageNum = 1;
		loginId = (String) getRequest().getAttributes().get("loginId");
		System.out.println("FileInfoResource.get(),loginId:" + loginId);
		if (StrUtils.isBlank(loginId)) {
			loginId = "guest";
		}
		String pageNumstr = (String) getRequest().getAttributes()
				.get("pageNum");
		if (StrUtils.isNotBlank(pageNumstr)) {
			pageNum = Integer.parseInt(pageNumstr);
		}
		// set maxresult =-1 to instate:using 12 default value
		PageView<FileInfo> pageView = new PageView<FileInfo>(-1, pageNum);
		LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();
		orderby.put("fileType", "desc");
		StringBuffer jpql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		jpql.append(" o.visible = ?1 ");
		params.add(true);
		// jpql.append(" and o.shared = ?2 ");
		// params.add(true);
		if(!"admin".equals(loginId)){
			System.out.println("the loginer is not the admin.");
			jpql.append(" and o.user.name = ?2 ");
			params.add(loginId);
		}
		System.out.println("loginId:" + loginId);
		pageView.setQueryResult(fileService.getScrollData(
				pageView.getFirstResult(), pageView.getMaxresult(),
				jpql.toString(), params.toArray(), orderby));
		bf.append(pageView.getNumList("&") + ";");
		bf.append("<table width='98%' align = 'center'>");
		for (FileInfo file : pageView.getRecords()) {
			bf.append("<tr>");
			bf.append("<td bgcolor='f5f5f5' width='8%'> <div align='center'><INPUT TYPE='checkbox' NAME='fileIds' value='"
					+ file.getId() + "'></div></td>");
			bf.append("<td bgcolor='f5f5f5' width='5%'> <div align='center'>"
					+ file.getShared() + "</div></td>");
			bf.append("<td bgcolor='f5f5f5' width='10%'> <div align='center'>"
					+ file.getFilePath().substring(
							file.getFilePath().lastIndexOf("/")+1)
					+ "</div></td>");
			bf.append("<td bgcolor='f5f5f5' width='40%'> <div align='center'>"
					+ (StrUtils.isNotBlank(file.getFileDescription()) ? file.getFileDescription().replace("&nbsp;", " ") :file.getFileDescription()) + "</div></td>");
			bf.append("<td bgcolor='f5f5f5' width='5%'><div align='center' ><a href='/rest/download/" + file.getId().toString().trim() + "'><input type='button' value='download'/></a></div></td>");
			bf.append("<td bgcolor='f5f5f5' width='10%'> <div align='center'>"
					+ ("admin".equals(loginId) ? file.getFileType().getName()+"/"+file.getUser().getName():file.getFileType().getName()) + "</div></td>");
			bf.append("<td bgcolor='f5f5f5' width='22%'> <div align='center'>"
					+ file.getUploadtime() + "</div></td>");
			bf.append("</tr>");
		}
		bf.append("</tr></table>");
		getResponse().setStatus(Status.SUCCESS_OK);
		rep = new StringRepresentation(bf.toString(), MediaType.TEXT_PLAIN);
		System.out.println(bf.toString());
		return rep;
	}

	@Post("multipart")
	public Representation post(Representation entity) {
		Representation rep = null;
		System.out.println("entering into the post method");
		FileInfo fileInfo = new FileInfo();
		if (entity != null) {
			if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(),
					true)) {
				DiskFileItemFactory diskFactory = new DiskFileItemFactory();
				diskFactory.setSizeThreshold(1000240);
				RestletFileUpload uploadFile = new RestletFileUpload(
						diskFactory);
				List<FileItem> items;
				try {
					String path = "";
					items = uploadFile.parseRequest(getRequest());
					String tmpFileName = "";
					String userName = "";
					int typeid = 0;
					for (final Iterator<FileItem> it = items.iterator(); it
							.hasNext();) {
						FileItem fileItem = it.next();
						System.out.println("fileItem:" + fileItem.getName());
						System.out.println("fileGetFieldName:" + fileItem.getFieldName());
						if (fileItem.getName() == null) {
							if ("userName".equals(fileItem.getFieldName())) {
								userName = new String(fileItem.get(), "UTF-8");
								if (StrUtils.isBlank(userName)) {
									userName = "guest";
								}
								UserInfo user = userService
										.getUserByName(userName);
								if (user != null) {
									fileInfo.setUser(user);
								} else {
									return RepResult
											.respResult(
													this,
													Status.SERVER_ERROR_NOT_IMPLEMENTED,
													"failed to save the file into Database!",
													null);
								}
							}
							if ("typeid".equals(fileItem.getFieldName())) {
								typeid = Integer.parseInt(new String(fileItem
										.get(), "UTF-8"));
								if (typeid < 0) {
									typeid = 0;
								}
								FileType type = typeService.find(typeid);
								if (type != null) {
									fileInfo.setFileType(type);
								} else {
									return RepResult
											.respResult(
													this,
													Status.SERVER_ERROR_NOT_IMPLEMENTED,
													"failed to save the file into Database!",
													null);
								}
							}
							if ("fileDescription".equals(fileItem
									.getFieldName())) {
								fileInfo.setFileDescription(new String(fileItem
										.get(), "UTF-8"));
								System.out.println("this is the description:" + fileInfo.getFileDescription());
							}
						} else {
							// copy and delete using java to operate linux
							// commands
//							path = uploadThrePool.getServCtx().getRealPath(
//									tempPath);
							path = tempPath;
							// //if the user is not new here, then not need to
							// create the dir;
							// File savePath = new File(path);
							// if(!savePath.exists()) savePath.mkdir();
							tmpFileName = userName + "#*" + fileItem.getName().replace(" ", "");
							fileItem.write(new File(path, tmpFileName));
							System.out.println("path_filename:" + path + "_" + tmpFileName + " exit?:" + new File(path+File.separator+ tmpFileName).exists());
						}
					}
					// to check the file is uploaded finished
					if (StrUtils.isNotBlank(tmpFileName)
							&& new File(path + File.separator + tmpFileName)
									.exists()) {
						// to add the db
						fileInfo.setFilePath(tmpFileName.replace("#*",
								File.separator));
						fileInfo.setFileDescription(tmpFileName.substring(tmpFileName.indexOf("#*")+2) + ":" + fileInfo.getFileDescription());
						System.out.println("fileInfo:" + fileInfo.toString());
						fileService.save(fileInfo);
						// failed to save the fileInfo so rollback
						if (fileService.isSaved(fileInfo.getFilePath())) {
							String url = "/defaultuploadfilelist.html?username="
									+ userName;
							if("admin".equals(userName)){
								url="/uploadfilelist.html?username="
										+ userName;
							}
							System.out.println("userName:" + userName);
							//copy and delete
							uploadThrePool.copyAndDel(tmpFileName, true);
							redirectPermanent(new Reference(baseRef, url));
						} else {
							rep = RepResult.respResult(this,
									Status.SERVER_ERROR_NOT_IMPLEMENTED,
									"failed to save the file into Database!",
									null);
							// delete
							uploadThrePool.copyAndDel(tmpFileName, false);
							return rep;
						}
					}
				} catch (Exception e) {
					rep = RepResult.respResult(this,
							Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage(), e);
				}
			} else {
				rep = RepResult.respResult(this,
						Status.CLIENT_ERROR_BAD_REQUEST,
						"MultiPart/form-data required", null);
			}
		} else {
			rep = RepResult.respResult(this, Status.CLIENT_ERROR_BAD_REQUEST,
					"error,null in the form", null);
		}
		return rep;
	}

	@Put
	public Representation put(Representation entity){
		//get the value from url
		ConcurrentMap<String, Object> mapAttr = getRequest().getAttributes();
		UploadFileForm uploadForm = new UploadFileForm(mapAttr);
		UserInfo user = uploadForm.getUser();
		//authenticate the user
		if(user == null){
			return RepResult.respResult(this, Status.CLIENT_ERROR_UNAUTHORIZED,
					"unauthorized 401", null);
		}
		//get the path of the file and check if the file exist or not or sliced??
		if(StrUtils.isBlank(uploadForm.getFileName())){
			return RepResult.respResult(this, Status.CLIENT_ERROR_BAD_REQUEST,
					"There is something wrong! Cant find the uploading fileName!", null);
		}
		
		FileInfo fileInfo = fileService.findByPath(user.getName() + File.separator + uploadForm.getFileName());
		if(fileInfo == null){
			//the file is a new one to upload
			String slices = addFileDetails(mapAttr, uploadForm, false);
			return RepResult.respResult(this, Status.SUCCESS_OK,
					slices, null);
		}
		if(fileInfo.getFinished()){
			return RepResult.respResult(this, Status.SUCCESS_OK,
					"This file has been uploaded!", null);
		}
		switch (uploadForm.getSlicedId()){
		case -2:
			return RepResult.respResult(this, Status.CLIENT_ERROR_BAD_REQUEST,
					"There is something wrong! Cant find the uploading fileName!", null);
		case -1:
			addFileDetails(mapAttr, uploadForm, true);
			return null;
		default:
			uploadFileSlice(mapAttr, fileInfo);
			return null;
		}
		
	}
	
	private String addFileDetails(ConcurrentMap<String, Object> mapAttr, UploadFileForm uploadForm, Boolean exits){
		try{
			
		}catch(Exception e){
			System.out.println("Exception:" + e.getStackTrace());
		}
		
		return null;
	}
	private String uploadFileSlice(ConcurrentMap<String, Object> mapAttr, FileInfo fileInfo){
		
		
		return null;
	}
	
	
}
