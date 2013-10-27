package cn.edu.ycu.webadmin.remote.rest.servImp;

import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.edu.ycu.webadmin.remote.rest.bean.UserInfo;
import cn.edu.ycu.webadmin.remote.rest.dto.DTOSupport;
import cn.edu.ycu.webadmin.remote.rest.service.UserInfoService;
import cn.edu.ycu.webadmin.remote.rest.utils.StrUtils;

@Service
@Transactional
public class UserInfoServImp extends DTOSupport<UserInfo> implements
		UserInfoService {

	public boolean checkUser(String name) {
		long count = (Long) em
				.createQuery("select count(o) from UserInfo o where o.name=?1")
				.setParameter(1, name).getSingleResult();
		return count > 0;
	}

	public boolean checkPasswd(UserInfo user) {
		long count = (Long) em
				.createQuery(
						"select count(o) from UserInfo o where o.name=?1 and o.password=?2")
				.setParameter(1, user.getName())
				.setParameter(2, user.getPassword()).getSingleResult();
		return count > 0;
	}

	@Override
	public UserInfo getUserByName(String name) {
		if (StrUtils.isBlank(name)) {
			return null;
		}
		Object user = em.createQuery("select o from UserInfo o where o.name=?1").setParameter(1, name).getSingleResult();
		return user!=null ? (UserInfo)user:null;
	}

	@Override
	public void deleteUsers(Integer[] userIds) {
		if (userIds != null && userIds.length > 0) {
			StringBuffer jpql = new StringBuffer();
			for (int i = 0; i < userIds.length; i++) {
				jpql.append('?').append((i + 1)).append(',');
			}
			jpql.deleteCharAt(jpql.length() - 1);
			Query query = em.createQuery("delete  UserInfo o where o.id in("
					+ jpql.toString() + ")");
			for (int i = 0; i < userIds.length; i++) {
				query.setParameter(i + 1, userIds[i]);
			}
			query.executeUpdate();
		}
	}
}
