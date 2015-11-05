package io.github.stalker2010.post.vm;
import java.lang.ref.*;
import static io.github.stalker2010.post.PostApplication.current;

public class OP implements Runnable
{
	public final String[] args;
	public final String op;
	public final WeakReference<VM> vm;
	public OP(final VM vm, final String op, final String[] args)
	{
		this.op = op;
		this.args = args;
		this.vm = new WeakReference<VM>(vm);
	}
	private static final String[] ops = new String[] {
		"<", ">", "←", "→", "M", "1", "X", "0", "++", "--"
	};
	public static boolean isApplicableOp(final String op)
	{
		for (int i=0; i < ops.length; i++)
		{
			if (op.equals(ops[i]))
			{
				return true;
			}
		}
		return false;
	}
	public void shiftLeft()
	{
		final VM v = vm.get();
		v.cline--;
	}
	public void shiftRight()
	{
		final VM v = vm.get();
		v.cline++;
	}
	public void stop()
	{
		final VM v = vm.get();
		v.stopVM();
	}
	public void mark()
	{
		final VM v = vm.get();
		if (!v.line.get(v.cline))
		{
			v.line.put(v.cline, true);
		}
		else
		{
			current().linter.post("[perf] Cell is already 1");
		}
	}
	public void unmark()
	{
		final VM v = vm.get();
		if (v.line.get(v.cline))
		{
			v.line.put(v.cline, false);
		}
		else
		{
			current().linter.post("[perf] Cell is already 0");
		}
	}
	public void registerAdd()
	{
		final VM v = vm.get();
		v.register++;
	}
	public void registerSubstract()
	{
		final VM v = vm.get();
		v.register--;
	}
	public void registerSet(int value)
	{
		final VM v = vm.get();
		v.register = value;
	}
	public boolean isCorrect()
	{
		return true;
	}
	public void gotoLine(final int id)
	{
		final VM v = vm.get();
		if (v.cpos == id)
		{
			current().linter.post("[warn] GoTo the same line. Will fall into infinite cycle. Went to next line instead.");
			v.cpos++;
		}
		else
		{
			v.cpos = id;
		}
	}
	public void gotoNextLine()
	{
		final VM v = vm.get();
		v.cpos++;
	}

	@Override
	public void run()
	{
		if (op.equals("<") || op.equals("←"))
		{
			shiftLeft();
		}
		else if (op.equals(">") || op.equals("→"))
		{
			shiftRight();
		}
		else if (op.equals("++"))
		{
			registerAdd();
		}
		else if (op.equals("--"))
		{
			registerSubstract();
		}
		else if (op.equals("M") || op.equals("1"))
		{
			mark();
		}
		else if (op.equals("X") || op.equals("0"))
		{
			unmark();
		}
		if (args.length == 1)
		{
			try
			{
				gotoLine(Integer.valueOf(args[0]));
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
				current().linter.post("[error] Next line number is not a number");
				vm.get().interruptVM();
			}
		}
		else
		{
			gotoNextLine();
		}
	}

	@Override
	public String toString()
	{
		final StringBuilder b = new StringBuilder();
		for (String s: args)
		{
			b.append(" ");
			b.append(s);
		}
		return (op + b.toString()).trim();
	}

}
