package com.shell.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * bean与map互转
 * @author shell
 */
public class BeanUtil {
	
	/**
	 * 将bean对象转换为map
	 * @param object
	 * @return
	 */
	public static Map<String, Object> bean2Map(Object obj) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (obj == null) {
			return map;
		}
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();

				// 过滤class属性
				if (!key.equals("class")) {
					// 得到property对应的getter方法
					Method getter = property.getReadMethod();
					Object value = getter.invoke(obj);

					map.put(key, value);
				}

			}
		} catch (Exception e) {

		}
		return map;
	}
	
	/**
	 * 将map转为指定的bean
	 * @param map
	 * @param clazz
	 * @return
	 */
	public static <T> T map2Bean(Map<String, Object> map, Class<T> clazz) {
		try {
			T t = clazz.newInstance();
			if (!map.isEmpty()) {
				BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
				PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors(); 
				for (int i = 0; i< propertyDescriptors.length; i++) { 
		            PropertyDescriptor descriptor = propertyDescriptors[i]; 
		            String propertyName = descriptor.getName(); 

		            if (map.containsKey(propertyName)) { 
		                // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。 
		                Object value = map.get(propertyName); 
		                Object[] args = new Object[1]; 
		                args[0] = value; 

		                descriptor.getWriteMethod().invoke(t, args); 
		            } 
		        }
			}
			return t;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return null;
	}
}
