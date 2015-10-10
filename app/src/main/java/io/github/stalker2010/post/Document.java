package io.github.stalker2010.post;

import io.github.stalker2010.post.vm.*;
import java.io.*;
import java.util.*;

public class Document implements Closeable, Serializable
{
	public String name = "new";
	public File file = null;
	public VM vm = new VM();
	public PostLinter linter = new PostLinter(vm);
	@Override
	public void close()
	{
		vm.interruptVM();
		vm.callbacks.clear();
		vm.code.clear();
		vm.line.clear();
		linter.messages.clear();
		vm.log = null;
		vm = null;
		linter = null;
		file = null;
	}
	public boolean write() {
		if (file == null) {
			return false;
		}
		try
		{
			FileWriter w = new FileWriter(file);
			w.append(vm.writeCode());
			w.flush();
			w.close();
			return true;
		}
		catch (IOException e)
		{
			return false;
		}
	}
	public boolean read() {
		if (file == null) {
			return false;
		}
		try
		{
			Scanner s = new Scanner(file);
			StringBuilder b = new StringBuilder();
			while (s.hasNextLine()) {
				b = b.append(s.nextLine()).append("\n");
			}
			vm.load(b.toString(), false);
			return true;
		}
		catch (IOException e)
		{
			return false;
		}
	}
	public void setFile(File f) {
		file = f;
		name = f.getName();
	}
	
}
