package cn.edu.ycu.webadmin.remote.rest.service;

import java.util.List;

import cn.edu.ycu.webadmin.remote.rest.bean.FileType;
import cn.edu.ycu.webadmin.remote.rest.dto.DTO;


public interface FileTypeService extends DTO<FileType> {
		
		public List<FileType> getSubTypes(Integer parentId);
		

	
}
