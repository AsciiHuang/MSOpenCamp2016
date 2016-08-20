package com.ascii.jnisoproject;

import android.content.Context;

public class ReceiptAPIHelper {

	static {
		System.loadLibrary("ReceiptAPIHelper");
	}

	public native String getParams(Context context);
}
