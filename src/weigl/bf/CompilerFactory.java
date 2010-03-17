package weigl.bf;

import static com.sun.org.apache.bcel.internal.generic.InstructionConstants.*;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.*;
import com.sun.org.apache.bcel.internal.generic.*;

public class CompilerFactory {

    public static final String MEMORY_NAME = "memory";
    public static final String POINTER_NAME = "pointer";
    public static final int MEM_SZ = 20000;
    public static final Type MEMORY_TYPE = Type.getType((new char[MEM_SZ])
	    .getClass());
    public static final String CHECK_LOOP_NAME = "checkLoop";

    public static final int DEFAULT_ACCESS = Constants.ACC_PUBLIC
	    | Constants.ACC_STATIC;

    public static Field createMemoryField(ConstantPoolGen constantPool) {
	FieldGen field = new FieldGen(DEFAULT_ACCESS, MEMORY_TYPE, MEMORY_NAME,
		constantPool);
	return field.getField();
    }

    public static Field createPointerField(ConstantPoolGen constantPool) {
	FieldGen field = new FieldGen(DEFAULT_ACCESS, Type.INT, POINTER_NAME,
		constantPool);
	return field.getField();
    }

    public static Method createMethodLoop(ClassGen cg) {
	// append("private static boolean  checkLoop() { return memory[pointer] > 0 && memory[pointer] < 255;}");

	final String className = cg.getClassName();
	InstructionFactory fac = new InstructionFactory(cg.getConstantPool());
	InstructionList m = new InstructionList();

	InstructionList ril = new InstructionList();
	ril.append(new ICONST(0));
	ril.append(new IRETURN());
	InstructionHandle retFalse = m.append(ril);

	InstructionList il = new InstructionList();
	il.append(fac.createGetStatic(className, MEMORY_NAME, MEMORY_TYPE));
	il.append(fac.createGetStatic(className, POINTER_NAME, Type.INT));
	il.append(new CALOAD());
	il.append(new IFLE(retFalse));
	il.append(fac.createGetStatic(className, MEMORY_NAME, MEMORY_TYPE));
	il.append(fac.createGetStatic(className, POINTER_NAME, Type.INT));
	il.append(new CALOAD());
	il.append(new SIPUSH((short) 255));
	il.append(new IF_ICMPGE(retFalse));
	il.append(new ICONST(1));
	il.append(new IRETURN()); // return true

	m.insert(retFalse, il);

	MethodGen method = new MethodGen(DEFAULT_ACCESS, Type.BOOLEAN,
		new Type[] {}, new String[] {}, CHECK_LOOP_NAME, className, m,
		cg.getConstantPool());
	method.setMaxStack();
	method.setMaxLocals();
	return method.getMethod();
    }

    public static Method createMain(InstructionList il, ClassGen cg) {
	il.append(RETURN);
	MethodGen method = new MethodGen(DEFAULT_ACCESS, Type.VOID, 
		new Type[]{ Type.getType(String[].class) }, 
		new String[] { "args" }, "main", cg.getClassName(), il, cg.getConstantPool());
	
	method.addException(Exception.class.getCanonicalName());
	method.setMaxStack();
	method.setMaxLocals();
	return method.getMethod();
    }

    public static void incrementValue(ClassGen cg, InstructionList il) {
	InstructionFactory fac = new InstructionFactory(cg);
	il.append(fac.createGetStatic(cg.getClassName(), MEMORY_NAME,
		MEMORY_TYPE));
	il.append(fac
		.createGetStatic(cg.getClassName(), POINTER_NAME, Type.INT));
	il.append(DUP2);
	il.append(CALOAD);
	il.append(ICONST_1);
	il.append(IADD);
	il.append(I2C);
	il.append(CASTORE);
    }

    public static void decrementValue(ClassGen cg, InstructionList il) {
	InstructionFactory fac = new InstructionFactory(cg);
	il.append(fac.createGetStatic(cg.getClassName(), MEMORY_NAME,
		MEMORY_TYPE));
	il.append(fac
		.createGetStatic(cg.getClassName(), POINTER_NAME, Type.INT));
	il.append(DUP2);
	il.append(CALOAD);
	il.append(ICONST_1);
	il.append(ISUB);
	il.append(I2C);
	il.append(CASTORE);
    }

    public static void nextRegister(ClassGen cg, InstructionList il) {
	InstructionFactory fac = new InstructionFactory(cg);
	il.append(fac
		.createGetStatic(cg.getClassName(), POINTER_NAME, Type.INT));
	il.append(DUP);
	il.append(ICONST_1);
	il.append(IADD);
	il.append(fac
		.createPutStatic(cg.getClassName(), POINTER_NAME, Type.INT));
    }

    public static void prevRegister(ClassGen cg, InstructionList il) {
	InstructionFactory fac = new InstructionFactory(cg);
	il.append(fac
		.createGetStatic(cg.getClassName(), POINTER_NAME, Type.INT));
	il.append(DUP);
	il.append(ICONST_1);
	il.append(ISUB);
	il.append(fac
		.createPutStatic(cg.getClassName(), POINTER_NAME, Type.INT));
    }

    public static void readInput(ClassGen cg, InstructionList il) {
	ConstantPoolGen cp = cg.getConstantPool();
	InstructionFactory fac = new InstructionFactory(cg);

	int in = cp.addFieldref("java.lang.System", "in",
		"Ljava/io/PrintStream;");
	int read = cp.addMethodref("java.io.PrintStream", "read", "()I");

	il.append(fac.createGetStatic(cg.getClassName(), MEMORY_NAME,
		MEMORY_TYPE));
	il.append(fac
		.createGetStatic(cg.getClassName(), POINTER_NAME, Type.INT));
	il.append(new GETSTATIC(in));
	il.append(new INVOKEVIRTUAL(read));
	il.append(I2C);
	il.append(CASTORE);
    }

    public static void writeOutput(ClassGen cg, InstructionList il) {
	ConstantPoolGen cp = cg.getConstantPool();
	InstructionFactory fac = new InstructionFactory(cg);

	int out = cp.addFieldref("java.lang.System", "out",
		"Ljava/io/PrintStream;");
	int println = cp.addMethodref("java.io.PrintStream", "print", "(C)V");

	il.append(new GETSTATIC(out));
	il.append(fac.createGetStatic(cg.getClassName(), MEMORY_NAME,
		MEMORY_TYPE));
	il.append(fac
		.createGetStatic(cg.getClassName(), POINTER_NAME, Type.INT));
	il.append(CALOAD);
	il.append(new INVOKEVIRTUAL(println));
    }

    public static void encapsulateWhile(ClassGen cg, InstructionList il,
	    InstructionList innerLoop) {
	InstructionFactory fac = new InstructionFactory(cg);

	if (innerLoop.size() == 0)
	    innerLoop.append(NOP);

	InstructionHandle loop = il.append(innerLoop);
	InstructionHandle loopF = il.append(fac.createInvoke(cg.getClassName(),
		CHECK_LOOP_NAME, Type.BOOLEAN, new Type[] {},
		Constants.INVOKESTATIC));
	il.insert(loop, new GOTO(loopF));
	il.append(new IFNE(loop));
    }

    public static Method createStaticInitializer(ClassGen cg) {
	InstructionList il = new InstructionList();
	InstructionFactory fac = new InstructionFactory(cg);

	il.append(new SIPUSH((short) 20000));
	il.append(new NEWARRAY(Type.CHAR));
	il.append(fac.createPutStatic(cg.getClassName(), MEMORY_NAME,
		MEMORY_TYPE));
	il.append(fac.createGetStatic(cg.getClassName(), MEMORY_NAME,
		MEMORY_TYPE));
	il.append(ARRAYLENGTH);
	il.append(ICONST_2);
	il.append(IDIV);
	il.append(fac
		.createPutStatic(cg.getClassName(), POINTER_NAME, Type.INT));
	il.append(RETURN);

	MethodGen method = new MethodGen(Constants.ACC_STATIC
		| Constants.ACC_STRICT, Type.VOID, new Type[] {},
		new String[] {}, "<clinit>", cg.getClassName(), il, cg
			.getConstantPool());
	method.setMaxStack(2);
	return method.getMethod();
    }
}
