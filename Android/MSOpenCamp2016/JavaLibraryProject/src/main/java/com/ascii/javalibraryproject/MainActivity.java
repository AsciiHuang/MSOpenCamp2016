package com.ascii.javalibraryproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ascii.mscamplib.Receipt;
import com.ascii.mscamplib.ReceiptAPI;
import com.ascii.mscamplib.ReceiptAPIListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	private TextView lblMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		lblMessage = (TextView) findViewById(R.id.lbl_message);

		ReceiptAPI api = new ReceiptAPI(this);
		api.getReceiptData(apiListener);
	}

	private ReceiptAPIListener apiListener = new ReceiptAPIListener() {
		@Override
		public void onSuccess(ArrayList<Receipt> receipts) {
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < receipts.size(); ++i) {
				String item = receipts.get(i).item;
				stringBuilder.append(item + "\n");
				String value = receipts.get(i).value;
				stringBuilder.append(value + "\n\n");
			}
			lblMessage.setText(stringBuilder.toString());
		}

		@Override
		public void onError() {
		}
	};
}
