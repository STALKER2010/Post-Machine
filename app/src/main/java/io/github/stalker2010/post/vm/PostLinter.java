package io.github.stalker2010.post.vm;
import java.io.*;
import java.util.*;
import static io.github.stalker2010.post.PostApplication.currentDoc;
import java.lang.ref.*;

public class PostLinter
{
	public static class LintMessage implements Serializable
	{
		public volatile String text = "";
		public volatile int line = -1;
		public LintMessage text(final String text)
		{
			this.text = text;
			return this;
		}
		public LintMessage line(final int line)
		{
			this.line = line;
			return this;
		}

		@Override
		public String toString()
		{
			return getClass().getSimpleName() + "[" + text + ":" + line + "]";
		}
	}
	public final List<LintMessage> messages = new ArrayList<LintMessage>();
	private final WeakReference<VM> vm;
	public PostLinter(final VM vm)
	{
		this.vm = new WeakReference<VM>(vm);
	}
	public void post(final String str, final int line)
	{
		messages.add(new LintMessage().text(str).line(line));
	}
	public void post(final String str)
	{
		post(str, vm.get().cpos);
	}
	static PostLinter current() {
		return currentDoc().linter;
	}
}
