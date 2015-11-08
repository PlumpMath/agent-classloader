package com.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Main {
	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class o =	Class.forName("com.smartagent.agent.instrumentation.pointcuts.PointCutInvoker", false, ClassLoader.getSystemClassLoader());
		System.out.println(o.getClassLoader());
		 Method m =	  o.getDeclaredMethod("invoke", new Class[]{ Object.class });
		 		m.invoke(null, (Object)null);
			  
		
	}
}	
