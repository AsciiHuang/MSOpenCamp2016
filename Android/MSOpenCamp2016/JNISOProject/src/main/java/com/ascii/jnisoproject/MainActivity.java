package com.ascii.jnisoproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

	private TextView lblMessage;
	private final static String RECEIPT_API_URL = "http://ascii.azurewebsites.net/ReceiptCheckSignature.aspx";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		lblMessage = (TextView) findViewById(R.id.lbl_message);

		ReceiptAPIHelper apiHelper = new ReceiptAPIHelper();
		String url = String.format("%s?%s", RECEIPT_API_URL, apiHelper.getParams(this));
		StringRequest request = new StringRequest(url, responseSuccessHandler, responseErrorHandler);
		((MSCamp) getApplication()).stringRequest(request);
	}

	private Response.Listener<String> responseSuccessHandler = new Response.Listener<String>() {
		@Override
		public void onResponse(String response) {
			try {
				JSONObject responseObject = new JSONObject(response);
				JSONArray receipts = responseObject.getJSONArray("receipts");
				StringBuilder stringBuilder = new StringBuilder();
				for (int i = 0; i < receipts.length(); ++i) {
					JSONObject receipt = receipts.getJSONObject(i);
					String item = receipt.optString("item");
					stringBuilder.append(item + "\n");
					String value = receipt.optString("value");
					stringBuilder.append(value + "\n\n");
				}
				lblMessage.setText(stringBuilder.toString());
			} catch (Exception e) {

			}
		}
	};

	private Response.ErrorListener responseErrorHandler = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			lblMessage.setText(error.toString());
		}
	};
}
