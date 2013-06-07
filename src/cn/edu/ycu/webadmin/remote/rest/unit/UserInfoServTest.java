package cn.edu.ycu.webadmin.remote.rest.unit;


import cn.edu.ycu.webadmin.remote.rest.bean.UserInfo;
import cn.edu.ycu.webadmin.remote.rest.servImp.UserInfoServImp;
import cn.edu.ycu.webadmin.remote.rest.service.UserInfoService;

public class UserInfoServTest {
	
	public static void main(String args[]){
		UserInfoService  service = new UserInfoServImp();
		UserInfo  userInfo = new UserInfo();
		userInfo.setName("name");
		userInfo.setPassword("password");
		service.save(userInfo);
	}
	
}
