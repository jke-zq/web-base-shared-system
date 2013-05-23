package cn.edu.ycu.webadmin.remote.rest;

import java.util.List;


public interface FileTypeService extends DTO<FileType> {
		
		public List<FileType> getSubTypes(Integer parentId);
		

	
}
