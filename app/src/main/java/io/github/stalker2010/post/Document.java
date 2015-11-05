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
	public void setFile(File f) {
		file = f;
		name = f.getName();
	}
	public boolean loadFromFile() {
		if (file == null) {
			return false;
		}
		String res = StorageUtils.get().read(name);
		if (res == null) {
			return false;
		}
		vm.load(res, false);
		return true;
	}
	public boolean saveToFile() {
		if (file == null) {
			return false;
		}
		String code = vm.writeCode();
		return StorageUtils.get().save(name, code);
	}
	
}
