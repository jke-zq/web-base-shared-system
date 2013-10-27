package cn.edu.ycu.webadmin.remote.rest.service;

import cn.edu.ycu.webadmin.remote.rest.bean.UserInfo;
import cn.edu.ycu.webadmin.remote.rest.dto.DTO;


public interface UserInfoService extends DTO<UserInfo> {
	
	public boolean checkUser(String name);
	
	public boolean checkPasswd(UserInfo user);
	
	public UserInfo getUserByName(String name);
	public void deleteUsers(Integer[] userIds);
	
}
