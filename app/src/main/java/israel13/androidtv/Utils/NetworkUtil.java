package israel13.androidtv.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * NetworkUtil checks available network
 * 
 */
public class NetworkUtil {

	/**
	 * Check network connection
	 * 
	 * @param context
	 * @return
	 */
	public static int TYPE_WIFI = 1;
	public static int TYPE_MOBILE = 2;
	public static int TYPE_NOT_CONNECTED = 0;
	public static String LOG_TAG = "Network check";

	
	public static int getConnectivityStatus(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (null != activeNetwork) {
			if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
				return TYPE_WIFI;
			
			if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
				return TYPE_MOBILE;
		} 
		return TYPE_NOT_CONNECTED;
	}
	
	public static String getConnectivityStatusString(Context context) {
		int conn = NetworkUtil.getConnectivityStatus(context);
		String status = null;
		if (conn == NetworkUtil.TYPE_WIFI) {
			status = "Wifi enabled";
		} else if (conn == NetworkUtil.TYPE_MOBILE) {
			status = "Mobile data enabled";
		} else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
			status = "no connection";
		}
		return status;
	}
	public static boolean checkNetworkAvailable(Context context) {
		NetworkInfo i;
		try {
			ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			i = conMgr.getActiveNetworkInfo();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (i == null) {
			return false;
		}
		if (!i.isConnected()) {
			return false;
		}
		if (!i.isAvailable()) {
			return false;
		}
		return true;
	}
	public static boolean hasActiveInternetConnection(Context context) {
		if (checkNetworkAvailable(context)) {
			try {
				return true;
//				HttpURLConnection urlc = (HttpURLConnection) (new URL("https://www.israel.tv").openConnection());
//				urlc.setRequestProperty("User-Agent", "Test");
//				urlc.setRequestProperty("Connection", "close");
//				urlc.setConnectTimeout(1500);
//				urlc.connect();
//				return (urlc.getResponseCode() == 200);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Error checking internet connection");
			}
		} else {
			Log.d(LOG_TAG, "No network available!");
		}
		return false;
	}

	public static boolean isServerAlive(Context context, String strURL) {
		if (checkNetworkAvailable(context)) {
			try {
				HttpURLConnection urlc = (HttpURLConnection) (new URL(strURL).openConnection());
				urlc.setRequestProperty("User-Agent", "Test");
				urlc.setRequestProperty("Connection", "close");
				urlc.setConnectTimeout(1000);
				urlc.connect();
				return (urlc.getResponseCode() == 200 || urlc.getResponseCode() == 403 || urlc.getResponseCode() == 401 || urlc.getResponseCode() ==402);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Error checking internet connection");
			}
		} else {
			Log.d(LOG_TAG, "No network available!");
		}
		return false;
	}

	public static boolean isServer401(Context context, String strURL) {
		if (checkNetworkAvailable(context)) {
			try {
				HttpURLConnection urlc = (HttpURLConnection) (new URL(strURL).openConnection());
				urlc.setRequestProperty("User-Agent", "Test");
				urlc.setRequestProperty("Connection", "close");
				urlc.setConnectTimeout(1000);
				urlc.connect();
				return (urlc.getResponseCode() == 401);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Error checking internet connection");
			}
		} else {
			Log.d(LOG_TAG, "No network available!");
		}
		return false;
	}
	/**
	 * Enable Strict mode
	 */
	@SuppressLint("NewApi")
	public static void enableStrictMode() {
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
	}
}
