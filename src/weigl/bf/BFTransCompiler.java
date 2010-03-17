package weigl.bf;

import java.io.File;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import static weigl.bf.BFInterpreter.*;

public class BFTransCompiler {
	private char[] code;
	private char currentSign;
	private int currentPos;
	private PrintStream output;
	private Object name;

	public BFTransCompiler(String code) {
		this.code = code.toCharArray();
	}

	public BFTransCompiler(char[] c, String name, PrintStream printStream) {
		code = c;
		output = printStream;
		this.name = name;
	}

	public void parse() {
		append("public class %s {", name);
		append("private static char[]    memory = new char[20000];");
		append("private static int      pointer = memory.length/2;");
		append("private static boolean  checkLoop() { return memory[pointer] > 0 && memory[pointer] < 255;}");
		append("public static void main(String[] args) throws java.io.IOException {");
		while (currentPos < code.length) {
			currentSign = code[currentPos];
			doOperation();
			currentPos++;
		}
		append("}\n}");
		output.flush();
	}

	private void append(String format, Object... args) {
		output.format(format, args);
	}

	protected void doOperation() {
		switch (currentSign) {
		case SIGN_PLUS:
			append("memory[pointer]++;");
			break;
		case SIGN_MINUS:
			append("memory[pointer]--;");
			break;
		case SIGN_FORWARD:
			append("pointer++;");
			break;
		case SIGN_BACKWARD:
			append("pointer--;");
			break;
		case SIGN_READ:
			append("memory[pointer] = System.in.read();");
			break;
		case SIGN_WRITE:
			append("System.out.print(memory[pointer]);");
			break;
		case SIGN_START_LOOP:
			append("while(checkLoop()){");
			break;
		case SIGN_END_LOOP:
			append("}");
			break;
		}
	}

	private void append(String string) {
		output.println(string);
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
		    System.err.println("you have to specify an input and output file!\n" +
		    		"USAGE: bf2java <input> <output>\n" +
		    		"");
		    return;
		}
		File input = new File(args[0]);

		String out = args[0].substring(0, args[0].indexOf('.') < 0 ? args[0]
				.length() : args[0].indexOf('.'));

		File output = new File(out + ".java");

		char c[] = new char[(int) input.length()];

		FileReader fr = new FileReader(input);
		fr.read(c);

		BFTransCompiler compiler = new BFTransCompiler(c, out, new PrintStream(output));
		compiler.parse();
	}
}