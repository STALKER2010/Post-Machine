package io.github.stalker2010.post.compiler;
import io.github.stalker2010.post.compat.*;

public abstract class PostClass
{
	public volatile BooleanStorage line = new BooleanStorage();
	public volatile int cline = 0;
	public volatile int register = 0;
	public volatile int codeline = 0;
	public volatile boolean debugMode = false;
	public volatile SparseArray<String> code = new SparseArray<String>();
	public abstract void run();
	public abstract String code();
	protected void setCurrentCodeLine(int i) {
		codeline = i;
	}
	protected void shiftL() {
		cline--;
	}
	protected void shiftR() {
		cline++;
	}
	protected void stop() {
		throw new StopException();
	}
	protected boolean is1() {
		return line.get(cline);
	}
	protected void mark() {
		line.put(cline, true);
	}
	protected void unmark() {
		line.put(cline, false);
	}
	protected void registerInc() {
		register++;
	}
	protected void registerDec() {
		register--;
	}
	protected void registerSet(int n) {
		register = n;
	}
	public String code(int i) {
		return code.get(i);
	}
}
