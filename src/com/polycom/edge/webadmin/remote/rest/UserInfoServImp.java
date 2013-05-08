package com.polycom.edge.webadmin.remote.rest;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polycom.edge.webadmin.remote.rest.utils.StrUtils;

@Service@Transactional
public class UserInfoServImp extends DTOSupport<UserInfo> implements UserInfoService {

	
	public boolean checkUser(String name) {
		long count = (Long)em.createQuery("select count(o) from UserInfo o where o.name=?1").setParameter(1,name).getSingleResult();
         return count>0;
	}

	
	public boolean checkPasswd(UserInfo user) {
		long count = (Long)em.createQuery("select count(o) from UserInfo o where o.name=?1 and o.password=?2").setParameter(1,user.getName()).setParameter(2,user.getPassword()).getSingleResult();
        return count>0;
	}


	@Override
	public UserInfo getUserByName(String name) {
		if(StrUtils.isBlank(name)){
			return null;
		}
		return (UserInfo)em.createQuery("select o from UserInfo o where o.name=?1").setParameter(1, name).getSingleResult();
	}

}
