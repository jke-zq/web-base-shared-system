package cn.edu.ycu.webadmin.remote.rest.service;

import cn.edu.ycu.webadmin.remote.rest.bean.FileInfo;
import cn.edu.ycu.webadmin.remote.rest.dto.DTO;


public interface FileInfoService extends DTO<FileInfo> {
	
	public Boolean isSaved(String filePath);
	
	public void setShared(Integer[] params);
	
	public void deleteIds(Integer[] params);
	
//	public FileInfo findByUserId(int userId);
	
	public FileInfo findByPath(String path);
}
