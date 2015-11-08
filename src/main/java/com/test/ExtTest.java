package com.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;

public class ExtTest {
	public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Class c = Class.forName("java.lang.reflect.Proxy");
		Field f = c.getDeclaredField("nextUniqueNumberLock");
			  f.setAccessible(true);
		InvocationHandler h = (InvocationHandler)f.get(null);
			  System.out.println(h);	
	}
}
