function QueryString(item) {
	var qs = location.search.match(new RegExp("[\?\&]" + escape(item)
			+ "=([^\&]*)(\&?)"));
//	return qs ? unescape(qs[1]) : unescape(qs);
	return qs ? unescape(qs[1]) : null;
};

//change the userName value to the new one of the QueryString returns new value;
function changeParam( ahref, paraName,defaultStr){
    var newStr=QueryString(paraName);
    var hrefStr=ahref.href;
    if(hrefStr.indexOf("=")<0){
    	ahref.href=ahref.href+"?"+paraName+"="+defaultStr;
    	return true;
    }
    var oldHref=hrefStr.split("=");
    if(newStr != null && oldHref[1] != newStr){
     ahref.href=oldHref[0]+"="+newStr;
    }
  }

// 去除字符串中间空格
String.prototype.Trim = function() {
	return this.replace(/(^\s*)|(\s*$)/g, "");
};
// 去除字符串左侧空格
String.prototype.LTrim = function() {
	return this.replace(/(^\s*)/g, "");
};
// 去除字符串右侧空格
String.prototype.RTrim = function() {
	return this.replace(/(\s*$)/g, "");
};

var XMLHttp = {
	_objPool : [],

	_getInstance : function() {
		for ( var i = 0; i > this._objPool.length; i++) {
			if (this._objPool[i].readyState == 0
					|| this._objPool[i].readyState == 4) {
				return this._objPool[i];
			}
		}

		// IE5中不支持push方法
		this._objPool[this._objPool.length] = this._createObj();

		return this._objPool[this._objPool.length - 1];
	},

	_createObj : function() {
		if (window.XMLHttpRequest) {
			var objXMLHttp = new XMLHttpRequest();

		} else {
			var MSXML = [ "MSXML2.XMLHTTP.5.0", "MSXML2.XMLHTTP.4.0",
					"MSXML2.XMLHTTP.3.0", "MSXML2.XMLHTTP", "Microsoft.XMLHTTP" ];
			for ( var n = 0; n < MSXML.length; n++) {
				try {
				    var	objXMLHttp = new ActiveXObject(MSXML[n]);
					break;
				} catch (e) {
				}
			}
		}

		// mozilla某些版本没有readyState属性
		if (objXMLHttp.readyState == null) {
			objXMLHttp.readyState = 0;

			objXMLHttp.addEventListener("load", function() {
				objXMLHttp.readyState = 4;

				if (typeof objXMLHttp.onreadystatechange == "function") {
					objXMLHttp.onreadystatechange();
				}
			}, false);
		}

		return objXMLHttp;
	},

	// 发送请求(方法[post,get], 地址, 数据, 回调函数 , 异步)
	sendReq : function(method, url, data, callback, XMLHttpbool) {
		if (!XMLHttpbool)
			XMLHttpbool = true;
		var objXMLHttp = this._getInstance();
		with (objXMLHttp) {
			try {
				// 加随机数防止缓存
				if (url.indexOf("?") > 0) {
					url += "&amp;randnum=" + Math.random();
				} else {
					url += "?randnum=" + Math.random();
				}
				open(method, url, XMLHttpbool);
				setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
				send(data);
				onreadystatechange = function(){
						if (objXMLHttp.readyState == 4) {
							if (objXMLHttp.status == 200 || objXMLHttp.status == 304) {
								callback(objXMLHttp, true);
							}
							if (objXMLHttp.status == 201) {
								callback(objXMLHttp, false);
							}
						}
				};
			} catch (e) {
				alert(e);
			}
		}
	},
	/*
	 * 
	 * form 表单
	 * 
	 * url 处理文件名
	 * 
	 * func 提交后的处理方法
	 * 
	 */
	formSubmit : function(form, url, func) {
		if (typeof form != "object") {
			var form = document.getElementById(form);
		}
		var ele = form.elements;
		var post = new Array();
		for ( var i = 0; i < ele.length; i++) {
			// 只考虑了 <input type="text" /> 其它的要加些 if ...
			post[i] = ele[i].value;
		}
		var data = post.join("&");
		this.sendReq("POST", url, data, func, false);
	},

	redirect : function(url,func) {
		this.sendReq("GET", url, null, func, false);
		//alert("get url:" + url);
	}
};