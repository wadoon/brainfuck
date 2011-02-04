package weigl.bf;

import java.util.LinkedList;
import java.util.List;

public class BFInterpreter {
	public static final char SIGN_PLUS = '+';
	public static final char SIGN_MINUS = '-';
	public static final char SIGN_BACKWARD = '<';
	public static final char SIGN_FORWARD = '>';
	public static final char SIGN_READ = ',';
	public static final char SIGN_WRITE = '.';
	public static final char SIGN_END_LOOP = ']';
	public static final char SIGN_START_LOOP = '[';

	public static final int MEMORY_SZ = 10 * 1024;

	private char[] memory = new char[MEMORY_SZ];

	private char[] code;

	private char currentSign;
	private int currentPos;
	private int pointer = MEMORY_SZ / 2;

	private IOInterface io;
	private List<DebugInterface> debugger = new LinkedList<DebugInterface>();

	private BFInterpreter() {
	}

	public BFInterpreter(String code) {
		this();
		this.code = code.toCharArray();
	}

	public BFInterpreter(char[] c) {
		this();
		code = c;
	}

	public void run() {
		fireStart();
		while (currentPos < code.length) {
			currentSign = code[currentPos];

			if (currentSign == SIGN_START_LOOP) {
				if (memory[pointer] > 0 || memory[pointer] < 255)
					wexecute();
			} else {
				operation();
			}
			currentPos++;
		}
		fireStop();
	}

	protected void fireStop() 
	{
		for (DebugInterface inf : debugger) 
			inf.stop();
	}

	protected void fireStart() {
		for (DebugInterface inf : debugger) 
			inf.start(this);
	}

	protected void fireOpDone() {
		for (DebugInterface inf : debugger) 
			inf.operationDone();
	}

	protected void fireOpStart() {
		for (DebugInterface inf : debugger) 
			inf.operationStart(currentPos, currentSign);
	}

	protected void operation() {
		fireOpStart();
		switch (currentSign) {
		case SIGN_PLUS:
			memory[pointer]++;
			break;
		case SIGN_MINUS:
			memory[pointer]--;
			break;
		case SIGN_FORWARD:
			pointer++;
			break;
		case SIGN_BACKWARD:
			pointer--;
			break;
		case SIGN_READ:
			memory[pointer] = io.read();
			break;
		case SIGN_WRITE:
			io.write(memory[pointer]);
			break;
		default:
			// Do Nothing
			break;
		}
		fireOpDone();
	}

	public void wexecute() {
		int wbegin = currentPos;
		currentPos++;

		loop: while (currentPos < code.length) {
			currentSign = code[currentPos];

			switch (currentSign) {
			case SIGN_END_LOOP:
				if (memory[pointer] != 0) {
					currentPos = wbegin;
					break loop;
				}
				break;
			case SIGN_START_LOOP:
				wexecute();
				break;
			default:
				operation();
				break;
			}
			if (memory[pointer] < 0 && memory[pointer] > 255)
				break;
			currentPos++;
		}
	}

	public void setInOut(IOInterface io) {
		this.io = io;
	}

	public void addDebugger(DebugInterface di) {
		debugger.add(di);
	}

	public void removeDebugger(DebugInterface di) {
		debugger.remove(di);
	}
}
