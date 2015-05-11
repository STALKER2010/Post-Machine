package io.github.stalker2010.post;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import java.lang.ref.*;
import io.github.stalker2010.post.VM.*;

public class RunFragment extends Fragment implements MainActivity.OnBackPressedListener, VM.OnVMStateChange {
		@Override
		public void onVMStateChange(VM.VMState state) {
				if (!state.equals(VM.VMState.IDLE)) {
						ui.start();
				}
				ui.run();
		}

		private TextView lg;
		private ProgressBar pb;
		private static final int updInterval = 500;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
				View v = inflater.inflate(R.layout.log, container, false);
				lg = (TextView) v.findViewById(R.id.tv_log);
				pb = (ProgressBar) v.findViewById(R.id.pb_log);
				pb.setVisibility(View.VISIBLE);
				ui.start();
				return v;
		}
		@Override
		public void onResume() {
				super.onResume();
				ui.run();
		}
		@Override
		public void onStop() {
				super.onStop();
				h.removeCallbacks(ui);
		}

		@Override
		public void onDestroy() {
				super.onDestroy();
				PostApplication.getRefWatcher(getActivity()).watch(this);
				h.removeCallbacksAndMessages(null);
		}

		@Override
		public boolean onBackPressed(MainActivity ma) {
				ma.setFragment(new EditorFragment());
				return true;
		}

		private final Handler h = new Handler();
		private final LogUpdater ui = new LogUpdater(this);
		private static final class LogUpdater implements Runnable {
				public WeakReference<RunFragment> fragment;
				public final Thread real = new Thread(RealRunner.instance);
				public LogUpdater(final RunFragment f) {
						this.fragment = new WeakReference<RunFragment>(f);
				}
				private CharSequence prev = null;
				public void start() {
						if (!real.isAlive()) {
								real.start();
						}
				}
				@Override
				public void run() {
						final RunFragment f = fragment.get();
						if (f == null) {
								return;
						}
						f.h.removeCallbacks(this);
						final CharSequence s = VM.current.log.toString();
						if (!s.equals(prev)) {
								f.lg.setText(s);
								prev = s;
						}
						if (real.isAlive()) {
								f.h.postDelayed(this, updInterval);
						} else {
								f.pb.setVisibility(View.GONE);
						}
				}
				public static final class RealRunner implements Runnable {
						public static final RealRunner instance = new RealRunner();

						@Override
						public void run() {
								if (VM.current.state.equals(VM.VMState.IDLE)) {
										VM.current.run();
								} else {
										VM.current.continueRun();
								}
						}
				}
		}
}
