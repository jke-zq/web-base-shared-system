package cn.edu.ycu.webadmin.remote.rest.utils;

public enum ExtensionType {
	DOC("doc","word file"), EXE("exe","app file"), PDF("pdf","pdf file"), PPT("ppt","ppt file"), XLS("xls","xls file"), RAR("rar","rar file"), ZIP("zip","zip file"), JPG("jpg","jpg file"), GIF("gif","gif file"), BMP("bmp","bmp file"), PNG("png","png file"), TXT("txt","txt file"), SWF("swf","swf file"),SO("so","so file");
	private String ext;
	private String alertStr;
	
	private ExtensionType(String ext,String alertStr){
		this.ext = ext;
		this.alertStr = alertStr;
	}
	public static String getAlert(String ext){
		for(ExtensionType et: ExtensionType.values()){
			if(et.getExt() == ext){
				return et.getAlertStr();
			}
		}
		return "not alert message";
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	public String getAlertStr() {
		return alertStr;
	}
	public void setAlertStr(String alertStr) {
		this.alertStr = alertStr;
	}
}
