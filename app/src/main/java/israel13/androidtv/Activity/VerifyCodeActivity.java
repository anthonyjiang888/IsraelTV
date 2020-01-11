package israel13.androidtv.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import israel13.androidtv.NetworkOperation.IJSONParseListener;
import israel13.androidtv.NetworkOperation.JSONRequestResponse;
import israel13.androidtv.NetworkOperation.MyVolley;
import israel13.androidtv.R;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.CustomEditText;
import israel13.androidtv.Utils.CustomTextView;
import israel13.androidtv.Utils.LinkClickListener;
import israel13.androidtv.Utils.UiUtils;

import static israel13.androidtv.R.id.et_username;

/**
 * Created by puspak on 2017.12.01.
 */

public class VerifyCodeActivity extends Activity implements IJSONParseListener, LinkClickListener {

	ProgressDialog wait_dialog;

	CustomEditText edit_verify_code;
	Button tv_resend_code;
	TextView tv_contact_us, tv_description, tv_resend_count;
	Button btn_verify, verify_btn_contact_us;

	int send_count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_verify_code);

		tv_description = (TextView)findViewById(R.id.verify_tv_description);
		tv_description.append(Html.fromHtml("<b>" + getIntent().getStringExtra("email") + "</b>"));

		edit_verify_code = (CustomEditText) findViewById(R.id.verify_edit_code);
		edit_verify_code.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if (!hasFocus) {
					InputMethodManager immgr = (InputMethodManager) VerifyCodeActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					immgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
			}

		});

		tv_resend_code = (Button) findViewById(R.id.verify_tv_resend_code);
		tv_resend_code.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				requestResendingEmail();
			}
		});

		tv_resend_count = (TextView)findViewById(R.id.verify_tv_resend_count);

		tv_contact_us = (TextView)findViewById(R.id.verify_tv_contact_us);

        verify_btn_contact_us =(Button) findViewById(R.id.verify_btn_contact_us);
        verify_btn_contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(VerifyCodeActivity.this, ContactUsActivity.class));
            }
        });

		btn_verify = (Button) findViewById(R.id.verify_btn_verify);
		btn_verify.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendVerifyCode();
			}
		});
	}

	private void sendVerifyCode() {
		String verify_code = edit_verify_code.getText().toString();
		if (verify_code.isEmpty()) {
			edit_verify_code.setError( R.string.verify_error_code_empty);
			return;
		}

		MyVolley.init(VerifyCodeActivity.this);
		showProgressDilog(VerifyCodeActivity.this);

		JSONRequestResponse response = new JSONRequestResponse(VerifyCodeActivity.this);
		Bundle params = new Bundle();
		params.putString("id", verify_code);
		response.getResponse(Request.Method.GET, Constant.BASE_URL + "active.php", Constant.RETURN_CODE_GET_VERIFY, VerifyCodeActivity.this, params, false);
	}

	private void requestResendingEmail() {
		MyVolley.init(VerifyCodeActivity.this);
		showProgressDilog(this);

		JSONRequestResponse mResponse = new JSONRequestResponse(VerifyCodeActivity.this);
		Bundle params = new Bundle();
		params.putString("email", getIntent().getStringExtra("email"));
		mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "reactivemail.php", Constant.RETURN_CODE_GET_VERIFY_RESEND, this, params, false);

		send_count ++;
		tv_resend_count.setText(String.valueOf(send_count));
	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		UiUtils.showToast(this, R.string.verify_error_other);
		dismissProgress(VerifyCodeActivity.this);
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {

		dismissProgress(VerifyCodeActivity.this);

		String errorcode = "";
		if (requestCode == Constant.RETURN_CODE_GET_VERIFY) {
			try {
				errorcode = response.getString("error");
			} catch (Exception e) {
				try { errorcode = response.getString("errorcode"); } catch (JSONException e1) { e1.printStackTrace(); }
			}

			if (errorcode.equalsIgnoreCase("0")){
				saveRegisterInfos(response);

				finish();
				if (RegistrationActivity.regAct != null) {
					RegistrationActivity.regAct.finish();
					RegistrationActivity.regAct = null;
				}

				try {
					if (!response.has("expires") || Constant.parseInt(response.getString("expires")) == 0) {
						Toast.makeText(VerifyCodeActivity.this, "החשבון אומת בהצלחה, אך לא ניתן לקבל ניסיון נוסף מכיוון שהמערכת זיהתה שקיבלת בעבר", Toast.LENGTH_LONG).show();
						Intent intent = new Intent(VerifyCodeActivity.this, HomeActivity.class);
						startActivity(intent);
					} else {
						UiUtils.showToast(this, "החשבון אומת בהצלחה ");
						startActivity(new Intent(getApplicationContext(), HomeActivity.class));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			} else if (errorcode.equalsIgnoreCase("58117")) {
				edit_verify_code.setError(R.string.verify_error_58117);
			} else if (errorcode.equalsIgnoreCase("58403")) {
				Constant.showPopup_Error_vpn_message(VerifyCodeActivity.this);
			} else {
				Constant.ShowErrorToast(VerifyCodeActivity.this);
			}

		}
		else if (requestCode == Constant.RETURN_CODE_GET_VERIFY_RESEND) {
				try {
					errorcode = response.getString("error");
				} catch (Exception e) {
					try { errorcode = response.getString("errorcode"); } catch (JSONException e1) { e1.printStackTrace();
					}
				}

				if (errorcode.equalsIgnoreCase("0")) {
					UiUtils.showToast(this, R.string.verify_error_0);
				} else if (errorcode.equalsIgnoreCase("58118")) {
					UiUtils.showToast(this, R.string.verify_error_58118);
//					tv_resend_code.setError(R.string.verify_error_58118);
				} else if (errorcode.equalsIgnoreCase("58119")) {
					UiUtils.showToast(this, R.string.verify_error_58119);
//					tv_resend_code.setError(R.string.verify_error_58119);
				} else if (errorcode.equalsIgnoreCase("58200")) {
					UiUtils.showToast(this, R.string.verify_error_58120);
//					tv_resend_code.setError(R.string.verify_error_58120);
				}
		}
	}

	@Override
	public void SuccessResponseArray(JSONArray response, int requestCode) {
		dismissProgress(VerifyCodeActivity.this);
	}

	@Override
	public void SuccessResponseRaw(String response, int requestCode) {
		dismissProgress(VerifyCodeActivity.this);
	}

	private void saveRegisterInfos(JSONObject response) {
		SharedPreferences logindetails = this.getSharedPreferences("logindetails", LoginActivity.MODE_PRIVATE);
		SharedPreferences.Editor edit = logindetails.edit();

		try { edit.putString("id", response.getString("id")); } catch (JSONException e) { edit.putString("id", ""); }
		try { edit.putString("name", response.getString("name")); } catch (JSONException e) { edit.putString("name", ""); }
		try { edit.putString("password", response.getString("password")); } catch (JSONException e) { edit.putString("password", ""); }
		try { edit.putString("expires", response.getString("expires")); } catch (JSONException e) { edit.putString("expires", ""); }

		JSONObject package1 = null;
		try { package1 = response.getJSONObject("package"); } catch (JSONException e) { e.printStackTrace(); }
		try { edit.putString("package_id", package1.getString("id")); } catch (JSONException e) { edit.putString("package_id", ""); }
		try { edit.putString("package_name", package1.getString("name")); } catch (JSONException e) { edit.putString("package_name", ""); }
		try { edit.putString("package_price", package1.getString("price")); } catch (JSONException e) { edit.putString("package_price", ""); }
		try { edit.putString("package_pricestr", package1.getString("pricestr")); } catch (JSONException e) { edit.putString("package_pricestr", ""); }
		try { edit.putString("package_pgname", package1.getString("pgname")); } catch (JSONException e) { edit.putString("package_pgname", ""); }
		try { edit.putString("package_description", package1.getString("description")); } catch (JSONException e) { edit.putString("package_description", ""); }

		try { edit.putString("regtime", response.getString("regtime")); } catch (JSONException e) { edit.putString("regtime", ""); }
		try { edit.putString("phone", response.getString("phone")); } catch (Exception e) { edit.putString("phone", ""); }
		try { edit.putString("carousel", response.getString("carousel")); } catch (Exception e) { edit.putString("carousel", ""); }
		try { edit.putString("isactive", response.getString("isactive")); } catch (Exception e) { edit.putString("isactive", ""); }
		try { edit.putString("activekey", response.getString("activekey")); } catch (Exception e) { edit.putString("activekey", ""); }
		try { edit.putString("email", response.getString("email")); } catch (Exception e) { edit.putString("email", ""); }
		try { edit.putString("status", response.getString("status")); } catch (Exception e) { edit.putString("status", ""); }
		try { edit.putString("ptime", response.getString("ptime")); } catch (Exception e) { edit.putString("ptime", ""); }
		try { edit.putString("sid", response.getString("sid")); } catch (Exception e) { edit.putString("sid", ""); }

		edit.commit();
	}

	void showProgressDilog(Context c) {
		wait_dialog = new ProgressDialog(c);
		wait_dialog.show();
		wait_dialog.setCancelable(false);
		wait_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		wait_dialog.setContentView(R.layout.layout_progress_dilog);
	}

	void dismissProgress(Context c) {
		if (wait_dialog != null && wait_dialog.isShowing())
			wait_dialog.dismiss();
	}

	@Override
	public void clickLink(int viewId) {
		switch (viewId) {
//			case R.id.verify_tv_resend_code:
//				requestResendingEmail();
//				break;
			case R.id.verify_tv_contact_us:
//				startActivity(new Intent(VerifyCodeActivity.this, ContactUsActivity.class));
				break;
		}
	}
}
