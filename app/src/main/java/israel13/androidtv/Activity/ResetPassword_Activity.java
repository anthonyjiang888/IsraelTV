package israel13.androidtv.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import israel13.androidtv.NetworkOperation.IJSONParseListener;
import israel13.androidtv.NetworkOperation.JSONRequestResponse;
import israel13.androidtv.NetworkOperation.MyVolley;
import israel13.androidtv.R;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.UiUtils;

/**
 * Created by Puspak on 06/11/17.
 */
public class ResetPassword_Activity extends AppCompatActivity implements IJSONParseListener {

	private EditText et_email_forgot_password;
	private TextView tv_send_forgot_password;
	private int FORGOT_PASSWORD = 100;
	private ProgressDialog pDialog;

	public static String email_forgot_password_text = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_reset_password);

		et_email_forgot_password = (EditText) findViewById(R.id.et_email_forgot_password);
		tv_send_forgot_password = (TextView) findViewById(R.id.tv_send_forgot_password);
		et_email_forgot_password.setHint(Html.fromHtml("<i>" + "Email address " + "</i>"));
		et_email_forgot_password.requestFocus();
		tv_send_forgot_password.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (et_email_forgot_password.getText().toString().length() == 0) {
					et_email_forgot_password.setError(getResources().getString(R.string.enter_email));
				} else {
					ForgotPassword();
				}
			}
		});
		tv_send_forgot_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (view != null) {
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
				System.gc();
				if (!b){
					view.animate().scaleY(1).scaleX(1).setDuration(200);
				}else{
					view.animate().scaleX(1.03f).scaleY(1.03f).setDuration(200);
				}
			}
		});
	}

	void ForgotPassword() {
		JSONRequestResponse mResponse = new JSONRequestResponse(ResetPassword_Activity.this);
		Bundle parms = new Bundle();
		parms.putString("email", et_email_forgot_password.getText().toString());
		MyVolley.init(ResetPassword_Activity.this);
		ShowProgressDilog(this);
		mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "forgetpassword.php", FORGOT_PASSWORD, this, parms, false);
	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {

		DismissProgress(ResetPassword_Activity.this);
		Constant.ShowErrorToast(ResetPassword_Activity.this);
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {

		if (requestCode == FORGOT_PASSWORD) {
			DismissProgress(ResetPassword_Activity.this);
			try {
				if (response.getString("errorcode").equalsIgnoreCase("0")) {
					email_forgot_password_text = et_email_forgot_password.getText().toString().trim();
					UiUtils.showToast(this, getResources().getString(R.string.reset_password_success) + " " + et_email_forgot_password.getText().toString());
					finish();
					startActivity(new Intent(ResetPassword_Activity.this, LoginActivity.class));
				} else if (response.getString("errorcode").equalsIgnoreCase("11008")) {
					UiUtils.showToast(this, R.string.reset_password_error_11008);
				} else if (response.getString("errorcode").equalsIgnoreCase("11009")) {
					UiUtils.showToast(this, R.string.reset_password_error_11009);
				}
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
		pDialog.setCancelable(false);
		pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		pDialog.setContentView(R.layout.layout_progress_dilog);
	}

	void DismissProgress(Context c) {
		if (pDialog != null && pDialog.isShowing())
			pDialog.dismiss();
	}
}
