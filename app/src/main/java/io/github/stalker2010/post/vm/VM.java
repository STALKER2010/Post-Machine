package io.github.stalker2010.post.vm;

import java.util.ArrayList;
import java.util.List;
import io.github.stalker2010.post.compat.SparseArray;
import io.github.stalker2010.post.compat.*;

public final class VM
{
	public volatile int register = 0;
	public final SparseArray<OP> code = new SparseArray<OP>(40);
	public volatile BooleanStorage line = new BooleanStorage();
	public volatile int cpos = -1;
	public volatile int cline = 0;
	public volatile VMState state = VMState.IDLE;
	public volatile boolean stopFlag = false;
	public volatile boolean debugMode = false;
	public volatile StringBuilder log = new StringBuilder();
	public volatile String latestLine = "";
	public static final class VMOptions
	{
		public static final int OP_LIMIT = 10000;
	}
	public static final List<OnVMStateChange> callbacks = new ArrayList<OnVMStateChange>();
	public static interface OnVMStateChange
	{
		public void onVMStateChange(VMState state);
	}
	public final enum VMState
	{
		IDLE, PREPARING, RUNNING, FINISHING, INTERRUPTED, OP_LIMIT_REACHED, BUSY_INTERNAL;
	}
	public VM()
	{
		log("VM Created");
	}
	public boolean run()
	{
		if (!state.equals(VMState.IDLE)) return false;
		if (code.size() <= 0) return false;
		state = VMState.PREPARING;
		for (final OnVMStateChange c: callbacks)
		{
			c.onVMStateChange(state);
		}
		log("VM Started executing");
		state = VMState.RUNNING;
		for (final OnVMStateChange c: callbacks)
		{
			c.onVMStateChange(state);
		}
		cpos = 1;
		return continueRun();
	}
	public boolean continueRun()
	{
		int i = -1;
		while (i < VMOptions.OP_LIMIT)
		{
			i++;
			runLine();
			if (state.equals(VMState.INTERRUPTED))
			{
				for (final OnVMStateChange c: callbacks)
				{
					c.onVMStateChange(state);
				}
				log("VM Interrupted");
				state = VMState.IDLE;
				return false;
			}
			if (stopFlag)
			{
				state = VMState.FINISHING;
				for (final OnVMStateChange c: callbacks)
				{
					c.onVMStateChange(state);
				}
				stopFlag = false;
				log("VM Ran OK");
				state = VMState.IDLE;
				return true;
			}
		}
		state = VMState.OP_LIMIT_REACHED;
		for (final OnVMStateChange c: callbacks)
		{
			c.onVMStateChange(state);
		}
		log("VM OP count > " + VMOptions.OP_LIMIT);
		return false;
	}
	public VM reset()
	{
		cpos = -1;
		register = 0;
		stopFlag = false;
		return this;
	}
	public VM load(String str, boolean log)
	{
		VMState prev = state;
		state = VMState.BUSY_INTERNAL;
		code.clear();
		code.append(0, null);
		boolean containsStop = false;
		{
			int curl = 1;
			final String[] sa = str.split("\n");
			for (int i=0; i < sa.length; i++)
			{
				final String s = sa[i].split(";")[0];
				final String[] tokens = s.trim().split(" ");
				String op = tokens[0];
				int shift = 1;
				if (Character.isDigit(op.charAt(0)))
				{
					if ((op.equals("1") || op.equals("0")) && (tokens.length <= 1))
					{

					}
					else
					{
						curl = Integer.valueOf(op);
						op = tokens[1];
						shift = 2;
					}
				}
				final String[] args = new String[tokens.length - shift];
				for (int j=shift; j < tokens.length; j++)
				{
					args[j - shift] = tokens[j];
				}
				if (OP.isApplicableOp(op))
				{
					code.append(curl, new OP(this, op, args));
				}
				else if (IfOP.isApplicableOp(op))
				{
					code.append(curl, new IfOP(this, op, args));
				}
				else if (RegSetOp.isApplicableOp(op))
				{
					code.append(curl, new RegSetOp(this, op, args));
				}
				else if (StopOP.isApplicableOp(op))
				{
					code.append(curl, new StopOP(this, op, args));
					containsStop = true;
				}
				else
				{
					PostLinter.current().post("[error] Unknown OP: " + op);
				}
				curl++;
			}
		}
		code.sort();
		if (log) log("VM Loaded code");
		if (!containsStop)
		{
			PostLinter.current().post("[error] No stop statement.");
		}
		state = prev;
		for (final OnVMStateChange c: callbacks)
		{
			c.onVMStateChange(state);
		}
		return this;
	}
	public void runLine()
	{
		{
			final int cs = code.size();
			if ((cs <= cpos) || (cpos < 0))
			{
				PostLinter.current().post("[error] No such line defined");
				interruptVM();
				return;
			}
		}
		final OP cur = code.get(cpos);
		if (!cur.isCorrect())
		{
			log("OP:" + cpos + ": incorrect");
			PostLinter.current().post("[error] OP incorrect");
			interruptVM();
			return;
		}
		final int cprev = cpos;
		cur.run();
		if (!debugMode)
		{
			StringBuilder ll = new StringBuilder();
			ll.append((cprev) + ": ");
			for (int i=cline - 10; i <= cline + 8; i++)
			{
				ll.append(line.get(i) ?"1": "0");
			}
			log(ll.toString());
		}
	}
	public synchronized OP getCurrentOP()
	{
		{
			final int cs = code.size();
			if ((cs <= cpos) || (cpos < 0))
			{
				return null;
			}
		}
		final OP cur = code.get(cpos);
		return cur;
	}
	public synchronized void stopVM()
	{
		stopFlag = true;
	}
	public synchronized void interruptVM()
	{
		state = VM.VMState.INTERRUPTED;
	}
	public synchronized void log(final String str)
	{
		log.append(str);
		log.append("\n");
		latestLine = str;
	}
	public synchronized String writeCode()
	{
		final StringBuilder b = new StringBuilder();
		boolean writeLines = false;
		int line = 1;
		int linesSkipped = 0;
		final int linesSkippedThreshold = 3;
		if (code.get(0) != null)
		{
			line = 0;
			writeLines = true;
		}
		code.sort();
		for (int i=0; i < code.size(); i++)
		{
			int key = code.keyAt(i);
			OP op = code.valueAt(i);
			if ((op == null) && (key == 0))
			{
				line++;
				continue;
			}
			linesSkipped = key - line - 1;
			if (linesSkipped > linesSkippedThreshold)
			{
				writeLines = true;
			}
			line += linesSkipped + 1;
			if (writeLines)
			{
				line = key;
				b.append(key);
				b.append(" ");
			}
			else
			{
				while (linesSkipped > 0)
				{
					b.append("\n");
					linesSkipped--;
				}
			}
			b.append((op != null) ?op.toString(): "null");
			b.append("\n");
		}
		return b.toString().trim().intern();
	}
}
