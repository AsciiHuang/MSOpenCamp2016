package com.ascii.mscamplib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class SafeReceiptAPI {

	private boolean signValid = false;
	private RequestQueue queue;
	private ReceiptAPIListener listener;
	private final static String RECEIPT_API_URL = "http://ascii.azurewebsites.net/ReceiptChecksumWithTimeLimited.aspx";

	public SafeReceiptAPI(Context context) {
		queue = Volley.newRequestQueue(context);
		signValid = checkSignature(context);
	}

	public void getReceiptData(final ReceiptAPIListener listener) {
		this.listener = listener;
		if (signValid) {
			long now = System.currentTimeMillis() / 1000;
			String checksum = getChecksum(now);
			String url = String.format("%s?now=%s&checksum=%s", RECEIPT_API_URL, now, checksum);
			StringRequest request = new StringRequest(url, responseSuccessHandler, responseErrorHandler);
			queue.add(request);
		} else {
			listener.onError();
		}
	}

	private boolean checkSignature(Context context) {
		int signHash = 0;
		try {
			PackageManager manager = context.getPackageManager();
			String packageName = context.getPackageName();
			PackageInfo info = manager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
			Signature sign = info.signatures[0];
			signHash = sign.hashCode();
		} catch (Exception e) {
		}
		return signHash == 786706950; // Change to your signature
	}

	private String getChecksum(long now) {
		if (signValid) {
			return getMd5Hash(String.format("%dAscii_Receipt_Open_Camp", now));
		} else {
			return getMd5Hash(String.format("%dErrorString", now));
		}
	}

	private String getMd5Hash(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			String md5 = number.toString(16);

			while (md5.length() < 32) {
				md5 = "0" + md5;
			}

			return md5;
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	private Response.Listener<String> responseSuccessHandler = new Response.Listener<String>() {
		@Override
		public void onResponse(String response) {
			try {
				ArrayList<Receipt> resList = new ArrayList<>();
				JSONObject responseObject = new JSONObject(response);
				JSONArray receipts = responseObject.getJSONArray("receipts");
				for (int i = 0; i < receipts.length(); ++i) {
					JSONObject receipt = receipts.getJSONObject(i);
					String item = receipt.optString("item");
					String value = receipt.optString("value");
					resList.add(new Receipt(item, value));
				}
				listener.onSuccess(resList);
			} catch (Exception e) {
				listener.onError();
			}
		}
	};

	private Response.ErrorListener responseErrorHandler = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			listener.onError();
		}
	};
}
