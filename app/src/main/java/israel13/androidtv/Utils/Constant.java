package israel13.androidtv.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import israel13.androidtv.R;
import israel13.androidtv.Service.ResetCacheService;

/**
 * Created by puspak on 17/05/17.
 */
public class Constant {
	public static String BASE_URL = "https://api.israel.tv/";
	public static String BASE_URL_IMAGE = "http://israel.tv/";

	public static String CACHE_EXO_DEFAULT_NAME = "exo-media-cache";
	public static String DEVICE_TYPE = "androidtv";

	public static final int TAB_PROFILE = 1;
	public static final int TAB_SEARCH = 2;
	public static final int TAB_VOD = 3;
	public static final int TAB_RECORD = 4;
	public static final int TAB_LIVECHANNEL= 5;
	public static final int TAB_FAVOURITE = 6;

	public static int RETURN_CODE_REGISTRATION = 101;
	public static int RETURN_CODE_GET_VERIFY = 102;
	public static int RETURN_CODE_GET_VERIFY_RESEND = 103;

	public static int SUCCESS = 0;
	public static int UNKNOWN_ERROR = 1234567890;

	public static int RESPONSE_DATACOUNT = 32;

	public static long NETWORK_CHECK_TIME = 15 * 1000; // every 15 seconds
	public static long BUFFERING_TIME_OUT = 60 * 1000; // every 60 seconds
	public static long RECONNECT_NEXT_TIME_OUT = 3 * 1000; // every 3 seconds

	public static int getChannelItemWidth(Activity a){
		DisplayMetrics metrics = new DisplayMetrics();
		a.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return 230 + (int)(20 * metrics.density);
	}
	public static boolean isInternetAvailable() {
		try {
			final InetAddress address = InetAddress.getByName("192.168.14.95");
			return !address.equals("");
		} catch (UnknownHostException e) {
			// Log error
		}
		return false;
	}
	public static  void setEXOPlaybackBufferMS(SharedPreferences ref, int exoplaybackbuffer) {
		SharedPreferences.Editor edit = ref.edit();
		edit.putInt("exoplaybackbuffer", exoplaybackbuffer);
		edit.commit();
	}

	public static int getEXOPlaybackBufferMS(SharedPreferences ref) {
		if (ref != null) {
			int playbackbuffer = ref.getInt("exoplaybackbuffer", DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS);
			return playbackbuffer;
		}
		return DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS;
	}

	public static  void setEXOMinBufferMS(SharedPreferences ref, int exominbuffer) {
		SharedPreferences.Editor edit = ref.edit();
		edit.putInt("exominbuffer", exominbuffer);
		edit.commit();
	}

	public static int getEXOMinBufferMS(SharedPreferences ref) {
		if (ref != null) {
			int exominbuffer = ref.getInt("exominbuffer", DefaultLoadControl.DEFAULT_MIN_BUFFER_MS);
			return exominbuffer;
		}
		return DefaultLoadControl.DEFAULT_MIN_BUFFER_MS;
	}

	public static String dayByGivenDate(String day) {

		if (day.equalsIgnoreCase("1")) {
			return "ראשון";
		} else if (day.equalsIgnoreCase("2")) {
			return "שני";
		} else if (day.equalsIgnoreCase("3")) {
			return "שלישי";
		} else if (day.equalsIgnoreCase("4")) {
			return "רביעי";
		} else if (day.equalsIgnoreCase("5")) {
			return "חמישי";
		} else if (day.equalsIgnoreCase("6")) {
			return "שישי";
		} else {
			return "שבת";
		}
	}

	public static String hour_min_format(int totalSecs) {

		int hours, minutes, seconds;

		hours = totalSecs / 3600;
		minutes = (totalSecs % 3600) / 60;
		seconds = totalSecs % 60;

		return String.format("%02d:%02d:%02d", hours, minutes, seconds);

	}

	public static int parseInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static float parseFloat(String str) {
		try {
			return Float.parseFloat(str);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static void ShowErrorToast(Context mContext) {
		if (mContext != null) {
			Toast.makeText(mContext, "Sorry Some Error Occured", Toast.LENGTH_SHORT).show();
		}
	}

	public static void ShowErrorToastText(Context mContext, String text) {
		Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
	}

	public static boolean hasFreeDiskSpace(Context context, boolean bShowToast) {
		long availableFreeSize = ResetCacheService.getCacheAvailableSize(context);
		if (availableFreeSize < CustomCacheDataSourceFactory.DEFAULT_MAX_CACHE_SIZE) {
			if (bShowToast) {
				Constant.ShowErrorToastText(context,
						String.format(context.getResources().getString(R.string.no_enough_disk_space_message), CustomCacheDataSourceFactory.DEFAULT_MAX_CACHE_SIZE/1024/1024));
			}
			return false;
		}
		return true;
	}

	public static int Getminutes(int totalSecs) {

		int hours, minutes, seconds;

		hours = totalSecs / 3600;
		minutes = (totalSecs % 3600) / 60;
		seconds = totalSecs % 60;

		return minutes;
	}

	public static String getTimeByAdding1(int seconds, String myTime) {

		try {

			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
			Date d = null;

			d = df.parse(myTime);

			Calendar cal = Calendar.getInstance();
			cal.setTime(d);

			int minutes, hours;
			if (seconds > 3600) {


				hours = seconds / 3600;
				minutes = (seconds % 3600) / 60;
				cal.add(Calendar.HOUR, hours);
				cal.add(Calendar.MINUTE, minutes);

			} else {
				minutes = (seconds % 3600) / 60;
				cal.add(Calendar.MINUTE, minutes);

			}


			return df.format(cal.getTime());

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getTimeByAdding(int duration, String timeslot) {
		//Puspak
		//flag==-1 means previousdate and flag==1 means tomorrow's date
		int minutes = 0;

		String neededdate = null;
		String times = timeslot;
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		try {
			Date date = dateFormat.parse(times);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);

			minutes = duration / 60;
			calendar.add(Calendar.MINUTE, minutes);
			neededdate = dateFormat.format(calendar.getTime());


		} catch (ParseException e) {
			e.printStackTrace();
		}

		return neededdate;
	}

	public static String getDateTimeByAdding(int duration, String timeslot) {
		//Puspak
		//flag==-1 means previousdate and flag==1 means tomorrow's date
		int minutes = 0;

		String neededdate = null;
		String times = timeslot;
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
		try {
			Date date = dateFormat.parse(times);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.SECOND, duration);
			neededdate = dateFormat.format(calendar.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return neededdate;
	}

	public static long time_diff_in_millisec(String start_time, String End_time) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
		Date startDate, endDate;
		try {
			startDate = simpleDateFormat.parse(start_time);
			endDate = simpleDateFormat.parse(End_time);

			long diffInMs = endDate.getTime() - startDate.getTime();

			long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);

			return diffInSec;
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static String getUnixDate(String rdatetime) {

		String no_info = "מידע אינו זמין";
		try {
			Date date = new Date(Long.parseLong(rdatetime) * 1000L); // *1000 is to convert seconds to milliseconds
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); // the format of your date
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+5.30")); // give a timezone reference for formating (see comment at the bottom

			return sdf.format(date);
		} catch (Exception e) {
			return no_info;
		}

	}

	public static String getUnixDate_record_date(String rdatetime) {
		Date date = new Date(Long.parseLong(rdatetime) * 1000L); // *1000 is to convert seconds to milliseconds
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // the format of your date
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+5.30")); // give a timezone reference for formating (see comment at the bottom

		return sdf.format(date);
	}

	public static boolean setListViewHeightBasedOnItems(ListView listView, int number_of_display) {

		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter != null) {

			int numberOfItems = number_of_display;

			// Get total height of all items.
			// Calculate the height with multiplier as first item height

			View item = listAdapter.getView(0, null, listView);
			item.measure(0, 0);
			int totalItemsHeight = item.getMeasuredHeight() * numberOfItems;
//			for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
//				View item = listAdapter.getView(itemPos, null, listView);
//				item.measure(0, 0);
//				totalItemsHeight += item.getMeasuredHeight();
//			}

			// Get total height of all item dividers.
			int totalDividersHeight = listView.getDividerHeight() *
					(numberOfItems - 1);

			// Set list height.
			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = (totalItemsHeight + totalDividersHeight + listView.getDividerHeight() / 2) + 10;
			listView.setLayoutParams(params);
			listView.requestLayout();

			return true;

		} else {
			return false;
		}

	}

	public static int findDaysDiff(long unixStartTime, long unixEndTime) {

		Date purchasedDate = new Date();
//multiply the timestampt with 1000 as java expects the time in milliseconds
		purchasedDate.setTime((long) unixStartTime * 1000);

		Date currentDate = new Date();
		currentDate.setTime((long) unixEndTime * 1000);

//To calculate the days difference between two dates
		// int diffInDays = (int)( (currentDate.getTime() - purchasedDate.getTime())
		int diffInDays = (int) ((currentDate.getTime() - purchasedDate.getTime())
				/ (1000 * 60 * 60 * 24));

		return diffInDays;
	}

	public static String findDaysDiffByDate(String start_date, String enddate) {

		String dayDifference = "";

		try {
			Date date1;
			Date date2;

			SimpleDateFormat dates = new SimpleDateFormat("dd-MM-yyyy HH:mm");

			date1 = dates.parse(start_date);
			date2 = dates.parse(enddate);

			//Comparing dates
			long difference = Math.abs(date1.getTime() - date2.getTime());
			long differenceDates = difference / (24 * 60 * 60 * 1000);

			dayDifference = Long.toString(differenceDates);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dayDifference;
	}

	public static String getUnixDateMyprofile(String rdatetime) {
		Date date = new Date(Long.parseLong(rdatetime) * 1000L); // *1000 is to convert seconds to milliseconds
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy"); // the format of your date
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem")); // give a timezone reference for formating (see comment at the bottom

		return sdf.format(date);
	}

	public static String getUnixDateMyprofile_new_format(String rdatetime) {
		Date date = new Date(Long.parseLong(rdatetime) * 1000L); // *1000 is to convert seconds to milliseconds
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm"); // the format of your date
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem")); // give a timezone reference for formating (see comment at the bottom

		return sdf.format(date);
	}

	public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate) {

		Date parsed = null;
		String outputDate = "";

		SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
		SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

		try {
			parsed = df_input.parse(inputDate);
			outputDate = df_output.format(parsed);

		} catch (ParseException e) {
			//LoginException(TAG, "ParseException - dateFormat");
			System.out.println("----exception----" + e);
		}

		return outputDate;

	}

	public static void showPopup_Error_expire_package(final Activity context) {

		DisplayMetrics displaymetrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;

		int popupwidth = width - 100;
		int popupHeight = height - 250;

		// Inflate the popup_layout.xml
		LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.totalview_popup_package_expired);
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = layoutInflater.inflate(R.layout.popup_layout_package_expired, viewGroup);

		// Creating the PopupWindow
		final PopupWindow popup = new PopupWindow(context);
		popup.setContentView(layout);
		popup.setWidth(900);
//		popup.setHeight(460);
		popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
		popup.setFocusable(true);

		TextView ok_popup_expired = (TextView) layout.findViewById(R.id.ok_popup_expired);

		ok_popup_expired.requestFocus();
		popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				popup.dismiss();
				context.finish();
			}
		});

		// Clear the default translucent background
		popup.setBackgroundDrawable(new BitmapDrawable());

		popup.showAtLocation(layout, Gravity.CENTER, 0, 0);

		ok_popup_expired.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popup.dismiss();
				context.finish();
			}
		});
	}

	public static void showPopup_Error_basic_package(final Activity context) {

		DisplayMetrics displaymetrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;

		int popupwidth = width - 100;
		int popupHeight = height - 250;

		// Inflate the popup_layout.xml
		LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.linear_layout_basic_package_expired);
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = layoutInflater.inflate(R.layout.popup_layout_package_basic, viewGroup);

		// Creating the PopupWindow
		final PopupWindow popup = new PopupWindow(context);
		popup.setContentView(layout);
	  /*  popup.setWidth(popupwidth);
        popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);*/
		popup.setWidth(1000);
		popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
		popup.setFocusable(true);

		TextView ok_basic_package = (TextView) layout.findViewById(R.id.ok_basic_package);

		ok_basic_package.requestFocus();
		popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				popup.dismiss();
				context.finish();
			}
		});


		// Clear the default translucent background
		popup.setBackgroundDrawable(new BitmapDrawable());


		popup.showAtLocation(layout, Gravity.CENTER, 0, 0);


		ok_basic_package.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popup.dismiss();
				context.finish();
			}
		});
	}

	public static String getDeviceUUID(Context context) {
		SharedPreferences logindetails = context.getSharedPreferences("logindetails", Context.MODE_PRIVATE);
		String deviceID = logindetails.getString("deviceID", "");
		if (!deviceID.isEmpty())
			return deviceID;

		deviceID = getMacAddr();

		// In case when the app can't get the deviceID or MacAddress
		if (deviceID.trim().isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
			deviceID = sdf.format(new Date());

			// Add User_Agent value to above.
			try {
				String userAgent = System.getProperty("http.agent");
				deviceID = deviceID + userAgent;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		deviceID = MD5Encryption(deviceID);
		SharedPreferences.Editor edit = logindetails.edit();
		edit.putString("deviceID", deviceID);
		edit.commit();
		return deviceID;
	}

	private static String getMacAddr() {
		String eth0 = null;
		String wlan0 = null;
		try {
			List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface nif : all) {
				if (!nif.getName().equalsIgnoreCase("wlan0")
						&& !nif.getName().equalsIgnoreCase("eth0")) continue;

				byte[] macBytes = nif.getHardwareAddress();
				if (macBytes == null) {
					return "";
				}

				StringBuilder res1 = new StringBuilder();
				for (byte b : macBytes) {
					res1.append(String.format("%02X:",b));
				}

				if (res1.length() > 0) {
					res1.deleteCharAt(res1.length() - 1);
				}

				if (nif.getName().equalsIgnoreCase("wlan0")) {
					wlan0 = res1.toString();
				}

				if (nif.getName().equalsIgnoreCase("eth0")) {
					eth0 = res1.toString();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (eth0 != null)
			return eth0;
		if (wlan0 != null)
			return wlan0;
		return "";
	}

	public static boolean isDeviceUniqueAvailable() {
		String macAddress = getMacAddr();
		if (macAddress == null || macAddress.isEmpty())
			return false;
		return true;
	}

	private static String MD5Encryption(String deviceId) {
		final String MD5 = "MD5";
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance(MD5);
			digest.update(deviceId.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuilder hexString = new StringBuilder();
			for (byte aMessageDigest : messageDigest) {
				String h = Integer.toHexString(0xFF & aMessageDigest);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deviceId;
	}

	public static boolean check_time_as_schedule(Date current_date) {
		boolean flag = false;
		Date curTime1 = current_date;

		try {


			String string1 = "23:59";
			Date time1 = new SimpleDateFormat("HH:mm").parse(string1);
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTime(time1);
			calendar1.add(Calendar.DATE, -1);

			String string2 = "06:00";
			Date time2 = new SimpleDateFormat("HH:mm").parse(string2);
			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTime(time2);
			//calendar2.add(Calendar.DATE, 1);

			if (curTime1.after(calendar1.getTime()) && curTime1.before(calendar2.getTime())) {
				//checkes whether the current time is between 14:49:00 and 20:11:13.
				flag = true;
				System.out.println("----------true");
			} else flag = false;
		} catch (ParseException e) {
			System.out.println("----exception----" + e);
		}
		return flag;
	}

	public static void showPopup_Error_freeze_package(final Activity context, String date) {

		DisplayMetrics displaymetrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;

		//  int popupwidth = width - 100;
		int popupwidth = width / 2;
		int popupHeight = height / 2;

		// Inflate the popup_layout.xml
		LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.totalview_popup_package_freeze);
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = layoutInflater.inflate(R.layout.popup_layout_package_freeze, viewGroup);

		// Creating the PopupWindow
		final PopupWindow popup = new PopupWindow(context);
		popup.setContentView(layout);
     /*   popup.setWidth(popupwidth);
        popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);*/
		popup.setWidth(900);
//		popup.setHeight(460);
		popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
		popup.setFocusable(true);

		TextView ok_popup_freeze = (TextView) layout.findViewById(R.id.ok_popup_freeze);
		TextView freezedate_package_freeze = (TextView) layout.findViewById(R.id.freezedate_package_freeze);

		ok_popup_freeze.requestFocus();

		freezedate_package_freeze.setText("כבקשתך המנוי שלך מוקפא עד תאריך: " + date);

		popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				popup.dismiss();
				context.finish();
			}
		});


		// Clear the default translucent background
		popup.setBackgroundDrawable(new BitmapDrawable());

		popup.showAtLocation(layout, Gravity.CENTER, 0, 0);

		ok_popup_freeze.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popup.dismiss();
				context.finish();
			}
		});
	}

	public static void showPopup_Error_suspend_package(final Activity context) {

		DisplayMetrics displaymetrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;

		int popupwidth = width - 100;
		int popupHeight = height - 250;

		// Inflate the popup_layout.xml
		LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.totalview_popup_package_suspend);
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = layoutInflater.inflate(R.layout.popup_layout_package_suspend, viewGroup);

		// Creating the PopupWindow
		final PopupWindow popup = new PopupWindow(context);
		popup.setContentView(layout);
      /*  popup.setWidth(popupwidth);
        popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);*/
		popup.setWidth(900);
//		popup.setHeight(460);
		popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
		popup.setFocusable(true);

		TextView ok_basic_package = (TextView) layout.findViewById(R.id.ok_popup_suspend);

		ok_basic_package.requestFocus();
		popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				popup.dismiss();
				context.finish();
			}
		});


		// Clear the default translucent background
		popup.setBackgroundDrawable(new BitmapDrawable());


		popup.showAtLocation(layout, Gravity.CENTER, 0, 0);


		ok_basic_package.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popup.dismiss();
				context.finish();
			}
		});
	}

	public static void showPopup_Error_vpn_message(Activity context) {

		DisplayMetrics displaymetrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;

		int popupwidth = width - 100;
		int popupHeight = height - 250;

		// Inflate the popup_layout.xml
		LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.totalview_popup_vpn_message);
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = layoutInflater.inflate(R.layout.popup_layout_package_vps_message, viewGroup);

		// Creating the PopupWindow
		final PopupWindow popup = new PopupWindow(context);
		popup.setContentView(layout);
		popup.setWidth(popupwidth);
		popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
		popup.setFocusable(true);

		TextView ok_popup_vpn_message = (TextView) layout.findViewById(R.id.ok_popup_vpn_message);

		popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				popup.dismiss();

			}
		});

		// Clear the default translucent background
		popup.setBackgroundDrawable(new BitmapDrawable());

		new Handler().postDelayed(new Runnable() {

			public void run() {
				popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
			}

		}, 100L);

		ok_popup_vpn_message.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popup.dismiss();
			}
		});
	}

	public static void showPopup_VPN_Block(Activity context) {

		DisplayMetrics displaymetrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;

		int popupwidth = width - 100;
		int popupHeight = height - 250;

		// Inflate the popup_layout.xml
		LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.totalview_popup_vpn_block);
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = layoutInflater.inflate(R.layout.popup_layout_block_vpn, viewGroup);

		// Creating the PopupWindow
		final PopupWindow popup = new PopupWindow(context);
		popup.setContentView(layout);
      /*  popup.setWidth(popupwidth);
        popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);*/
		popup.setWidth(900);
		popup.setHeight(460);
		popup.setFocusable(true);

		TextView ok_vpn = (TextView) layout.findViewById(R.id.ok_popup_vpn);

		ok_vpn.requestFocus();
		popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				popup.dismiss();

			}
		});


		// Clear the default translucent background
		popup.setBackgroundDrawable(new BitmapDrawable());


		popup.showAtLocation(layout, Gravity.CENTER, 0, 0);


		ok_vpn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popup.dismiss();
			}
		});
	}

	public static void showPopup_Login_Block(Activity context) {

		DisplayMetrics displaymetrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;

		int popupwidth = width - 100;
		int popupHeight = height - 250;

		// Inflate the popup_layout.xml
		LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.totalview_popup_login_block);
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = layoutInflater.inflate(R.layout.popup_layout_block_login, viewGroup);

		// Creating the PopupWindow
		final PopupWindow popup = new PopupWindow(context);
		popup.setContentView(layout);
      /*  popup.setWidth(popupwidth);
        popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);*/
		popup.setWidth(900);
		popup.setHeight(460);
		popup.setFocusable(true);

		TextView ok_vpn = (TextView) layout.findViewById(R.id.ok_popup_login_block);

		ok_vpn.requestFocus();
		popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				popup.dismiss();

			}
		});




		// Clear the default translucent background
		popup.setBackgroundDrawable(new BitmapDrawable());


		popup.showAtLocation(layout, Gravity.CENTER, 0, 0);


		ok_vpn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popup.dismiss();
			}
		});
	}
	public static void showPopup_Error_register_package(Activity context) {

		DisplayMetrics displaymetrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;

		int popupwidth = width - 100;
		int popupHeight = height - 250;

		// Inflate the popup_layout.xml
		LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.totalview_popup_register_error);
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = layoutInflater.inflate(R.layout.popup_layout_error_register, viewGroup);

		// Creating the PopupWindow
		final PopupWindow popup = new PopupWindow(context);
		popup.setContentView(layout);
      /*  popup.setWidth(popupwidth);
        popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);*/
		popup.setWidth(1000);
//		popup.setHeight(460);
		popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
		popup.setFocusable(true);

		TextView ok_vpn = (TextView) layout.findViewById(R.id.ok_popup_register_error);
		ok_vpn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (!b) {
					view.animate().scaleX(1).scaleY(1).setDuration(200);
				} else {
					view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);
				}
			}
		});
		ok_vpn.requestFocus();
		popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				popup.dismiss();

			}
		});




		// Clear the default translucent background
		popup.setBackgroundDrawable(new BitmapDrawable());


		popup.showAtLocation(layout, Gravity.CENTER, 0, 0);


		ok_vpn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popup.dismiss();
			}
		});
	}

	public static String getDatesWithDaysName(String dt) {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		// Start date
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(dateFormat.parse(dt));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		StringBuilder builder = new StringBuilder();
		int dayOfWeek=calendar.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == 1)  {
			builder.append(dateFormat.format(calendar.getTime())).append(" ").append("יום ").append("ראשון");
		} else if (dayOfWeek == 2) {
			builder.append(dateFormat.format(calendar.getTime())).append(" ").append("יום ").append("שני");
		} else if (dayOfWeek == 3) {
			builder.append(dateFormat.format(calendar.getTime())).append(" ").append("יום ").append("שלישי");
		} else if (dayOfWeek == 4) {
			builder.append(dateFormat.format(calendar.getTime())).append(" ").append("יום ").append("רביעי");
		} else if (dayOfWeek == 5) {
			builder.append(dateFormat.format(calendar.getTime())).append(" ").append("יום ").append("חמישי");
		} else if (dayOfWeek == 6) {
			builder.append(dateFormat.format(calendar.getTime())).append(" ").append("יום ").append("שישי");
		} else {
			builder.append(dateFormat.format(calendar.getTime())).append(" ").append("יום ").append("שבת");
		}
		return builder.toString();
	}

	public static boolean isStoreVersion(Context context) {
		boolean isStoreVersion = false;
		try {
			String installer = context.getPackageManager()
					.getInstallerPackageName(context.getPackageName());
			isStoreVersion = !TextUtils.isEmpty(installer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isStoreVersion;
	}
}
