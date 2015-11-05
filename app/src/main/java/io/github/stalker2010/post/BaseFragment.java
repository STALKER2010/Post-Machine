package io.github.stalker2010.post;

import android.support.v4.app.*;
import android.view.*;
import android.os.*;

public abstract class BaseFragment extends Fragment
{
	View root;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		root = inflater.inflate(viewID(), container, false);
		initView();
		return root;
	}
	public abstract int viewID();
	public void initView() {
		
	}
	public <T extends View> T byId(int id) {
		return (T) root.findViewById(id);
	}
}
