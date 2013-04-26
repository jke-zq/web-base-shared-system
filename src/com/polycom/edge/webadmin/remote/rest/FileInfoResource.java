package com.polycom.edge.webadmin.remote.rest;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.polycom.edge.webadmin.remote.rest.utils.RepResult;

public class FileInfoResource extends ServerResource {
	@Autowired
	private FileInfoService fileService;

	@Autowired
	private UploadThreadPoolService uploadThrePool;
//	private UploadThreadPoolServImp uploadThrePool;

	private String tempPath = "/tempFiles";
	private volatile Reference baseRef = new Reference("http://localhost:8080");

	public FileInfoResource() {
		getMetadataService().addExtension("multipart",
				MediaType.MULTIPART_FORM_DATA, true);
	}

	@Get
	public Representation get() {
		Representation rep = null;
		StringBuffer bf = new StringBuffer();
		String loginId = null;
		int pageNum = 1;
		
		Form form = getRequest().getReferrerRef().getQueryAsForm();
		// to validate the user wether if valide?
		if(form.getFirstValue("loginId") == null || (loginId = form.getFirstValue("loginId")) == null){
			loginId = "guest";
		}
		if(form.getFirstValue("pageNum") == null ||(pageNum = Integer.parseInt(form.getFirstValue("pageNum"))) < 1){
			pageNum = 1;
		}
		
		// set maxresult =-1 to instate:using 12 default value
		PageView<FileInfo> pageView = new PageView<FileInfo>(-1, pageNum);
		LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();
		orderby.put("id", "desc");
		StringBuffer jpql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		jpql.append(" o.visible = ?1 ");
		params.add(true);
		jpql.append(" and o.shared = ?2 ");
		params.add(true);
		jpql.append(" o.user.name = ?3 ");
		params.add(loginId);
		
		pageView.setQueryResult(fileService.getScrollData(
				pageView.getFirstResult(), pageView.getMaxresult(),jpql.toString(), params.toArray(), orderby));
		bf.append(pageView.getNumList("&") + ";");
		for (FileInfo file : pageView.getRecords()) {
			bf.append(file.getId() + "&" + file.getFileDescription() + "&"
					+ file.getUploadtime().toString() + "&"
					+ file.getFilePath() + "&" + file.getUser().getName());
			bf.append("#");
		}
		bf.deleteCharAt(bf.length() - 1);
		getResponse().setStatus(Status.SUCCESS_OK);
		rep = new StringRepresentation(bf.toString(), MediaType.TEXT_PLAIN);
		System.out.println(bf.toString());
		return rep;
	}

	@Post("multipart")
	public Representation post(Representation entity) {
		Representation rep = null;
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
					for (final Iterator<FileItem> it = items.iterator(); it
							.hasNext();) {
						FileItem fileItem = it.next();
						if (fileItem.getName() == null) {
							if ("userName".equals(fileItem.getFieldName())) {
								userName = new String(fileItem.get(), "UTF-8");
								fileInfo.setUser(new UserInfo(userName));
							}
							if("fileDescription".equals(fileItem
									.getFieldName())){
								fileInfo.setFileDescription(new String(fileItem
										.get(), "UTF-8"));
							}
						} else {
							// copy and delete using java to operating linux
							// commands
							path = uploadThrePool.getServCtx().getRealPath(
									tempPath);
							// //if the user is not new here, then not need to
							// create the dir;
							// File savePath = new File(path);
							// if(!savePath.exists()) savePath.mkdir();
							tmpFileName = userName + "#*" + fileItem.getName();
							System.out.println(path + tmpFileName);
							fileItem.write(new File(path, tmpFileName));
						}
					}
					//to check the file is uploaded finished
					if(tmpFileName != null && !"".equals(tmpFileName) && new File(path + File.separator + tmpFileName).exists()){
						//to add the db
						fileInfo.setFilePath(fileInfo.getUser().getName() + File.separator + userName);
						fileService.save(fileInfo);
						System.out.println("fileInfo:" + fileInfo.toString());
						//failed to save the fileInfo so rollback
						if(fileService.isSaved(fileInfo.getFilePath())){
							String url = "/uploadfilelist.html?userName=" + userName;
							redirectPermanent(new Reference(baseRef,url));
							//copy and delete
							uploadThrePool.copyAndDel(tmpFileName,true);
						}else{
							rep = RepResult.respResult(this, Status.SERVER_ERROR_NOT_IMPLEMENTED, "failed to save the file into Database!",null);
							//delete
							uploadThrePool.copyAndDel(tmpFileName,false);
							return rep;
						}
						
					}

				} catch (Exception e) {
					rep = RepResult.respResult(this, Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage(), e);
				}

			} else {
				rep = RepResult.respResult(this, Status.CLIENT_ERROR_BAD_REQUEST,"MultiPart/form-data required",null);
			}
		} else {
			rep = RepResult.respResult(this, Status.CLIENT_ERROR_BAD_REQUEST,"error,null in the form",null);
		}
		return rep;
	}
	
	
}
