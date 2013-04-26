package com.polycom.edge.webadmin.remote.rest;

import java.util.List;

public interface FileTypeService extends DTO<FileType> {
		
		public List<FileType> getSubTypes(Integer parentId);
		

	
}
