package com.polycom.edge.webadmin.remote.rest;

public interface FileInfoService extends DTO<FileInfo> {
	
	public Boolean isSaved(String filePath);
	
}
