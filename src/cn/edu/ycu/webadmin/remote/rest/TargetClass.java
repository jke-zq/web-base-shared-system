package cn.edu.ycu.webadmin.remote.rest;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import cn.edu.ycu.webadmin.remote.rest.resource.URIResource;


public class TargetClass {
	private Object instance = null;
	private Class<?> resClass = null;
	private URIResource resource = null;
	private XstreamParser parser = null;
	private static final Logger logger = Logger
			.getLogger(TargetClass.class);
	private static ApplicationContext applicationContext;
	
	public TargetClass(URIResource resource) {
		logger.debug("enter createInstance by class name: " + resource);
		System.out.println("enter createInstance by class name: " + resource);
		this.resource = resource;
		if (instance == null) {
			String[] names = resource.getClassName().split("\\.");
			String beanName = names[names.length - 1];
			System.out.println("beanName: " + beanName);
//			if(applicationContext.getBean(""))
			System.out.println("after applicatonContext");
			if (instance == null) {
				try {
					Class<?> c = Class.forName(resource.getClassName());
					instance = c.newInstance();
				} catch (Exception e) {
					RESTUtil.logErrorStack(logger, e);
				}
			}
			if (instance != null)
				this.resClass = instance.getClass();
		}
		if (resource != null)
			parser = new XstreamParser(resource.getAliasMap());
		logger.debug("leaving createInstance, got the instance: " + instance);
	}

	public String get(long id) throws Exception {
		logger.debug("Entering get method with id : " + id);
		String rtn = invokeMethod(resource.getGetMethodName(), id);
		logger.debug("Leaving get method with rtn : " + rtn);
		return rtn;
	}

	public String get() throws Exception {
		logger.debug("Entering get method.");
		String rtn = invokeMethod(resource.getGetMethodName(), null);
		logger.debug("Leaving get method with rtn : " + rtn);
		return rtn;
	}

	public String put(long id) throws Exception {
		logger.debug("Entering put method with id : " + id);
		String rtn = invokeMethod(resource.getPutMethodName(), id);
		logger.debug("Leaving put method with rtn : " + rtn);
		return rtn;
	}

	public String put(String body) throws Exception {
		logger.debug("Entering put method with body : " + body);
		String rtn = invokeMethod(resource.getPutMethodName(), body);
		logger.debug("Leaving put method with rtn : " + rtn);
		return rtn;
	}

	public String delete(long id) throws Exception {
		logger.debug("Entering delete method with id : " + id);
		String rtn = invokeMethod(resource.getDeleteMethodName(), id);
		logger.debug("Leaving delete method with rtn : " + rtn);
		return rtn;
	}

	public String post(String bodyStr) throws Exception {
		logger.debug("Entering post method with bodyStr : " + bodyStr);
		if (bodyStr == null)
			throw new Exception("Input parameter can not be null!");
		String methodName = null;
		Object reqBody = null;
		try {
			if (bodyStr != null && bodyStr.trim().startsWith("<"))
				reqBody = parser.fromXML(bodyStr);
			else
				reqBody = bodyStr;
			logger.debug("Successfully parsed the body to object: " + reqBody);

		} catch (Exception e) {
			RESTUtil.logErrorStack(logger, e);
			reqBody = bodyStr;
		}

		methodName = resource.getPostMethodsMap().get(
				reqBody.getClass().getCanonicalName());
		logger.debug("Got the methodName from PostMethodsMap: " + methodName);

		String rtn = invokeMethod(methodName, reqBody);

		logger.debug("Leaving post method with rtn : " + rtn);
		return rtn;
	}

	private String invokeMethod(String methodName, Object paramValue)
			throws Exception {
		logger.debug("Entering invokeMethod: " + methodName + ", " + paramValue);
		String rtnStr = null;
		StringWriter writer = null;
		try {
			Method method = getMethod(methodName);
			if (method == null)
				throw new IOException(methodName + " not found from: "
						+ resClass);
			Object rtn = null;

			logger.debug("Invoking the method:" + method.getName()
					+ " with param value: " + paramValue);
			Class<?>[] paramTypes = method.getParameterTypes();
			if (paramTypes == null || paramTypes.length == 0) {
				rtn = method.invoke(instance);
			} else {
				rtn = method.invoke(instance,
						getParamValues(paramValue, method));
			}

			if (method.getReturnType().equals(Void.TYPE)) {
				rtnStr = "this operation is done well!";
			} else {
				rtnStr = fromObject(rtn);
			}
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
		}
		logger.debug("Leaving invokeMethod with return String: " + rtnStr);
		return rtnStr;
	}

	private Method getMethod(String methodName) {
		logger.debug("Entering getMethod with params: " + methodName);
		Method rtn = null;
		if (methodName == null || "".equals(methodName)) {
			return null;
		}
		for (Method tmp : resClass.getDeclaredMethods()) {
			if (methodName.equals(tmp.getName())) {
				logger.debug("Found the method whose name is " + methodName);
				Class<?>[] paramTypes = tmp.getParameterTypes();
				if (paramTypes != null) {
					rtn = tmp;
				}
				break;
			}
		}
		logger.debug("Leaving getMethod with return:" + rtn);
		return rtn;
	}

	/**
	 * Change xml text in entity to a class instance specified by c
	 * 
	 * @param c
	 * @param entity
	 * @return
	 */
	private Object toObject(Class<?> c, Object body) throws Exception {
		logger.debug("Entering toObject, with: " + c + "," + body);

		Object rtn = null;
		if (c.getCanonicalName().equals("java.lang.String")) {
			if (body != null)
				rtn = body.toString();
		} else if (c.getCanonicalName().equals("long")
				|| c.getCanonicalName().equals("java.lang.Long")) {
			if (String.class.isInstance(body))
				rtn = Long.parseLong(body.toString());
			else if (Long.class.isInstance(body))
				rtn = body;
			else
				throw new IOException();
		} else {
			if (body != null && parser != null) {
				if (String.class.isInstance(body))
					rtn = parser.fromXml(c, body.toString());
				else
					// already parsed by parser
					rtn = body;
			}
		}
		logger.debug("Leaving toObject, with return value:" + rtn);
		return rtn;
	}

	/**
	 * 
	 * @param c
	 * @param obj
	 * @param writer
	 * @param writeXmlHeader
	 * @throws Exception
	 */
	private String fromObject(Object obj) throws Exception {
		logger.debug("Entering fromObject, with: " + obj);
		String rtn = null;
		String cname = obj.getClass().getCanonicalName();
		if (cname.equals("java.lang.String"))
			rtn = obj.toString();
		else if (cname.equals("long") || cname.equals("java.lang.Long"))
			rtn = obj.toString();
		else {
			if (parser != null) {
				rtn = parser.toXml(obj);
			}

		}
		logger.debug("Leaving fromObject, with return value:" + rtn);
		return rtn;
	}

	private Object[] getParamValues(Object value, Method method) {
		logger.debug("Entering getParamValues with params: " + value + ","
				+ method);
		Class<?>[] paramTypes = method.getParameterTypes();
		Object[] paramValues = new Object[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i++) {
			logger.debug("Processing the paramType:"
					+ paramTypes[i].getCanonicalName());
			try {
				paramValues[i] = toObject(paramTypes[i], value);
			} catch (Exception ex) {
				RESTUtil.logErrorStack(logger, ex);
			}
		}
		logger.debug("Leaving getParamValues with return:"
				+ Arrays.toString(paramValues));
		return paramValues;
	}
}
