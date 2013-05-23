package cn.edu.ycu.webadmin.remote.rest.unit;


import cn.edu.ycu.webadmin.remote.rest.UserInfo;
import cn.edu.ycu.webadmin.remote.rest.UserInfoServImp;
import cn.edu.ycu.webadmin.remote.rest.UserInfoService;

public class UserInfoServTest {
	
	public static void main(String args[]){
		UserInfoService  service = new UserInfoServImp();
		UserInfo  userInfo = new UserInfo();
		userInfo.setName("name");
		userInfo.setPassword("password");
		service.save(userInfo);
	}
	
}
