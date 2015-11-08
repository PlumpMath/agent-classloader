package com.smartagent.agent.instrumentation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class AgentWrapper implements InvocationHandler{

	public Object invoke(Object proxy, Method method, Object[] args)throws Throwable {
		return null;
	}

	public static void main(String[] args) {
		ClassTransformer.getTrace();
	}
	
	public static void  a(){
		ClassTransformer.getTrace();
	}
	
}
