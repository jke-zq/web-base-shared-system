/*#######  断点续传  ######
author：linrb
createTime: 2012-08-22
QQ: 569830404
*/
var websocket = null;  //websocket
var msg = null; //日志
var paragraph = 10240;  //每次分片传输文件的大小 10KB
var blob = null;//  分片数据的载体Blob对象
var file = null; //传输的文件
var startSize,endSize = 0; //分片的始终字节点
var uploadState = 0;  // 0: 无上传/取消， 1： 上传中， 2： 暂停

//初始化消息框
function init(){
	msg = document.getElementById("msg");
}
/**
 * 分片上传文件
 */
function uploadFile() {
	if(file){
		//将上传状态设置成1
		uploadState = 1;
		endSize = getLastestUploadEndSize(file);
		var reader = new FileReader();
		reader.onload = function loaded(evt) {
			var ArrayBuffer = evt.target.result;
			websocket.send(ArrayBuffer);
			uploadProgress(endSize);
		};
		if(endSize < file.size){
			//先发送文件名称
			//websocket.send(file.name);
			//处理文件发送（字节）
	        startSize = endSize;
	        if(paragraph > (file.size - endSize)){
	        	endSize = file.size;
	        }else{
	        	endSize += paragraph ;
	        }
	        if (file.webkitSlice) {
    		  //webkit浏览器
	        	blob = file.webkitSlice(startSize, endSize);
	        }else 
	        	blob = file.slice(startSize, endSize);
	        reader.readAsArrayBuffer(blob);
	    }
	}
}

//显示处理进程
function uploadProgress(uploadLen) {
    var percentComplete = Math.round(uploadLen * 100 / file.size);
    document.getElementById('progressNumber').innerHTML = percentComplete.toString() + '%';

    //保存到LocalStorage一边下次传输，可以记忆起这个断点
    localStorage.setItem(file.lastModifiedDate + "_" + file.name, uploadLen);
    
}

//WebSocket连接
function webSocketConn(){
	try{
		var readyState = new Array("正在连接", "已建立连接", "正在关闭连接"
					, "已关闭连接");
		var host = "ws://localhost:8000";
		websocket = new WebSocket(host);
		websocket.onopen = function(){
			msg.innerHTML += "<p>Socket状态： " + readyState[websocket.readyState] + "</p>";
		};
		websocket.onmessage = function(event){
			//每上传一个分片之后，等待介绍了服务端的提示之后再做下一个分片上传
			if(event.data.indexOf("ok") != -1 && uploadState == 1){
				if(endSize == file.size){
					localStorage.removeItem(file.lastModifiedDate + "_" + file.name);
					msg.innerHTML += "<p>上传完成!!</p>";
					websocket.close();//结束上传
				}else{
					uploadFile();
				}
			}
		};
		websocket.onclose = function(){
			msg.innerHTML += "<p>Socket状态： " + readyState[websocket.readyState] + "</p>";
		};
		msg.innerHTML += "<p>Socket状态： " + readyState[websocket.readyState] + "</p>";
	}catch(exception){
		msg.innerHTML += "<p>有错误发生</p>";
		return;
	}
}

/*
暂停上传
*/
function pauseUpload(){
	uploadState = 2;
}

/**
 * 从localStorage检查最后一次上传的字节
 */
function getLastestUploadEndSize(uploadFile){
	var lastestLen = localStorage.getItem(uploadFile.lastModifiedDate + "_" + uploadFile.name);
	if(lastestLen){
		return parseInt(lastestLen);
	}else{
		return 0;
	}
}


/*
	发送文件名
*/
function sendFileName(){
	websocket.send(file.name);
}
/**
 * 选择文件之后触发事件
 */
function fileSelected() {
  file = document.getElementById('fileToUpload').files[0];
  if (file) {
    var fileSize = 0;
    if (file.size > 1024 * 1024)
      fileSize = (Math.round(file.size * 100 / (1024 * 1024)) / 100).toString() + 'MB';
    else
      fileSize = (Math.round(file.size * 100 / 1024) / 100).toString() + 'KB';

    document.getElementById('fileName').innerHTML = 'Name: ' + file.name;
    document.getElementById('fileSize').innerHTML = 'Size: ' + fileSize;
    document.getElementById('fileType').innerHTML = 'Type: ' + file.type;
  }
}