package io.github.stalker2010.post;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import io.github.stalker2010.post.vm.PostLinter;

public class LineFragment extends Fragment implements MainActivity.OnBackPressedListener, View.OnClickListener, View.OnLongClickListener {

		public volatile boolean fromDebugger = false;
		public LineFragment() {

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
				View v = inflater.inflate(R.layout.line, container, false);
				for (int i=-12; i <= 12; i++) {
						final View bv = v.findViewById(diff_id(i));
						bv.setOnClickListener(this);
						bv.setOnLongClickListener(this);
				}
				return v;
		}
		@Override
		public void onResume() {
				super.onResume();
				load();
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

		private void set(int diff, boolean state) {
				try {
						VM.current.line.append(VM.current.cline + diff, state);
				} catch (IndexOutOfBoundsException e) {
				}
		}
		private boolean get(int diff) {
				return VM.current.line.get(VM.current.cline + diff, false);
		}
		public void load() {
				for (int i=-12; i <= 12; i++) {
						setCell(diff_id(i), get(i));
				}
				setCellCaret(diff_id(0));
		}
		private static final SparseIntArray id2layout = new SparseIntArray(25);
		private static final SparseIntArray layout2id = new SparseIntArray(25);
		static {
				id2layout.append(-12, R.id.lb_m12);
				id2layout.append(-11, R.id.lb_m11);
				id2layout.append(-10, R.id.lb_m10);
				id2layout.append(-9, R.id.lb_m9);
				id2layout.append(-8, R.id.lb_m8);
				id2layout.append(-7, R.id.lb_m7);
				id2layout.append(-6, R.id.lb_m6);
				id2layout.append(-5, R.id.lb_m5);
				id2layout.append(-4, R.id.lb_m4);
				id2layout.append(-3, R.id.lb_m3);
				id2layout.append(-2, R.id.lb_m2);
				id2layout.append(-1, R.id.lb_m1);
				id2layout.append(0, R.id.lb_0);
				id2layout.append(1, R.id.lb_p1);
				id2layout.append(2, R.id.lb_p2);
				id2layout.append(3, R.id.lb_p3);
				id2layout.append(4, R.id.lb_p4);
				id2layout.append(5, R.id.lb_p5);
				id2layout.append(6, R.id.lb_p6);
				id2layout.append(7, R.id.lb_p7);
				id2layout.append(8, R.id.lb_p8);
				id2layout.append(9, R.id.lb_p9);
				id2layout.append(10, R.id.lb_p10);
				id2layout.append(11, R.id.lb_p11);
				id2layout.append(12, R.id.lb_p12);
				for (int i=-12; i <= 12; i++) {
						layout2id.append(id2layout.get(i), i);
				}
		}
		private int id_diff(final int id) {
				return layout2id.get(id, 0);
		}
		private int diff_id(final int diff) {
				return id2layout.get(diff, 0);
		}
		private void setCell(int id, boolean state) {
				View v = getView().findViewById(id);
				if ((v != null) && (v instanceof Button)) {
						Button b = (Button) v;
						b.setText(state ? "1": "0");
				}
		}
		private boolean getCell(int id) {
				View v = getView().findViewById(id);
				if ((v != null) && (v instanceof Button)) {
						Button b = (Button) v;
						return b.getText().toString().equals("1");
				}
				return false;
		}
		private void setCellCaret(int id) {
				View v = getView().findViewById(id);
				if (v != null) {
						v.setBackgroundColor(Color.LTGRAY);
				}
		}

		@Override
		public void onClick(View p1) {
				int d = p1.getId();
				if (d != View.NO_ID) {
						int df = id_diff(d);
						setCell(d, !getCell(d));
						if (get(df) != getCell(d)) {
								set(df, getCell(d));
						}
				}
		}

		@Override
		public boolean onLongClick(View p1) {
				final int d = p1.getId();
				if (d != View.NO_ID) {
						VM.current.cline += id_diff(d);
						load();
						return true;
				} else {
						return false;
				}
		}

		@Override
		public void onDestroy() {
				super.onDestroy();
				PostApplication.getRefWatcher(getActivity()).watch(this);
		}
}
