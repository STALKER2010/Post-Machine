package io.github.stalker2010.post;

import android.app.Application;
import android.content.Context;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class PostApplication extends Application {

		public static RefWatcher getRefWatcher(Context context) {
				PostApplication application = (PostApplication) context.getApplicationContext();
				return application.refWatcher;
		}

		private RefWatcher refWatcher;

		@Override public void onCreate() {
				super.onCreate();
				refWatcher = installLeakCanary();
		}

		protected RefWatcher installLeakCanary() {
				return LeakCanary.install(this);
		}
}
