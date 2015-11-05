package io.github.stalker2010.post;

import com.github.paolorotolo.appintro.*;
import android.os.*;
import android.graphics.*;

public class PostIntroActivity extends AppIntro2
{

	@Override
	public void init(Bundle p1)
	{
		addSlide(AppIntroFragment.newInstance(
					 "Post Machine",
					 "Incredible new Post Machine realization for both PCs and phones.\nAndroid, Windows, Linux, OS X and so on!",
					 R.drawable.devices,
					 Color.parseColor("#4CAF50")
				 ));
		addSlide(AppIntroFragment.newInstance(
					 "Incredible fast",
					 "Two engines: interpreter and fast Ahead-of-Time compilator!",
					 R.drawable.devices,
					 Color.parseColor("#4CAF50")
				 ));
	}

	@Override
	public void onDonePressed()
	{
		finish();
	}

}
