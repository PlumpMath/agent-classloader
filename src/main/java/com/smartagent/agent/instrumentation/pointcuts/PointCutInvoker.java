package com.smartagent.agent.instrumentation.pointcuts;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;

public class PointCutInvoker implements InvocationHandler {

	static{
		System.out.println("=======");
	}
	
	public static PointCutInvoker invoker = new PointCutInvoker();
	
	public static void invoke(Object o){
		if(o instanceof HttpURLConnection){
			HttpURLConnection httpURLConnection = (HttpURLConnection)o;
			System.out.println(httpURLConnection.getURL());
		}
		System.out.println("kokokokokokokokokokookokokoooko");
	}

	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
	invoke(proxy);
		return null;
	}
	
}
