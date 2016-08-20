package com.ascii.mscamplib;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReceiptAPI {

	private RequestQueue queue;
	private ReceiptAPIListener listener;
	private final static String RECEIPT_API_URL = "http://ascii.azurewebsites.net/ReceiptBase.aspx";

	public ReceiptAPI(Context context) {
		queue = Volley.newRequestQueue(context);
	}

	public void getReceiptData(ReceiptAPIListener listener) {
		this.listener = listener;
		StringRequest request = new StringRequest(RECEIPT_API_URL, responseSuccessHandler, responseErrorHandler);
		queue.add(request);
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
