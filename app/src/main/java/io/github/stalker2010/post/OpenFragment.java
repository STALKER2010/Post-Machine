package io.github.stalker2010.post;

import android.app.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;

import static io.github.stalker2010.post.PostApplication.*;

public class OpenFragment extends BaseFragment implements AdapterView.OnItemClickListener, MainActivity.OnBackPressedListener
{
	ListView list;
	ArrayAdapter<String> files;

	@Override
	public int viewID()
	{
		return R.layout.open;
	}

	@Override
	public void initView()
	{
		super.initView();
		list = byId(R.id.openListView);
		File[] fdata = StorageUtils.get().list();
		List<String> data = new ArrayList<String>(fdata.length);
		for (File f: fdata)
		{
			data.add(f.getName());
		}
		Collections.sort(data);
		files = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, data);
		list.setAdapter(files);
		list.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		String fname = files.getItem(p3);
		{
			Document def = new Document();
			def.setFile(StorageUtils.get().fileByName(fname));
			if (!def.loadFromFile()) {
				Toast.makeText(getActivity(), "Failed to load from file", Toast.LENGTH_LONG).show();
			}
			vms.add(def);
			current(def);
		}
		Activity a = getActivity();
		if (a instanceof MainActivity) {
			MainActivity ma = (MainActivity) a;
			ma.setFragment(new EditorFragment());
		}
	}

	@Override
	public boolean onBackPressed(MainActivity ma)
	{
		ma.setFragment(new EditorFragment());
		return true;
	}

}
