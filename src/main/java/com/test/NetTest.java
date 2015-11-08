package com.test;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.smartagent.agent.instrumentation.pointcuts.PointCutInvoker;

public class NetTest {
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		
		Class o =	Class.forName("com.smartagent.agent.instrumentation.pointcuts.PointCutInvoker", true, ClassLoader.getSystemClassLoader());
		System.out.println(o.getClassLoader());
		URL obj = new URL("http://www.baidu.com");
		HttpURLConnection conn = (HttpURLConnection)obj.openConnection();
		
		//conn.getInputStream();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		
		conn.getOutputStream();
		
		System.out.println(conn.getResponseCode());
		
		conn.getInputStream();
		
		//Map<String, List<String>> map = conn.getHeaderFields();
		try {
			Class.forName("java.net.URLConnection");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Class o1 =	Class.forName("com.smartagent.agent.instrumentation.pointcuts.PointCutInvoker", true, new MyClassLoader((ClassLoader)null) {
		});
		
		System.out.println(o1.getClassLoader());
	}
	
	public static class MyClassLoader extends ClassLoader{
		public MyClassLoader( ClassLoader loader ){
			super(loader);
		}
	}
	
}
