package io.github.stalker2010.post;

import android.support.v7.preference.*;
import android.os.*;

public class PrefsFragment extends PreferenceFragmentCompat implements MainActivity.OnBackPressedListener
{

	@Override
	public boolean onBackPressed(MainActivity ma)
	{
		ma.setFragment(new EditorFragment());
		return true;
	}

	@Override
	public void onCreatePreferences(Bundle p1, String p2)
	{
		addPreferencesFromResource(R.xml.preferences);
	}
	
}
