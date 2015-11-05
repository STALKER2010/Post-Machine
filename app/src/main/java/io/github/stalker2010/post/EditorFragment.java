package io.github.stalker2010.post;

import android.content.*;
import android.os.*;
import android.text.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import com.melnykov.fab.*;
import io.github.stalker2010.post.vm.*;
import java.lang.ref.*;

import static io.github.stalker2010.post.PostApplication.*;

public class EditorFragment extends BaseFragment implements View.OnClickListener
{

	@Override
	public int viewID()
	{
		return R.layout.editor;
	}
	
	public EditText code;
	public TextView clines;
	volatile boolean changedCode = false;
	volatile long codeLen = 0;

	@Override
	public void initView()
	{
		super.initView();
		code = byId(R.id.code);
		clines = byId(R.id.codeLines);
		uRunnable.fragment = new WeakReference<EditorFragment>(this);
		final LinearLayout row1 = byId(R.id.row1);
		final LinearLayout row2 = byId(R.id.row2);
		final LinearLayout row3 = byId(R.id.row3);
		final LinearLayout row4 = byId(R.id.row4);
		final Button btn_space = byId(R.id.space);
		final Button btn_nl = byId(R.id.nl);
		final Button btn_run = byId(R.id.btn_run);
		final Button btn_debug = byId(R.id.btn_debug);
		final Button btn_line = byId(R.id.btn_line);
		final Button btn_lint = byId(R.id.btn_lint);
		final Button btn_del = byId(R.id.del);
		for (int i=0; i < row1.getChildCount(); i++)
		{
			View cv = row1.getChildAt(i);
			if (cv instanceof Button)
			{
				Button b = (Button) cv;
				b.setOnClickListener(this);
			}
		}
		for (int i=0; i < row2.getChildCount(); i++)
		{
			View cv = row2.getChildAt(i);
			if (cv instanceof Button)
			{
				Button b = (Button) cv;
				b.setOnClickListener(this);
			}
		}
		for (int i=0; i < row3.getChildCount(); i++)
		{
			View cv = row3.getChildAt(i);
			if (cv instanceof Button)
			{
				Button b = (Button) cv;
				b.setOnClickListener(this);
			}
		}
		for (int i=0; i < row4.getChildCount(); i++)
		{
			View cv = row4.getChildAt(i);
			if (cv instanceof Button)
			{
				Button b = (Button) cv;
				b.setOnClickListener(this);
			}
		}
		btn_space.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1)
				{
					insertCode(" ");
				}
			});
		btn_nl.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1)
				{
					insertCode("\n");
				}
			});
		btn_del.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1)
				{
					deleteChar();
				}
			});
		btn_run.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1)
				{
					if (changedCode && current().vm.debugMode)
					{
						current().vm.state = VM.VMState.FINISHING;
						for (final VM.OnVMStateChange c: current().vm.callbacks)
						{
							c.onVMStateChange(current().vm.state);
						}
						current().vm.log("VM Ran OK");
						current().vm.state = VM.VMState.IDLE;
						current().linter.messages.clear();
						current().vm.reset().load(code.getText().toString(), true);
						code.setText(current().vm.writeCode());
					}
					else if (!current().vm.debugMode)
					{
						current().linter.messages.clear();
						current().vm.reset().load(code.getText().toString(), true);
						code.setText(current().vm.writeCode());
					}
					current().vm.debugMode = false;
					((MainActivity) getActivity()).setFragment(new RunFragment());
				}
			});
		btn_debug.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1)
				{
					((MainActivity) getActivity()).setFragment(new DebuggerFragment());
				}
			});
		btn_line.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1)
				{
					((MainActivity) getActivity()).setFragment(new LineFragment());
				}
			});
		btn_lint.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1)
				{
					((MainActivity) getActivity()).setFragment(new LinterFragment());
				}
			});
		code.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					uHandler.removeCallbacks(uRunnable);
				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
				{}

				@Override
				public void afterTextChanged(Editable p1)
				{
					uHandler.postDelayed(uRunnable, 100);
				}
			});
		code.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					v.onTouchEvent(event);
					InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					if (imm != null)
					{
						imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
						imm = null;
					}                
					return true;
				}
			});
		FloatingActionButton b = byId(R.id.fab);
		final ObservableScrollView esv = byId(R.id.editorScrollView);
		b.attachToScrollView(esv);
	}
	
	@Override
	public void onClick(View p1)
	{
		if (p1 instanceof Button)
		{
			Button b = (Button) p1;
			String bc = b.getText().toString();
			insertCode(bc);
		}
	}

	@Override
	public void onPause()
	{
		super.onPause();
		PostApplication.code = code.getText().toString();
	}


	@Override
	public void onResume()
	{
		super.onResume();
		code.setText(PostApplication.code);
		uHandler.postDelayed(uRunnable, 10);
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		{
			final LinearLayout row1 = (LinearLayout) getView().findViewById(R.id.row1);
			final LinearLayout row2 = (LinearLayout) getView().findViewById(R.id.row2);
			final LinearLayout row3 = (LinearLayout) getView().findViewById(R.id.row3);
			final LinearLayout row4 = (LinearLayout) getView().findViewById(R.id.row4);
			for (int i=0; i < row1.getChildCount(); i++)
			{
				View cv = row1.getChildAt(i);
				if (cv instanceof Button)
				{
					Button b = (Button) cv;
					b.setOnClickListener(null);
				}
			}
			for (int i=0; i < row2.getChildCount(); i++)
			{
				View cv = row2.getChildAt(i);
				if (cv instanceof Button)
				{
					Button b = (Button) cv;
					b.setOnClickListener(null);
				}
			}
			for (int i=0; i < row3.getChildCount(); i++)
			{
				View cv = row3.getChildAt(i);
				if (cv instanceof Button)
				{
					Button b = (Button) cv;
					b.setOnClickListener(null);
				}
			}
			for (int i=0; i < row4.getChildCount(); i++)
			{
				View cv = row4.getChildAt(i);
				if (cv instanceof Button)
				{
					Button b = (Button) cv;
					b.setOnClickListener(null);
				}
			}
			row1.removeAllViews();
			row2.removeAllViews();
			row3.removeAllViews();
			row4.removeAllViews();
		}
		System.gc();
		System.gc();
		System.gc();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		uHandler.removeCallbacksAndMessages(null);

		System.gc();
		System.gc();
		System.gc();
	}

	private void insertCode(String bc)
	{
		changedCode = true;
		uHandler.removeCallbacks(uRunnable);
		int start = code.getSelectionStart();
		int end = code.getSelectionEnd();
		code.getText().replace(Math.min(start, end), Math.max(start, end), bc, 0, bc.length());
		code.setSelection(code.getSelectionEnd());
		code.clearFocus();
		uHandler.postDelayed(uRunnable, 100);
	}
	private void deleteChar()
	{
		changedCode = true;
		uHandler.removeCallbacks(uRunnable);
		int start = code.getSelectionStart();
		int end = code.getSelectionEnd();
		code.getText().delete(Math.max(Math.min(start, end) - 1, 0), Math.max(start, end));
		code.setSelection(code.getSelectionEnd());
		code.clearFocus();
		uHandler.postDelayed(uRunnable, 100);
	}

	private final Handler uHandler = new Handler();
	private final UpdLinesRunnable uRunnable = new UpdLinesRunnable();
	static final class UpdLinesRunnable implements Runnable
	{
		WeakReference<EditorFragment> fragment = new WeakReference<EditorFragment>(null);
		@Override
		public void run()
		{
			final EditorFragment f = fragment.get();
			if (f != null)
			{
				String t = f.code.getText().toString().intern();
				int clen = t.length();
				if (Math.abs(clen - f.codeLen) > 5)
				{
					current().vm.load(t, false);
					t = current().vm.writeCode();
					clen = t.length();
				}
				int lineCount = 1;
				for (int i=0; i < clen; i++)
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
				f.clines.setText(b);
				PostApplication.code = f.code.getText().toString();
			}
		}
	};

}
