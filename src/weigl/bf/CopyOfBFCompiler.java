package weigl.bf;

import static org.apache.bcel.Constants.ACC_PUBLIC;
import static org.apache.bcel.Constants.ACC_SUPER;
import static weigl.bf.BFInterpreter.SIGN_BACKWARD;
import static weigl.bf.BFInterpreter.SIGN_END_LOOP;
import static weigl.bf.BFInterpreter.SIGN_FORWARD;
import static weigl.bf.BFInterpreter.SIGN_MINUS;
import static weigl.bf.BFInterpreter.SIGN_PLUS;
import static weigl.bf.BFInterpreter.SIGN_READ;
import static weigl.bf.BFInterpreter.SIGN_START_LOOP;
import static weigl.bf.BFInterpreter.SIGN_WRITE;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Stack;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.verifier.NativeVerifier;
import org.apache.bcel.verifier.Verifier;
import org.apache.bcel.verifier.VerifierFactory;

public class CopyOfBFCompiler {
	private char[] code;
	private OutputStream output;
	private String name;

	public CopyOfBFCompiler(String code) {
		this.code = code.toCharArray();
	}

	public CopyOfBFCompiler(char[] c, String name, OutputStream printStream) {
		code = c;
		output = printStream;
		this.name = name;
	}

	int currentPos = 0;

	public void parse() {
		ClassGen cg = new ClassGen(name, "java.lang.Object", name, ACC_PUBLIC
				| ACC_SUPER, null);

		cg.addField(CompilerFactory.createMemoryField(cg.getConstantPool()));
		cg.addField(CompilerFactory.createPointerField(cg.getConstantPool()));
		cg.addMethod(CompilerFactory.createStaticInitializer(cg));
		cg.addMethod(CompilerFactory.createMethodLoop(cg));

		InstructionList mainList = sub(cg);
		Method main = CompilerFactory.createMain(mainList, cg);
		cg.addMethod(main);
		
		mainList.dispose();
//		cg.addEmptyConstructor(ACC_PUBLIC);
		write(cg);
	}

	private InstructionList sub(ClassGen cg) {
		InstructionList il = new InstructionList();
		loop: while (currentPos < code.length) {
			char currentSign = code[currentPos++];
			switch (currentSign) {
			case SIGN_PLUS:
				CompilerFactory.incrementValue(cg, il);
				break;
			case SIGN_MINUS:
				CompilerFactory.decrementValue(cg, il);
				break;
			case SIGN_FORWARD:
				CompilerFactory.nextRegister(cg, il);
				break;
			case SIGN_BACKWARD:
				CompilerFactory.prevRegister(cg, il);
				break;
			case SIGN_READ:
				CompilerFactory.readInput(cg, il);
				break;
			case SIGN_WRITE:
				CompilerFactory.writeOutput(cg, il);
				break;
			case SIGN_START_LOOP:
				InstructionList loop = sub(cg);
				CompilerFactory.encapsulateWhile(cg, il, loop);
				break;
			case SIGN_END_LOOP:
				break loop;
			}
		}
		return il;
	}

	private void write(ClassGen cg) {
		JavaClass clazz = cg.getJavaClass();
		try {
			clazz.dump(output);
			clazz.dump(new File("bin/Code.class"));
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			return;
		}
		File input = new File(args[0]);

		String out = args[0].substring(0, args[0].indexOf('.') < 0 ? args[0]
				.length() : args[0].indexOf('.'));

		File output = new File(out + ".class");

		char c[] = new char[(int) input.length()];

		FileReader fr = new FileReader(input);
		fr.read(c);

		CopyOfBFCompiler compiler = new CopyOfBFCompiler(c, out,
				new PrintStream(output));
		compiler.parse();
			Verifier.main(new String[] { output.getName() });
			NativeVerifier.main(new String[] { output.getName() });
	}
}