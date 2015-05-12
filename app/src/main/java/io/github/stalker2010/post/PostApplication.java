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
		public static String code = "0\n>\n? 29 4\n>\n? 4\n>\n? 4\n<\n<\n0\n>\n? 11\n>\n? 13\n1\n<\n? 16\n<\n? 18\n1\n<\n? 10\n<\n? 23\n<\n? 23\n>\n> 1\n>\n0\n>\n? 31\n1\n>\n? 38 36\n<\n? 29 36\n!";

		@Override public void onCreate() {
				super.onCreate();
				refWatcher = installLeakCanary();
		}

		protected RefWatcher installLeakCanary() {
				if (true) {
						return RefWatcher.DISABLED;
				} else {
						return LeakCanary.install(this);
				}
		}
}
