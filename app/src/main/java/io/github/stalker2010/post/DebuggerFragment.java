package io.github.stalker2010.post;

import android.graphics.*;
import android.os.*;
import android.support.v4.app.*;
import android.text.*;
import android.text.style.*;
import android.view.*;
import android.widget.*;
import io.github.stalker2010.post.vm.*;
import java.lang.ref.*;

import static io.github.stalker2010.post.PostApplication.*;

public class DebuggerFragment extends BaseFragment implements MainActivity.OnBackPressedListener, VM.OnVMStateChange, View.OnClickListener
{

	@Override
	public int viewID()
	{
		return R.layout.debugger;
	}
	
	@Override
	public void onVMStateChange(VM.VMState state)
	{
		ui.run();
	}

	private TextView vm_state, code, clines, reg, log;
	public View reg_row;
	private static final int updInterval = 5000;

	@Override
	public void initView()
	{
		super.initView();
		vm_state = byId(R.id.dbg_vmstate);
		code = byId(R.id.dbgCode);
		clines = byId(R.id.dbgCodeLines);
		reg = byId(R.id.dbg_reg);
		log = byId(R.id.dbg_log);
		reg_row = byId(R.id.dbg_reg_row);
		final Button btn_reset = byId(R.id.btn_dbg_reset);
		final Button btn_step = byId(R.id.btn_dbg_step);
		final Button btn_line = byId(R.id.btn_dbg_line);
		final Button btn_lint = byId(R.id.btn_dbg_lint);
		btn_reset.setOnClickListener(this);
		btn_step.setOnClickListener(this);
		btn_line.setOnClickListener(this);
		btn_lint.setOnClickListener(this);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		ui.run();
	}
	@Override
	public void onStop()
	{
		super.onStop();
		h.removeCallbacks(ui);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		h.removeCallbacksAndMessages(null);
	}

	@Override
	public boolean onBackPressed(MainActivity ma)
	{
		ma.setFragment(new EditorFragment());
		return true;
	}

	@Override
	public void onClick(View p1)
	{
		final int d = p1.getId();
		if (d != View.NO_ID)
		{
			final MainActivity ma = (MainActivity) getActivity();
			switch (d)
			{
				case R.id.btn_dbg_reset: {
						if (current().vm.state.equals(VM.VMState.IDLE))
						{
							current().linter.messages.clear();
							current().vm.reset().load(PostApplication.code, true);
							current().vm.debugMode = true;
							PostApplication.code = current().vm.writeCode();
							current().vm.cpos = 1;
							current().vm.log("VM Started executing");
							current().vm.state = VM.VMState.RUNNING;
						}
						break;
					}
				case R.id.btn_dbg_step: {
						if (current().vm.debugMode)
						{
							current().vm.runLine();
							if (current().vm.state.equals(VM.VMState.INTERRUPTED))
							{
								for (final VM.OnVMStateChange c: current().vm.callbacks)
								{
									c.onVMStateChange(current().vm.state);
								}
								current().vm.debugMode = false;
								current().vm.log("VM Interrupted");
								current().vm.state = VM.VMState.IDLE;
							}
							if (current().vm.stopFlag)
							{
								current().vm.state = VM.VMState.FINISHING;
								for (final VM.OnVMStateChange c: current().vm.callbacks)
								{
									c.onVMStateChange(current().vm.state);
								}
								current().vm.stopFlag = false;
								current().vm.cpos = -1;
								current().vm.debugMode = false;
								current().vm.log("VM Ran OK");
								current().vm.state = VM.VMState.IDLE;
							}
						}
						break;
					}
				case R.id.btn_dbg_line: {
						LineFragment f = new LineFragment();
						f.fromDebugger = true;
						ma.setFragment(f);
						break;
					}
				case R.id.btn_dbg_lint: {
						LinterFragment f = new LinterFragment();
						f.fromDebugger = true;
						ma.setFragment(f);
						break;
					}
			}
		}
		ui.run();
	}

	private final Handler h = new Handler();
	private final LogUpdater ui = new LogUpdater(this);
	private static final class LogUpdater implements Runnable
	{
		public WeakReference<DebuggerFragment> fragment;
		public LogUpdater(final DebuggerFragment f)
		{
			this.fragment = new WeakReference<DebuggerFragment>(f);
		}
		SpannableString ssb = new SpannableString("");
		int lintMessageCount = 0;
		@Override
		public void run()
		{
			final DebuggerFragment f = fragment.get();
			if (f == null)
			{
				return;
			}
			f.h.removeCallbacks(this);
			final String sc = PostApplication.code;
			final StringBuilder toLog = new StringBuilder();
			ssb = new SpannableString(sc);
			{
				final OP op = current().vm.getCurrentOP();
				if (op != null)
				{
					int color = Color.argb(210, 51, 181, 229);
					if (op instanceof IfOP)
					{
						final IfOP iop = (IfOP) op;
						int res = iop.conformsRegIF(iop.args[0]);
						if (res == -1)
						{
							final boolean isRight = current().vm.line.get(current().vm.cline);
							res = isRight ? 1: 0;
						}
						if (res == 0)
						{
							color = Color.argb(230, 255, 0, 0);
						}
						else if (res == 1)
						{
							color = Color.argb(220, 0, 255, 0);
						}
					}
					highlightLine(current().vm.cpos, color);
				}
				toLog.append(current().vm.latestLine);
				final int newLintMsgCount = current().linter.messages.size();
				if (lintMessageCount < newLintMsgCount)
				{
					toLog.append("(+" + (newLintMsgCount - lintMessageCount) + " Lint messages)");
					lintMessageCount = newLintMsgCount;
				}
				else if (lintMessageCount > newLintMsgCount)
				{
					lintMessageCount = newLintMsgCount;
				}
			}
			f.code.setText(ssb);
			updateLineNumbers();
			f.reg.setText(Integer.toString(current().vm.register));
			f.vm_state.setText(current().vm.state.name());
			f.log.setText(toLog.toString());
			f.h.postDelayed(this, updInterval);
		}
		public void highlightLine(int line, int color)
		{
			ForegroundColorSpan fcs = new ForegroundColorSpan(color);
			BackgroundColorSpan bcs = new BackgroundColorSpan(Color.argb(Color.alpha(color) - 160, Color.red(color), Color.green(color), Color.blue(color)));
			int start = -1, end = -1, curLine = 1;
			if (line == 1) start = 0;
			for (int i=0; i < ssb.length(); i++)
			{
				final char c = ssb.charAt(i);
				if (c == '\n')
				{
					curLine++;
					if (start == -1)
					{
						if (curLine == line)
						{
							start = i + 1;
						}
					}
					else
					{
						end = i;
						break;
					}
				}
			}
			if (start != -1)
			{
				if (end == -1)
				{
					end = ssb.length();
				}
				ssb.setSpan(fcs, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				ssb.setSpan(bcs, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		public void updateLineNumbers()
		{
			final String t = fragment.get().code.getText().toString();
			int lineCount = 1;
			for (int i=0; i < t.length(); i++)
			{
				final char c = t.charAt(i);
				if (c == '\n') lineCount++;
			}
			final StringBuilder b = new StringBuilder();
			for (int i=1; i <= lineCount; i++)
			{
				b.append(i);
				if (i != lineCount)
					b.append("\n");
			}
			fragment.get().clines.setText(b);
		}
	}
}
