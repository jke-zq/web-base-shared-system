package com.polycom.edge.webadmin.remote.rest.unit;


import com.polycom.edge.webadmin.remote.rest.UserInfo;
import com.polycom.edge.webadmin.remote.rest.UserInfoServImp;
import com.polycom.edge.webadmin.remote.rest.UserInfoService;

public class UserInfoServTest {
	
	public static void main(String args[]){
		UserInfoService  service = new UserInfoServImp();
		UserInfo  userInfo = new UserInfo();
		userInfo.setName("name");
		userInfo.setPassword("password");
		service.save(userInfo);
	}
	
}
