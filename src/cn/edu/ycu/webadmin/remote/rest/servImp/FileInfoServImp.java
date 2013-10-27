package cn.edu.ycu.webadmin.remote.rest.servImp;


import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.edu.ycu.webadmin.remote.rest.bean.FileInfo;
import cn.edu.ycu.webadmin.remote.rest.dto.DTOSupport;
import cn.edu.ycu.webadmin.remote.rest.service.FileInfoService;



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

	@Override
	public void setShared(Integer[] fileIds) {
		if(fileIds!=null && fileIds.length>0){
			StringBuffer jpql = new StringBuffer();
			for(int i=0;i<fileIds.length;i++){
				jpql.append('?').append((i+1)).append(',');
			}
			jpql.deleteCharAt(jpql.length()-1);
			Query query = em.createQuery("update FileInfo o set o.shared=true where o.id in("+ jpql.toString()+ ")");
			for(int i=0;i<fileIds.length;i++){
				query.setParameter(i+1, fileIds[i]);
			}
			query.executeUpdate();
		}
	}

	@Override
	public void deleteIds(Integer[] fileIds) {
		
		if(fileIds!=null && fileIds.length>0){
			StringBuffer jpql = new StringBuffer();
			for(int i=0;i<fileIds.length;i++){
				jpql.append('?').append((i+1)).append(',');
			}
			jpql.deleteCharAt(jpql.length()-1);
			Query query = em.createQuery("update FileInfo o set o.visible=false  where o.id in("+ jpql.toString()+ ")");
			for(int i=0;i<fileIds.length;i++){
				query.setParameter(i+1, fileIds[i]);
			}
			query.executeUpdate();
		}
	}

//	@Override
//	public FileInfo findByUserId(int userId) {
//		if(userId < 0){
//			return null;
//		}
//		Object o = em.createQuery("select o form FileInfo o where o.UserInfo.id=?1").setParameter(1, userId).getSingleResult();
//		return o == null ? null : (FileInfo)o;
//	}

	@Override
	public FileInfo findByPath(String path) {
		// TODO Auto-generated method stub
		if(path == null || "".equals(path)){
			return null;
		}
		Object o = em.createQuery("select o from FileInfo o where o.filePath=?1").setParameter(1, path).getFirstResult();
		return o == null ? null : (FileInfo)o;
	}
}
