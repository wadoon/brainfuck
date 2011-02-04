package weigl.bf;

/**
 * @author Alexander Weigl <alexweigl@gmail.com>
 */
public interface DebugInterface 
{
	public void start(BFInterpreter bfInterpreter);
	public void stop();

	public void operationStart(int currentPos, char currentSign);
	public void operationDone();
}
