package io.github.stalker2010.post;
import android.support.v4.app.*;
import android.view.*;
import android.os.*;
import android.widget.*;
import java.lang.ref.*;
import io.github.stalker2010.post.vm.*;

public class LinterFragment extends Fragment implements VM.OnVMStateChange, MainActivity.OnBackPressedListener {

		@Override
		public void onVMStateChange(VM.VMState state) {
				adapter.notifyDataSetChanged();
		}

		public LinterFragment() {}
		ListView list;
		LintAdapter adapter = new LintAdapter(this);
		public volatile boolean fromDebugger = false;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
				View v = inflater.inflate(R.layout.linter, container, false);
				list = (ListView) v.findViewById(R.id.linter_list);
				list.setAdapter(adapter);
				VM.current.callbacks.add(this);
				return v;
		}

		@Override
		public void onDestroy() {
				super.onDestroy();
				PostApplication.getRefWatcher(getActivity()).watch(this);
		}
		@Override
		public boolean onBackPressed(MainActivity ma) {
				if (fromDebugger) {
						ma.setFragment(new DebuggerFragment());
				} else {
						ma.setFragment(new EditorFragment());
				}
				return true;
		}
		public static final class LintAdapter extends BaseAdapter {
				private WeakReference<LinterFragment> fragment;
				public LintAdapter(final LinterFragment fragment) {
						this.fragment = new WeakReference<LinterFragment>(fragment);
				}

				@Override
				public int getCount() {
						return PostLinter.lint.messages.size();
				}

				@Override
				public PostLinter.LintMessage getItem(int p1) {
						return PostLinter.lint.messages.get(p1);
				}

				@Override
				public long getItemId(int p1) {
						return p1;
				}

				@Override
				public View getView(int p1, View v, ViewGroup p3) {
						if (v == null) {
								if (fragment.get() == null) {
										throw new IllegalStateException("No LintFragment");
								} else {
										final LayoutInflater li = LayoutInflater.from(fragment.get().getActivity());
										v = li.inflate(android.R.layout.simple_list_item_2, p3, false);
								}
						}
						final TextView text1 = (TextView) v.findViewById(android.R.id.text1);
						final TextView text2 = (TextView) v.findViewById(android.R.id.text2);
						final PostLinter.LintMessage m = getItem(p1);
						if (m.line == -1) {
								text1.setText("No line");
						} else {
								text1.setText("Line #" + m.line);
						}
						text2.setText(m.text);
						return v;
				}
		}
}
