package io.github.stalker2010.post;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.text.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import io.github.stalker2010.post.vm.*;

public class EditorFragment extends Fragment implements View.OnClickListener {
		public EditText code;
		public TextView clines;
		volatile boolean changedCode = false;
		private Button lastPressed = null;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
				View v = inflater.inflate(R.layout.editor, container, false);
				{
						code = (EditText) v.findViewById(R.id.code);
						clines = (TextView) v.findViewById(R.id.codeLines);
						final LinearLayout row1 = (LinearLayout) v.findViewById(R.id.row1);
						final LinearLayout row2 = (LinearLayout) v.findViewById(R.id.row2);
						final LinearLayout row3 = (LinearLayout) v.findViewById(R.id.row3);
						final LinearLayout row4 = (LinearLayout) v.findViewById(R.id.row4);
						final Button btn_space = (Button) v.findViewById(R.id.space);
						final Button btn_nl = (Button) v.findViewById(R.id.nl);
						final Button btn_run = (Button) v.findViewById(R.id.btn_run);
						final Button btn_debug = (Button) v.findViewById(R.id.btn_debug);
						final Button btn_line = (Button) v.findViewById(R.id.btn_line);
						final Button btn_lint = (Button) v.findViewById(R.id.btn_lint);
						final Button btn_del = (Button) v.findViewById(R.id.del);
						for (int i=0; i < row1.getChildCount(); i++) {
								View cv = row1.getChildAt(i);
								if (cv instanceof Button) {
										Button b = (Button) cv;
										b.setOnClickListener(this);
								}
						}
						for (int i=0; i < row2.getChildCount(); i++) {
								View cv = row2.getChildAt(i);
								if (cv instanceof Button) {
										Button b = (Button) cv;
										b.setOnClickListener(this);
								}
						}
						for (int i=0; i < row3.getChildCount(); i++) {
								View cv = row3.getChildAt(i);
								if (cv instanceof Button) {
										Button b = (Button) cv;
										b.setOnClickListener(this);
								}
						}
						for (int i=0; i < row4.getChildCount(); i++) {
								View cv = row4.getChildAt(i);
								if (cv instanceof Button) {
										Button b = (Button) cv;
										b.setOnClickListener(this);
								}
						}
						btn_space.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View p1) {
												insertCode(" ");
										}
								});
						btn_nl.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View p1) {
												insertCode("\n");
										}
								});
						btn_del.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View p1) {
												deleteChar();
										}
								});
						btn_run.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View p1) {
												if (changedCode && VM.current.debugMode) {
														VM.current.state = VM.VMState.FINISHING;
														for (final VM.OnVMStateChange c: VM.current.callbacks) {
																c.onVMStateChange(VM.current.state);
														}
														VM.current.stopFlag = false;
														VM.current.cpos = -1;
														VM.current.log("VM Ran OK");
														VM.current.state = VM.VMState.IDLE;
														VM.current.load(code.getText().toString());
												} else if (!VM.current.debugMode) {
														VM.current.load(code.getText().toString());
												}
												VM.current.debugMode = false;
												((MainActivity) getActivity()).setFragment(new RunFragment());
										}
								});
						btn_debug.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View p1) {
												((MainActivity) getActivity()).setFragment(new DebuggerFragment());
										}
								});
						btn_line.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View p1) {
												((MainActivity) getActivity()).setFragment(new LineFragment());
										}
								});
						btn_lint.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View p1) {
												((MainActivity) getActivity()).setFragment(new LinterFragment());
										}
								});
						code.addTextChangedListener(new TextWatcher() {
										@Override
										public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
												uHandler.removeCallbacks(uRunnable);
										}

										@Override
										public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {}

										@Override
										public void afterTextChanged(Editable p1) {
												uHandler.postDelayed(uRunnable, 100);
										}
								});
						code.setOnTouchListener(new View.OnTouchListener() {
										@Override
										public boolean onTouch(View v, MotionEvent event) {
												v.onTouchEvent(event);
												InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
												if (imm != null) {
														imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
														imm = null;
												}                
												return true;
										}
								});
//						code.setInputType(InputType.TYPE_NULL);
//						if (android.os.Build.VERSION.SDK_INT >= 11) {
//								code.setRawInputType(InputType.TYPE_CLASS_TEXT);
//						}
				}
				return v;
		}
		@Override
		public void onClick(View p1) {
				if (p1 instanceof Button) {
						Button b = (Button) p1;
						lastPressed = b;
						String bc = b.getText().toString();
						insertCode(bc);
				}
		}

		@Override
		public void onPause() {
				super.onPause();
				((MainActivity) getActivity()).code = code.getText().toString();
		}


		@Override
		public void onResume() {
				super.onResume();
				code.setText(((MainActivity)getActivity()).code);
				uHandler.postDelayed(uRunnable, 10);
		}

		@Override
		public void onDestroy() {
				super.onDestroy();
				PostApplication.getRefWatcher(getActivity()).watch(this);
				uHandler.removeCallbacksAndMessages(null);
		}

		private void insertCode(String bc) {
				changedCode = true;
				uHandler.removeCallbacks(uRunnable);
				int start = code.getSelectionStart();
				int end = code.getSelectionEnd();
				code.getText().replace(Math.min(start, end), Math.max(start, end), bc, 0, bc.length());
				code.setSelection(code.getSelectionEnd());
				code.clearFocus();
				if (lastPressed != null) {
						lastPressed.requestFocus();
				}
				uHandler.postDelayed(uRunnable, 100);
		}
		private void deleteChar() {
				changedCode = true;
				uHandler.removeCallbacks(uRunnable);
				int start = code.getSelectionStart();
				int end = code.getSelectionEnd();
				code.getText().delete(Math.max(Math.min(start, end) - 1, 0), Math.max(start, end));
				code.setSelection(code.getSelectionEnd());
				code.clearFocus();
				if (lastPressed != null) {
						lastPressed.requestFocus();
				}
				uHandler.postDelayed(uRunnable, 100);
		}

		private final Handler uHandler = new Handler();
		private final Runnable uRunnable = new Runnable() {
				@Override
				public void run() {
						final String t = code.getText().toString();
						int lineCount = 1;
						for (int i=0; i < t.length(); i++) {
								final char c = t.charAt(i);
								if (c == '\n') lineCount++;
						}
						final StringBuilder b = new StringBuilder();
						for (int i=1; i <= lineCount; i++) {
								b.append(i);
								if (i != lineCount)
										b.append("\n");
						}
						clines.setText(b);
				}
		};

}
