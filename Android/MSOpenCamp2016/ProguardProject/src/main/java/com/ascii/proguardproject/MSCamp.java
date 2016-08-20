package com.ascii.proguardproject;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MSCamp extends Application {

	private RequestQueue queue;

	@Override
	public void onCreate() {
		super.onCreate();
		queue = Volley.newRequestQueue(this);
	}

	public void stringRequest(StringRequest request) {
		queue.add(request);
	}
}
