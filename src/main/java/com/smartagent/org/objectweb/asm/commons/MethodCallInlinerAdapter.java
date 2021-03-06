package com.smartagent.org.objectweb.asm.commons;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.smartagent.agent.deps.org.objectweb.asm.Label;
import com.smartagent.agent.deps.org.objectweb.asm.MethodVisitor;
import com.smartagent.agent.deps.org.objectweb.asm.Opcodes;
import com.smartagent.agent.deps.org.objectweb.asm.Type;
import com.smartagent.agent.deps.org.objectweb.asm.commons.AnalyzerAdapter;
import com.smartagent.agent.deps.org.objectweb.asm.commons.LocalVariablesSorter;
import com.smartagent.agent.deps.org.objectweb.asm.commons.Remapper;
import com.smartagent.agent.deps.org.objectweb.asm.commons.RemappingMethodAdapter;
import com.smartagent.agent.deps.org.objectweb.asm.commons.TryCatchBlockSorter;
import com.smartagent.agent.deps.org.objectweb.asm.tree.MethodNode;

public abstract class MethodCallInlinerAdapter extends LocalVariablesSorter
{
  private final AnalyzerAdapter analyzerAdapter;
  private Map<String, InlinedMethod> inliners;
  
  public static class InlinedMethod
  {
    public final MethodNode method;
    public final Remapper remapper;
    MethodCallInlinerAdapter.InliningAdapter inliner;
    
    public InlinedMethod(MethodNode method, Remapper remapper)
    {
      this.method = method;
      this.remapper = remapper;
    }
  }
  
  static InlinedMethod DO_NOT_INLINE = new InlinedMethod(null, null);
  
  public MethodCallInlinerAdapter(String owner, int access, String name, String desc, MethodVisitor next, boolean inlineFrames){
    this(Opcodes.ASM5, owner, access, name, desc, next, inlineFrames);
  }
  
  protected MethodCallInlinerAdapter(int api, String owner, int access, String name, String desc, MethodVisitor next, boolean inlineFrames)
  {
    super(api, access, desc, getNext(owner, access, name, desc, next, inlineFrames));
    this.analyzerAdapter = (inlineFrames ? (AnalyzerAdapter)this.mv : null);
  }
  
  private static MethodVisitor getNext(String owner, int access, String name, String desc, MethodVisitor next, boolean inlineFrames)
  {
    MethodVisitor mv = new TryCatchBlockSorter(next, access, name, desc, null, null);
    if (inlineFrames) {
      mv = new AnalyzerAdapter(owner, access, name, desc, mv);
    }
    return mv;
  }
  
  public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf)
  {
    InlinedMethod inliner = getInliner(owner, name, desc);
    if (inliner == DO_NOT_INLINE)
    {
      super.visitMethodInsn(opcode, owner, name, desc, itf);
      return;
    }
    if (inliner.inliner == null)
    {
      MethodVisitor mv = this.mv;
      if (this.analyzerAdapter != null) {
        mv = new MergeFrameAdapter(this.api, this.analyzerAdapter, mv);
      }
      int access = opcode == 184 ? 8 : 0;
      inliner.inliner = new InliningAdapter(this.api, access, desc, this, mv, inliner.remapper);
    }
    inliner.method.accept(inliner.inliner);
  }
  
  protected abstract InlinedMethod mustInline(String paramString1, String paramString2, String paramString3);
  
  private InlinedMethod getInliner(String owner, String name, String desc)
  {
    if (this.inliners == null) {
      this.inliners = new HashMap();
    }
    String key = owner + "." + name + desc;
    InlinedMethod method = (InlinedMethod)this.inliners.get(key);
    if (method == null)
    {
      method = mustInline(owner, name, desc);
      if (method == null) {
        method = DO_NOT_INLINE;
      } else {
        method.method.instructions.resetLabels();
      }
      this.inliners.put(key, method);
    }
    return method;
  }
  
  static class InliningAdapter
    extends RemappingMethodAdapter
  {
    private final int access;
    private final String desc;
    private final LocalVariablesSorter caller;
    private Label end;
    
    public InliningAdapter(int api, int access, String desc, LocalVariablesSorter caller, MethodVisitor next, Remapper remapper)
    {
      super(access, desc, next, remapper);
      this.access = access;
      this.desc = desc;
      this.caller = caller;
    }
    
    public void visitCode()
    {
      super.visitCode();
      int off = (this.access & 0x8) != 0 ? 0 : 1;
      Type[] args = Type.getArgumentTypes(this.desc);
      
      int argRegister = off;
      for (int i = 0; i < args.length; i++) {
        argRegister += args[i].getSize();
      }
      for (int i = args.length - 1; i >= 0; i--)
      {
        argRegister -= args[i].getSize();
        visitVarInsn(args[i].getOpcode(54), argRegister);
      }
      if (off > 0) {
        visitVarInsn(58, 0);
      }
      this.end = new Label();
    }
    
    public void visitInsn(int opcode)
    {
      if ((opcode >= 172) && (opcode <= 177)) {
        super.visitJumpInsn(167, this.end);
      } else {
        super.visitInsn(opcode);
      }
    }
    
    public void visitVarInsn(int opcode, int var)
    {
      super.visitVarInsn(opcode, var + this.firstLocal);
    }
    
    public void visitIincInsn(int var, int increment)
    {
      super.visitIincInsn(var + this.firstLocal, increment);
    }
    
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index)
    {
      super.visitLocalVariable(name, desc, signature, start, end, index + this.firstLocal);
    }
    
    public void visitMaxs(int stack, int locals)
    {
      super.visitLabel(this.end);
    }
    
    public void visitEnd() {}
    
    protected int newLocalMapping(Type type)
    {
      return this.caller.newLocal(type);
    }
  }
  
  static class MergeFrameAdapter
    extends MethodVisitor
  {
    private final AnalyzerAdapter analyzerAdapter;
    
    public MergeFrameAdapter(int api, AnalyzerAdapter analyzerAdapter, MethodVisitor next)
    {
      super(Opcodes.ASM5,next);
      this.analyzerAdapter = analyzerAdapter;
    }
    
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack)
    {
      List<Object> callerLocal = this.analyzerAdapter.locals;
      int nCallerLocal = callerLocal == null ? 0 : callerLocal.size();
      int nMergedLocal = Math.max(nCallerLocal, nLocal);
      Object[] mergedLocal = new Object[nMergedLocal];
      for (int i = 0; i < nCallerLocal; i++) {
        if (callerLocal.get(i) != Opcodes.TOP) {
          mergedLocal[i] = callerLocal.get(i);
        }
      }
      for (int i = 0; i < nLocal; i++) {
        if (local[i] != Opcodes.TOP) {
          mergedLocal[i] = local[i];
        }
      }
      List<Object> callerStack = this.analyzerAdapter.stack;
      int nCallerStack = callerStack == null ? 0 : callerStack.size();
      int nMergedStack = nCallerStack + nStack;
      Object[] mergedStack = new Object[nMergedStack];
      for (int i = 0; i < nCallerStack; i++) {
        mergedStack[i] = callerStack.get(i);
      }
      if (nStack > 0) {
        System.arraycopy(stack, 0, mergedStack, nCallerStack, nStack);
      }
      super.visitFrame(type, nMergedLocal, mergedLocal, nMergedStack, mergedStack);
    }
  }
}
