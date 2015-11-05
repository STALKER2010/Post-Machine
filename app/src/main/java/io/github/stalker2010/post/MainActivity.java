package io.github.stalker2010.post;

import android.database.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import io.github.stalker2010.post.vm.*;

import static io.github.stalker2010.post.PostApplication.*;
import android.content.*;
import android.preference.*;

public class MainActivity extends AppCompatActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
		{
			Document def = new Document();
			vms.add(def);
			current(def);
		}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(new DocSpinnerAdapter(), new ActionBar.OnNavigationListener() {
				@Override
				public boolean onNavigationItemSelected(int p1, long p2)
				{
					current(vms.get(p1));
					return false;
				}
			});
		setFragment(new EditorFragment());
		if (savedInstanceState == null) {
			SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
			if (!p.getBoolean("intro_shown", false)) {
				Intent i = new Intent(this, PostIntroActivity.class);
				startActivity(i);
			}
		}
    }

	public static interface OnBackPressedListener
	{
		boolean onBackPressed(final MainActivity ma);
	}

	private MenuItem reset, prefs, open, save;
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu, menu);
		reset = menu.findItem(R.id.menu_reset);
		reset.setVisible(false);
		prefs = menu.findItem(R.id.menu_prefs);
		open = menu.findItem(R.id.menu_open);
		save = menu.findItem(R.id.menu_save);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int itemId = item.getItemId();
		if (itemId == R.id.menu_reset)
		{
			current().vm.line.clear();
			current().vm.cline = 0;
			if (fragment instanceof LineFragment)
			{
				final LineFragment lf = (LineFragment) fragment;
				lf.load();
			}
			return true;
		}
		if (itemId == R.id.menu_prefs)
		{
			setFragment(new PrefsFragment());
			return true;
		}
		if (itemId == R.id.menu_open) {
			current().vm.stopVM();
			setFragment(new OpenFragment());
			return true;
		}
		if (itemId == R.id.menu_save) {
			if (current().file != null) {
				if (!current().saveToFile()) {
					Toast.makeText(this, "Failed to save to file", Toast.LENGTH_LONG).show();
				}
			} else {
				DialogFragment fr = new SaveDialogFragment();
				fr.show(getSupportFragmentManager(), "save_dialog");
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed()
	{
		if (fragment != null)
		{
			if (fragment instanceof OnBackPressedListener)
			{
				if (((OnBackPressedListener) fragment).onBackPressed(this))
				{
					return;
				}
			}
		}
		if (current().vm.state.equals(VM.VMState.RUNNING))
		{
			current().vm.interruptVM();
			return;
		}
		super.onBackPressed();
	}


	private volatile Fragment fragment = null;
	public synchronized void setFragment(final Fragment f)
	{
		final FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.container, f);
		t.commit();
		fragment = f;
		if (reset != null)
		{
			if (f instanceof LineFragment)
			{
				reset.setVisible(true);
			}
			else
			{
				reset.setVisible(false);
			}
		}
		if (prefs != null)
		{
			if (f instanceof EditorFragment)
			{
				prefs.setVisible(true);
			}
			else
			{
				prefs.setVisible(false);
			}
		}
		if (open != null)
		{
			if (f instanceof EditorFragment)
			{
				open.setVisible(true);
			}
			else
			{
				open.setVisible(false);
			}
		}
		if (save != null)
		{
			if (f instanceof EditorFragment)
			{
				save.setVisible(true);
			}
			else
			{
				save.setVisible(false);
			}
		}
	}

	public class DocSpinnerAdapter implements SpinnerAdapter
	{

		@Override
		public void registerDataSetObserver(DataSetObserver p1)
		{
			// TODO: Implement this method
//			throw new RuntimeException("registerDataSetObserver");
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver p1)
		{
			// TODO: Implement this method
//			throw new RuntimeException("unregisterDataSetObserver");
		}

		@Override
		public int getCount()
		{
			return PostApplication.vms.size();
		}

		@Override
		public Object getItem(int p1)
		{
			return PostApplication.vms.get(p1);
		}

		@Override
		public long getItemId(int p1)
		{
			return p1;
		}

		@Override
		public boolean hasStableIds()
		{
			return false;
		}

		private LayoutInflater inflater = null;
		@Override
		public View getView(int p1, View p2, ViewGroup p3)
		{
			if (p2 == null)
			{
				if (inflater == null)
				{
					inflater = LayoutInflater.from(MainActivity.this);
				}
				p2 = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, p3, false);
			}
			TextView tv = (TextView) p2.findViewById(android.R.id.text1);
			Document d = (Document) getItem(p1);
			tv.setText(d.name);
			return p2;
		}

		@Override
		public int getItemViewType(int p1)
		{
			// TODO: Implement this method
			return 0;
		}

		@Override
		public int getViewTypeCount()
		{
			return 1;
		}

		@Override
		public boolean isEmpty()
		{
			return PostApplication.vms.isEmpty();
		}

		@Override
		public View getDropDownView(int p1, View p2, ViewGroup p3)
		{
			if (p2 == null)
			{
				if (inflater == null)
				{
					inflater = LayoutInflater.from(MainActivity.this);
				}
				p2 = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, p3, false);
			}
			TextView tv = (TextView) p2.findViewById(android.R.id.text1);
			Document d = (Document) getItem(p1);
			tv.setText(d.name);
			return p2;
		}
	}

}
