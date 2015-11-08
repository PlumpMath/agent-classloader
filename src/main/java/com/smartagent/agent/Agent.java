package com.smartagent.agent;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.jar.JarFile;

import com.smartagent.agent.deps.org.objectweb.asm.Opcodes;
import com.smartagent.agent.instrumentation.ClassTransformer;

public class Agent {
	public static void premain(String agentOps, Instrumentation inst) throws IOException{  
        System.out.println("启动鸟");  
        // inst.appendToBootstrapClassLoaderSearch(new JarFile(new File("E:\\JavaWork\\Agent\\SmartAgent\\target\\SmartAgent-0.0.1-SNAPSHOT.jar")));
        inst.addTransformer(new ClassTransformer());
    }  
}
