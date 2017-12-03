/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jiubang.ggheart.launcher;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;

/**
 * Specific {@link AppWidgetHost} that creates our
 * {@link LauncherWidgetHostView} which correctly captures all long-press
 * events. This ensures that users can always pick up and move widgets.
 */
public class LauncherWidgetHost extends AppWidgetHost {

	public LauncherWidgetHost(Context context, int hostId) {
		super(context, hostId);
	}

	@Override
	protected AppWidgetHostView onCreateView(Context context, int appWidgetId,
			AppWidgetProviderInfo appWidget) {
		LauncherWidgetHostView view = new LauncherWidgetHostView(context);
		return view;
	}
}
