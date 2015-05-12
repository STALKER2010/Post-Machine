package io.github.stalker2010.post;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
				if (savedInstanceState == null) {
						setFragment(new EditorFragment());
				}
    }

		public static interface OnBackPressedListener {
				boolean onBackPressed(final MainActivity ma);
		}

		private MenuItem reset;
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
				getMenuInflater().inflate(R.menu.lines, menu);
				reset = menu.findItem(R.id.reset);
				return true;
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
				if (item.getItemId() == R.id.reset) {
						VM.current.line.clear();
						VM.current.cline = 0;
						if (fragment instanceof LineFragment) {
								final LineFragment lf = (LineFragment) fragment;
								lf.load();
						}
						return true;
				}
				return super.onOptionsItemSelected(item);
		}

		@Override
		public void onBackPressed() {
				if (fragment != null) {
						if (fragment instanceof OnBackPressedListener) {
								if (((OnBackPressedListener) fragment).onBackPressed(this)) {
										return;
								}
						}
				}
				if (VM.current.state.equals(VM.VMState.RUNNING)) {
						VM.current.interruptVM();
						return;
				}
				super.onBackPressed();
		}


		private volatile Fragment fragment = null;
		public synchronized void setFragment(final Fragment f) {
				final FragmentTransaction t = getSupportFragmentManager().beginTransaction();
				t.replace(R.id.container, f);
				t.commit();
				fragment = f;
				if (reset != null) {
						if (f instanceof LineFragment) {
								reset.setVisible(true);
						} else {
								reset.setVisible(false);
						}
				}
		}

}
