package com.polycom.edge.webadmin.remote.rest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FileInfoServImp extends DTOSupport<FileInfo> implements
		FileInfoService {

	public Boolean isSaved(String filePath) {
		long count = (Long) em
				.createQuery("select count(o) from FileInfo o where o.filePath=?1")
				.setParameter(1, filePath).getSingleResult();
		return count > 0;
	}
}
