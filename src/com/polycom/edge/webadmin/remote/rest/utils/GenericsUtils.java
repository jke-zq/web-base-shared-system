package com.polycom.edge.webadmin.remote.rest.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GenericsUtils {

	@SuppressWarnings("unchecked")
	public static Class getSuperClassGenricType(Class clazz, int index) {
		Type geneType = clazz.getGenericSuperclass();
		if (geneType instanceof ParameterizedType) {

			Type[] paraTypes = ((ParameterizedType) geneType)
					.getActualTypeArguments();
			if (index < 0 || index >= paraTypes.length) {
				new RuntimeException("index is "
						+ (index < 0 ? "less than zero!"
								: "more than the sum of parameters!"));
			}
			if(paraTypes[index] instanceof Class){
				return (Class) paraTypes[index];
			}
		}
		return Object.class;
	}

	@SuppressWarnings("unchecked")
	public static Class getSuperClassGenricType(Class clazz) {
		return getSuperClassGenricType(clazz, 0);
	}

	@SuppressWarnings("unchecked")
	public static Class getMethodGenericReturnType(Method method, int index) {

		Type retnType = method.getReturnType();
		if(retnType instanceof ParameterizedType){
			Type[] paramTypes = ((ParameterizedType)retnType).getActualTypeArguments();
			if(index < 0 || index >= paramTypes.length){
				new RuntimeException("index is " + (index < 0 ? "less than zero!":"more than the sum of params!"));
			}
			if(paramTypes[index] instanceof Class){
				return (Class)paramTypes[index];
			}
		}
		return Object.class;
	}

	@SuppressWarnings("unchecked")
	public static Class getMethodGenericReturnType(Method method) {
		return getMethodGenericReturnType(method, 0);
	}

	@SuppressWarnings("unchecked")
	public static List<Class> getMethodGenericParameterTypes(Method method,
			int index) {
		List<Class> listParamType = new ArrayList<Class>();
		Type[] arguTypes = method.getParameterTypes();
		if(index < 0 || index >= arguTypes.length){
			new RuntimeException("index is " + (index < 0 ? "less than zero!":"more than the sum of params"));
		}
		if(arguTypes[index] instanceof ParameterizedType){
			Type[] paramTypes = ((ParameterizedType)arguTypes[index]).getActualTypeArguments();
			for(Type paramType : paramTypes){
				listParamType.add(paramType instanceof Class ? (Class)paramType : Object.class);
			}
		}
		return listParamType;
	}


	@SuppressWarnings("unchecked")
	public static List<Class> getMethodGenericParameterTypes(Method method) {
		return getMethodGenericParameterTypes(method, 0);
	}


	@SuppressWarnings("unchecked")
	public static Class getFieldGenericType(Field field, int index) {
		Type fieldType = field.getGenericType();
		if(fieldType instanceof ParameterizedType){
			Type[] paramTypes =((ParameterizedType)fieldType).getActualTypeArguments();
			if(index < 0 || index >= paramTypes.length){
				new RuntimeException("index is " + (index > 0 ? "less than zero!" : "more than the sum of params"));
			}
			if(paramTypes[index] instanceof Class){
				return (Class)paramTypes[index];
			}
		}
		return Object.class;

	}

	@SuppressWarnings("unchecked")
	public static Class getFieldGenericType(Field field) {
		return getFieldGenericType(field, 0);
	}
}
