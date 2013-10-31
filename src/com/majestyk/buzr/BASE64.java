package com.majestyk.buzr;

import java.io.UnsupportedEncodingException;

import android.util.Base64;
import android.util.Log;

public class BASE64 {

	// Sending side
	public static String encodeBase64(String text) {
		String base64 = new String();
//		try {
//			byte[] data = text.getBytes("UTF-8");
//			base64 = Base64.encodeToString(data, Base64.DEFAULT);
//			Log.e("BASE64 encoder", text + " => " + base64);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
			base64 = text;
//		}
		return base64;
	}

	// Receiving side
	public static String decodeBase64(String base64) {
		String text = new String();
//		try {
//			byte[] data = Base64.decode(base64, Base64.DEFAULT);
//			text = new String(data, "UTF-8");
//			Log.e("BASE64 decoder", base64 + " => " + text);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
			text = base64;
//		}
		return text;
	}

}
