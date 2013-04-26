package com.polycom.edge.webadmin.remote.rest;

import java.util.List;

import javax.persistence.Query;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional
public class FileTypeServImp extends DTOSupport<FileType> implements FileTypeService {
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<FileType> getSubTypes(Integer parentId) {
		if(parentId > 0){
			Query query = em.createQuery("select  o from FileType o where o.parentId = " + parentId);
			return query.getResultList();
		}
		
		return null;
	}


}