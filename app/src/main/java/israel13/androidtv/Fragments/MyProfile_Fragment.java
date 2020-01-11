package israel13.androidtv.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.Activity.LoginActivity;
import israel13.androidtv.Activity.Splash;
//import israel13.androidtv.Activity.UpdateActivity;
import israel13.androidtv.NetworkOperation.IJSONParseListener;
import israel13.androidtv.NetworkOperation.JSONRequestResponse;
import israel13.androidtv.NetworkOperation.MyVolley;
import israel13.androidtv.R;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.NetworkUtil;
import israel13.androidtv.Utils.UiUtils;


/**
 * Created by puspak on 04/07/17.
 */
public class MyProfile_Fragment extends Fragment implements IJSONParseListener {

	TextView full_name_myprofile, email_myprofile, phone_myprofile, status_myprofile;
	TextView exist_package_myprofile, expired_in_myprofile;
	LinearLayout back_button_my_profile;

	SharedPreferences logindetails;
	ProgressDialog pDialog;
	int GET_USER_DETAILS = 101;
	String expire = "";
	long expire_in_rdatetime = 0;
	Button Log_out_btn, speed_test_btn,update_btn;
	String flag_expire = "";
	TextView app_version;
	String version = "";
	private static boolean isSpeedTestGoandOut = false;
	ScrollView scrollView;
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = layoutInflater.inflate(R.layout.activity_my_profile, null);

		scrollView = (ScrollView)layout.findViewById(R.id.scrollview2);

		full_name_myprofile = (TextView) layout.findViewById(R.id.full_name_myprofile);
		email_myprofile = (TextView) layout.findViewById(R.id.email_myprofile);
		phone_myprofile = (TextView) layout.findViewById(R.id.phone_myprofile);
		status_myprofile = (TextView) layout.findViewById(R.id.status_myprofile);
		app_version = (TextView) layout.findViewById(R.id.app_version);
		exist_package_myprofile = (TextView) layout.findViewById(R.id.exist_package_myprofile);
		expired_in_myprofile = (TextView) layout.findViewById(R.id.expired_in_myprofile);
		Log_out_btn = (Button) layout.findViewById(R.id.Log_out_btn);
		speed_test_btn = (Button) layout.findViewById(R.id.speed_test_btn);
		update_btn = (Button)layout.findViewById(R.id.update_btn);


		logindetails = getActivity().getSharedPreferences("logindetails", LoginActivity.MODE_PRIVATE);

		int versionCode = 0;
		try {
			PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
			version = pInfo.versionName;
			versionCode = pInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		boolean isStoreVersion = Constant.isStoreVersion(getActivity());
		if (isStoreVersion) {
			app_version.setText("Android TV Version : " + version + " (" + versionCode + ")");
		} else {
			app_version.setText("Android TV Version : " + version + " (" + versionCode + " - SideLoaded)");
		}

		// GetUserDetails();
		Log_out_btn.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View view, int i, KeyEvent keyEvent) {
				if (i == keyEvent.KEYCODE_DPAD_UP) {
					((HomeActivity)getActivity()).requestTabFocus(Constant.TAB_PROFILE);
					scrollView.smoothScrollTo(0, 0);
					return true;
				}
				return  false;

			}
		});
		speed_test_btn.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View view, int i, KeyEvent keyEvent) {
				if (i == keyEvent.KEYCODE_DPAD_UP) {
					((HomeActivity)getActivity()).requestTabFocus(Constant.TAB_PROFILE);
					scrollView.smoothScrollTo(0, 0);
					return true;
				}
			return false;
			}
		});
		Log_out_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {


				ContextThemeWrapper ctx = new ContextThemeWrapper(getActivity(), R.style.Theme_Sphinx);
				android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ctx);


				TextView myMsg = new TextView(getActivity());
				myMsg.setText("התנתק");
				myMsg.setGravity(Gravity.RIGHT);
				myMsg.setTextSize(23);
				myMsg.setPadding(10, 25, 25, 25);
				myMsg.setTextColor(getResources().getColor(R.color.sky_blue));
				builder.setCustomTitle(myMsg);

				//builder.setTitle("התנתק");
				builder.setMessage("האם הינך בטוח שברצונך להתנתק?");

				builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						UiUtils.logout(getActivity());
					}
				});


				builder.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();


					}
				});

				android.app.AlertDialog dialog = builder.create();
				dialog.show();
			}
		});
		Log_out_btn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (!b){
					view.animate().scaleX(1).scaleY(1).setDuration(200);
				}else{
					view.animate().scaleX(1.08f).scaleY(1.08f).setDuration(200);

					scrollView.smoothScrollTo(0,scrollView.getScrollY() + 300);

				}
			}
		});
		speed_test_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isSpeedTestGoandOut = true;
				Speed_test speed_test = new Speed_test();
				FragmentTransaction ft1 = getActivity().getSupportFragmentManager().beginTransaction();
				ft1.replace(R.id.fram_container, speed_test, "speed_test");
				ft1.addToBackStack(null);
				ft1.commit();
			}
		});
		speed_test_btn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (!b){
					view.animate().scaleX(1).scaleY(1).setDuration(200);
				}else{

					view.animate().scaleX(1.08f).scaleY(1.08f).setDuration(200);
					scrollView.smoothScrollTo(0,scrollView.getScrollY() + 300);
				}
			}
		});
		update_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent i = new Intent(getActivity(), UpdateActivity.class);
//				i.putExtra("fromActivity","profile");
//				getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//				startActivity(i);
			}
		});
		update_btn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (!b){
					view.animate().scaleX(1).scaleY(1).setDuration(200);
				}else{
					view.animate().scaleX(1.08f).scaleY(1.08f).setDuration(200);
				}
			}
		});
		return layout;

	}

	@Override
	public void onResume() {
		Call_api();
		getView().setFocusableInTouchMode(false);
		getView().requestFocus();
		if (isSpeedTestGoandOut == true) {
			scrollView.smoothScrollTo(0,scrollView.getScrollY() + 300);
			speed_test_btn.requestFocus();

		}
		isSpeedTestGoandOut = false;
		super.onResume();
	}

	public void Call_api() {
		if (NetworkUtil.checkNetworkAvailable(getActivity())) {

			GetUserDetails();
		} else {
			ShowErrorAlert(getActivity(), "Please check your network connection..");
		}
	}

	public void ShowErrorAlert(final Context c, String text) {
		android.app.AlertDialog.Builder alertDialogBuilder;
		ContextThemeWrapper ctx = new ContextThemeWrapper(c, R.style.Theme_Sphinx);
		alertDialogBuilder = new android.app.AlertDialog.Builder(ctx);

		alertDialogBuilder.setTitle("Alert!");
		alertDialogBuilder.setMessage(text);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				Call_api();
			}
		});
		alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
			}
		});

		android.app.AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.show();

	}


	void GetUserDetails() {
		JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
		Bundle parms = new Bundle();
		parms.putString("sid", logindetails.getString("sid", ""));
		MyVolley.init(getActivity());
		ShowProgressDilog(getActivity());
		mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "/loaduser.php", GET_USER_DETAILS, this, parms, false);
	}

	public String GetCountryZipCode() {
		String CountryID = "";
		String CountryZipCode = "";

		TelephonyManager manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		//getNetworkCountryIso
		CountryID = manager.getSimCountryIso().toUpperCase();
		String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
		for (int i = 0; i < rl.length; i++) {
			String[] g = rl[i].split(",");
			if (g[1].trim().equals(CountryID.trim())) {
				CountryZipCode = g[0];
				break;
			}
		}
		return CountryZipCode;
	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {

		DismissProgress(getActivity());
		Constant.ShowErrorToast(getActivity());

	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {

		DismissProgress(getActivity());
		if (requestCode == GET_USER_DETAILS) {
			String errorcode = "";
			try {
				errorcode = response.getString("error");
			} catch (Exception e) {
				try {
					errorcode = response.getString("errorcode");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}

			try {

				if (errorcode.equalsIgnoreCase("0")) {
					if (response.getString("status").equalsIgnoreCase("0")) {
						status_myprofile.setText("מנוי פעיל");
					} else if (response.getString("status").equalsIgnoreCase("10")) {
						JSONObject freeze = response.getJSONObject("freeze");
						String freeze_time = Constant.getUnixDate(freeze.getString("endtime"));
						status_myprofile.setText("המנוי מוקפא עד תאריך: " + freeze_time);
					} else if (response.getString("status").equalsIgnoreCase("100")) {
						//its a suspended user
						status_myprofile.setText("המנוי חסום");
					}
				} else {
					if (errorcode.equalsIgnoreCase("999")) {
						status_myprofile.setText("מנוי לא פעיל");
						flag_expire = "expired";
					}else if (errorcode.equalsIgnoreCase("997")) {
						status_myprofile.setText("המנוי חסום");
					} else if(errorcode.equalsIgnoreCase("911")
							|| errorcode.equalsIgnoreCase("99")) {
						SharedPreferences.Editor edit = logindetails.edit();
						edit.clear();
						edit.commit();

						Intent in = new Intent(getContext(), LoginActivity.class);
						in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(in);
						return;
					}
				}

				phone_myprofile.setText(response.getString("phonearea") + " - " + response.getString("phone"));
				full_name_myprofile.setText(response.getString("name"));
				email_myprofile.setText(response.getString("email"));
				try {
					expire = response.getString("expires");
				} catch (Exception e) {
					expire = response.getString("0");
				}
				try {
					expire_in_rdatetime = Long.parseLong(expire);

				} catch (Exception e) {
					expire_in_rdatetime = 0;
				}

				try {
					JSONObject package1 = response.getJSONObject("package");
					exist_package_myprofile.setText(package1.getString("name") + " - "
							+ package1.getString("pricestr"));
				} catch (Exception e) {
					exist_package_myprofile.setText("");
				}


				String pattern = "dd-MM-yyyy HH:mm";
				SimpleDateFormat sdf = new SimpleDateFormat(pattern);
				sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
				String currdate = sdf.format(new Date());
				String end_date = Constant.getUnixDateMyprofile_new_format(expire);
				String daystofinish = Constant.findDaysDiffByDate(currdate, end_date);

				if (flag_expire.equalsIgnoreCase("expired")) {
					expired_in_myprofile.setText(Constant.getUnixDateMyprofile(expire));

				} else {
					expired_in_myprofile.setText(Constant.getUnixDateMyprofile(expire) + "\n" + "( " + daystofinish + " ימים לסיום" + " )");

				}
				System.out.println("-----------");

			} catch (Exception e) {
				e.printStackTrace();
			}

		}


	}

	@Override
	public void SuccessResponseArray(JSONArray response, int requestCode) {

	}

	@Override
	public void SuccessResponseRaw(String response, int requestCode) {

	}

	void ShowProgressDilog(Context c) {
		pDialog = new ProgressDialog(c);
		pDialog.show();
		pDialog.setCancelable(true);
		pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		pDialog.setContentView(R.layout.layout_progress_dilog);
	}

	void DismissProgress(Context c) {
		if (pDialog != null && pDialog.isShowing())
			pDialog.dismiss();
	}
}
