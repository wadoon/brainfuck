package weigl.bf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 
 * @author Alexander Weigl
 * @date a-long-time-ago
 */
public class BFCLI {
    public BFCLI(String file) throws IOException {
	BufferedReader br = new BufferedReader(new FileReader(new File(file)));
	StringBuffer b = new StringBuffer();
	String tmp = null;

	while ((tmp = br.readLine()) != null)
	    b.append(tmp);

	tmp = b.toString();
	
	BFInterpreter interpreter = new BFInterpreter(tmp);
	interpreter.setInOut(new CommandLineIO());
	interpreter.run();
    }

    public static void main(String[] args) throws Exception {
	if (args.length == 1)
	    new BFCLI(args[0]);
	else
	    System.err.println("You have to specify an brainfuck script.\n"
		    + "Usage: bfrun <script>\n");
    }
}

class CommandLineIO implements IOInterface {

    public char read() {
	try {
	    return (char) System.in.read();
	} catch (IOException e) {
	    e.printStackTrace();
	    return 0;
	}
    }

    public void write(char c) {
	System.out.print(c);
    }
}