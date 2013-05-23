package cn.edu.ycu.webadmin.remote.rest;


public interface FileInfoService extends DTO<FileInfo> {
	
	public Boolean isSaved(String filePath);
	
}
