package com.smartagent.agent.instrumentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;

import com.smartagent.agent.deps.org.objectweb.asm.ClassReader;
import com.smartagent.agent.deps.org.objectweb.asm.ClassVisitor;
import com.smartagent.agent.deps.org.objectweb.asm.ClassWriter;
import com.smartagent.agent.deps.org.objectweb.asm.Label;
import com.smartagent.agent.deps.org.objectweb.asm.MethodVisitor;
import com.smartagent.agent.deps.org.objectweb.asm.Opcodes;
import com.smartagent.agent.deps.org.objectweb.asm.Type;
import com.smartagent.agent.deps.org.objectweb.asm.commons.AdviceAdapter;
import com.smartagent.agent.instrumentation.pointcuts.PointCutInvoker;

public class ClassTransformer implements ClassFileTransformer {

	public byte[] transform(ClassLoader loader, String className,Class<?> classBeingRedefined, ProtectionDomain protectionDomain,byte[] classfileBuffer) throws IllegalClassFormatException {
		//System.out.println(className+" " + loader);
		
		if(className.equals("sun/net/www/protocol/http/HttpURLConnection")){
			System.out.println("adada");
			 ClassReader cr = new ClassReader(classfileBuffer);  
			 System.out.println("adada1");
		     ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);  
		     System.out.println("adada2");
		     ClassVisitor cv = new MethodChangeClassAdapter(cw);  
		     System.out.println("adada3.1");
		     try {
				cr.accept(cv, ClassReader.EXPAND_FRAMES);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		     System.out.println("adada3");
		     try {
				FileOutputStream fos = new FileOutputStream(new File("d:\\1.class"));
				System.out.println(cw.toByteArray().length);
				 fos.write(cw.toByteArray());
				 fos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		     System.out.println("csd");
		     return cw.toByteArray();
		}
		return null;
	}


	public static class MethodChangeClassAdapter extends ClassVisitor{

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			//System.out.println(name+" |"+desc +"|"+signature);
			if(name.equals("getOutputStream")){
				System.out.println(199);
				return new MethodChangesAdapter(cv.visitMethod(access, name, desc, signature, exceptions), access, name, desc);
						
			}
			return super.visitMethod(access, name, desc, signature, exceptions);
		}

		public MethodChangeClassAdapter(ClassVisitor cv) {
			super(Opcodes.ASM5 , cv);
		}
		
	}
	
	public static class MethodChangesAdapter extends AdviceAdapter implements Opcodes{

		@Override
		protected void onMethodExit(int opcode) {
			if(opcode!=Opcodes.ATHROW){
//				int  index = newLocal(Type.getType(Object.class));
//				visitVarInsn(Opcodes.ASTORE,index);
//				mv.visitVarInsn(ALOAD, 0);
//				mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(PointCutInvoker.class), "invoke", "(Ljava/lang/Object;)V", false);
//				visitVarInsn(Opcodes.ALOAD,index);
			}
		}

		@Override
		protected void onMethodEnter() {
			// TODO Auto-generated method stub
			//super.onMethodEnter();
			
			mv.visitVarInsn(ALOAD, 0);
			/**
			try {
				//mv.visitFieldInsn(Opcodes.GETSTATIC, Type.getInternalName(PointCutInvoker.class), "invoker", Type.getInternalName(PointCutInvoker.class));
				//mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(PointCutInvoker.class), "invoke", Type.getMethodDescriptor(PointCutInvoker.class.getDeclaredMethod("invoke", new Class[]{Object.class}))    , false);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			**/
			
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLdcInsn("com.smartagent.agent.instrumentation.pointcuts.PointCutInvoker");
			mv.visitInsn(ICONST_0);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/ClassLoader", "getSystemClassLoader", "()Ljava/lang/ClassLoader;", false);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;", false);
			mv.visitVarInsn(ASTORE, 1);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn("invoke");
			mv.visitInsn(ICONST_1);
			mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
			mv.visitInsn(DUP);
			mv.visitInsn(ICONST_0);
			mv.visitLdcInsn(Type.getType("Ljava/lang/Object;"));
			mv.visitInsn(AASTORE);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getDeclaredMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
			mv.visitVarInsn(ASTORE, 2);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitInsn(ACONST_NULL);
			mv.visitInsn(ICONST_1);
			mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
			mv.visitInsn(DUP);
			mv.visitInsn(ICONST_0);
			mv.visitInsn(ACONST_NULL);
			mv.visitInsn(AASTORE);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);
			mv.visitInsn(POP);
			
			System.out.println(222);
		}

		protected MethodChangesAdapter(MethodVisitor mv, int access,String name, String desc) {
			super(Opcodes.ASM5, mv, access, name, desc);
		}

		
		
		

		
		
	}
	
	public static String getTrace(){
		System.out.println("调用了一次");
		return null;
	}
	
	public static void main(String[] args) throws NoSuchMethodException, SecurityException {
		// getTrace();
		 System.out.println(Type.getMethodDescriptor(PointCutInvoker.class.getDeclaredMethod("invoke", new Class[]{Object.class,Method.class,Object[].class})));
	}
}
