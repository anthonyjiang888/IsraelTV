package israel13.androidtv.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import israel13.androidtv.Activity.LoginActivity;
import israel13.androidtv.NetworkOperation.IJSONParseListener;
import israel13.androidtv.NetworkOperation.JSONRequestResponse;
import israel13.androidtv.NetworkOperation.MyVolley;
import israel13.androidtv.R;

public class UiUtils {

	public  static  void showToast(final Context context, final  String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public  static  void showToast(final Context context, final  int resid) {
		Toast.makeText(context, context.getResources().getString(resid), Toast.LENGTH_SHORT).show();
	}

	public static void appendLink(final Context context, final TextView textview, final String text, final LinkClickListener listener) {

//		final DynamicDrawableSpan span_focused_background = new DynamicDrawableSpan() {
//			@Override
//			public Drawable getDrawable() {
//				return context.getResources().getDrawable(R.drawable.edit_background_focused);
//			}
//		};
//
		ClickableSpan span_click = new ClickableSpan() {
			@Override
			public void onClick(View widget) {
				listener.clickLink(textview.getId());
			}
		};

		final SpannableStringBuilder span = new SpannableStringBuilder(text);
		span.setSpan(span_click, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		span.setSpan(new ForegroundColorSpan(Color.rgb(51, 122, 183)), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//		span.setSpan(span_focused_background, 0, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		textview.append(span);

		LinkMovementMethod lmm = (LinkMovementMethod) LinkMovementMethod.getInstance();
		textview.setMovementMethod(lmm);

		textview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					((Activity)context).dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN));
				}

//				Editable text = textview.getEditableText();
//				SpannableStringBuilder ssb = new SpannableStringBuilder(text);
//				ClickableSpan[] spans = text.getSpans(0, text.length(), ClickableSpan.class);
//				int start = text.nextSpanTransition(0, text.length(), ClickableSpan.class);
//				int end = text.nextSpanTransition(start, text.length(), ClickableSpan.class);
//
//				for (int i = 0; i < spans.length; i++) {
//					ssb.setSpan(hasFocus ? span_focused_background : null, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//					start = text.nextSpanTransition(end, text.length(), ClickableSpan.class);
//					end = text.nextSpanTransition(start, text.length(), ClickableSpan.class);
//				}
			}
		});
	}

	public  static int convertDpToPx(Context context, int dp) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return dp * (metrics.densityDpi / 160);
	}

	public static void logout(final Activity activity) {
		JSONRequestResponse mResponse = new JSONRequestResponse(activity);
		Bundle parms = new Bundle();
		final SharedPreferences preferences = activity.getSharedPreferences("logindetails", Activity.MODE_PRIVATE);
		parms.putString("sid",  preferences.getString("sid",""));
		parms.putString("act","logout");
		parms.putString("dtype", Constant.DEVICE_TYPE);
		parms.putString("serialno", Constant.getDeviceUUID(activity));
		MyVolley.init(activity);

		final ProgressDialog pDialog = new ProgressDialog(activity);
		pDialog.show();
		pDialog.setCancelable(true);
		pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		pDialog.setContentView(R.layout.layout_progress_dilog);

		mResponse.getResponse(Request.Method.POST, Constant.BASE_URL + "slogin.php", 0, new IJSONParseListener() {
			@Override
			public void ErrorResponse(VolleyError error, int requestCode) {
				pDialog.dismiss();
				SharedPreferences.Editor edit = preferences.edit();
				edit.clear();
				edit.commit();
				Intent in = new Intent(activity, LoginActivity.class);
				in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				activity.startActivity(in);
				activity.finish();
			}

			@Override
			public void SuccessResponse(JSONObject response, int requestCode) {
				pDialog.dismiss();
				SharedPreferences.Editor edit = preferences.edit();
				edit.clear();
				edit.commit();
				Intent in = new Intent(activity, LoginActivity.class);
				in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				activity.startActivity(in);
				activity.finish();
			}

			@Override
			public void SuccessResponseArray(JSONArray response, int requestCode) {
				pDialog.dismiss();
				SharedPreferences.Editor edit = preferences.edit();
				edit.clear();
				edit.commit();
				Intent in = new Intent(activity, LoginActivity.class);
				in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				activity.startActivity(in);
				activity.finish();
			}

			@Override
			public void SuccessResponseRaw(String response, int requestCode) {
				pDialog.dismiss();
				SharedPreferences.Editor edit = preferences.edit();
				edit.clear();
				edit.commit();
				Intent in = new Intent(activity, LoginActivity.class);
				in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				activity.startActivity(in);
				activity.finish();
			}
		}, parms, false);
	}
}
