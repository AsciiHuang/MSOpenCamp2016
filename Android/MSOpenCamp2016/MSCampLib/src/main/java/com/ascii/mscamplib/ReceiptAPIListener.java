package com.ascii.mscamplib;

import java.util.ArrayList;

public interface ReceiptAPIListener {
	void onSuccess(ArrayList<Receipt> receipts);
	void onError();
}
