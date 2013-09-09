/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.jumplife.movieinfo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class MovieInfoAppliccation extends Application {
	
	private final String preferenceName = "Preference";
	public static SharedPreferences shIO;
	
	@Override
	public void onCreate() {		
		super.onCreate();
		
		SharePreferenceInit();
		initImageLoader(getApplicationContext());
	}
	private void SharePreferenceInit() {
		shIO = this.getSharedPreferences(preferenceName, 0);
	}
	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
	        .tasksProcessingOrder(QueueProcessingType.LIFO)
			.threadPoolSize(2)
			.threadPriority(Thread.NORM_PRIORITY)
			.memoryCache(new WeakMemoryCache())
			.discCacheFileNameGenerator(new Md5FileNameGenerator())
			.build();
		ImageLoader.getInstance().init(config);
	}
}