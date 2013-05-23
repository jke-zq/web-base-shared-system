package cn.edu.ycu.webadmin.remote.rest;


public interface UserInfoService extends DTO<UserInfo> {
	
	public boolean checkUser(String name);
	
	public boolean checkPasswd(UserInfo user);
	
	public UserInfo getUserByName(String name);
	
}
