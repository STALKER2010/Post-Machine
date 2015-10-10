package io.github.stalker2010.post;

import android.app.*;
import android.os.*;
import android.view.*;
import io.github.stalker2010.post.vm.*;
import android.widget.*;
import android.database.*;
import static io.github.stalker2010.post.PostApplication.*;

public class MainActivity extends Activity
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
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getActionBar().setListNavigationCallbacks(new DocSpinnerAdapter(), new ActionBar.OnNavigationListener() {
				@Override
				public boolean onNavigationItemSelected(int p1, long p2)
				{
					current(vms.get(p1));
					return false;
				}
			});
		if (savedInstanceState == null)
		{
			setFragment(new EditorFragment());
		}
    }

	public static interface OnBackPressedListener
	{
		boolean onBackPressed(final MainActivity ma);
	}

	private MenuItem reset;
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.lines, menu);
		reset = menu.findItem(R.id.reset);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == R.id.reset)
		{
			current().line.clear();
			current().cline = 0;
			if (fragment instanceof LineFragment)
			{
				final LineFragment lf = (LineFragment) fragment;
				lf.load();
			}
			return true;
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
		if (current().state.equals(VM.VMState.RUNNING))
		{
			current().interruptVM();
			return;
		}
		super.onBackPressed();
	}


	private volatile Fragment fragment = null;
	public synchronized void setFragment(final Fragment f)
	{
		final FragmentTransaction t = getFragmentManager().beginTransaction();
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
