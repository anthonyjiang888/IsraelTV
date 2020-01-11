package israel13.androidtv.Activity;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import israel13.androidtv.Adapter.CountryCodeAdapter;
import israel13.androidtv.NetworkOperation.IJSONParseListener;
import israel13.androidtv.NetworkOperation.JSONRequestResponse;
import israel13.androidtv.NetworkOperation.MyVolley;
import israel13.androidtv.R;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.CustomEditText;
import israel13.androidtv.Utils.UiUtils;
import israel13.androidtv.Utils.VerifyUtil;

import static com.makeramen.roundedimageview.RoundedDrawable.TAG;

/**
 * Created by puspak on 2017.11.30.
 */

public class RegistrationActivity extends Activity implements IJSONParseListener {

	ScrollView  scrollContainer;

	CustomEditText edit_user_name;
	CustomEditText edit_email, edit_email_verification;
	CustomEditText edit_password, edit_confirm_password;
	CustomEditText edit_phone;
	CustomEditText edit_capture_code;
	CustomEditText edit_referral_code;

	Button btn_password_show, btn_confirm_password_show;
	Button btn_join_us;

	ImageButton btn_refresh_capture_code, btn_get_capture_code;

	EditText	edit_code_search;
	ListView listview_code_list;

	Button btn_country_code;
	ImageView img_code_arrow;
	ImageView	img_country_flag;
	PopupWindow pw_country_code;
	CountryCodeAdapter code_adapter;

	ProgressDialog wait_dialog;

	Bitmap bmp_capture_code;

	public static RegistrationActivity regAct = null;

	private View mCountryBeforeView = null;
	private int mScroll = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		regAct = this;

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_registration);

		scrollContainer = (ScrollView) findViewById(R.id.scrollContainer);
		edit_user_name = (CustomEditText) findViewById(R.id.register_edit_name);
		addCheckListeners(edit_user_name, true);

		edit_email = (CustomEditText) findViewById(R.id.register_edit_email);
		addCheckListeners(edit_email, true);

		edit_email_verification = (CustomEditText) findViewById(R.id.register_edit_email_verification);
		addCheckListeners(edit_email_verification, true);

		edit_password = (CustomEditText) findViewById(R.id.register_edit_password);
		addCheckListeners(edit_password, true);

		edit_confirm_password = (CustomEditText) findViewById(R.id.register_edit_confirm_password);
		addCheckListeners(edit_confirm_password, true);

		btn_password_show = (Button) findViewById(R.id.register_btn_password);
		btn_password_show.setOnClickListener(btnClickListener);

		btn_confirm_password_show = (Button) findViewById(R.id.register_btn_confirm_password);
		btn_confirm_password_show.setOnClickListener(btnClickListener);

		img_country_flag = (ImageView)findViewById(R.id.register_img_flag);
		img_code_arrow = (ImageView) findViewById(R.id.register_img_code_arrow);

		btn_country_code = (Button) findViewById(R.id.register_btn_country_code);
		btn_country_code.setOnClickListener(btnClickListener);
		btn_country_code.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					InputMethodManager immgr = (InputMethodManager) RegistrationActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					immgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
		});

		edit_phone = (CustomEditText) findViewById(R.id.register_edit_phone);
		addCheckListeners(edit_phone, true);

		edit_capture_code = (CustomEditText) findViewById(R.id.register_edit_capture_code);
		addCheckListeners(edit_capture_code, true);
		edit_capture_code.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
				if (i == EditorInfo.IME_ACTION_NEXT) {
					edit_referral_code.requestFocus();
					return true;
				}
				return false;
			}
		});

		btn_get_capture_code = (ImageButton) findViewById(R.id.register_btn_capture_code);
		btn_refresh_capture_code = (ImageButton) findViewById(R.id.register_btn_get_capture_code);
		btn_refresh_capture_code.setOnClickListener(btnClickListener);
		btn_refresh_capture_code.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					InputMethodManager immgr = (InputMethodManager) RegistrationActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					immgr.hideSoftInputFromWindow(btn_join_us.getWindowToken(), 0);
				}
				if (!hasFocus){
					v.animate().scaleX(1).scaleY(1).setDuration(200);
				}else{
					v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);
				}
			}
		});


		edit_referral_code = (CustomEditText) findViewById(R.id.register_edit_referral_code);
		addCheckListeners(edit_referral_code, false);
		edit_referral_code.setOnFocusChangeListener(new View.OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus){
				if(hasFocus){
//					Rect r = new Rect();
//					scrollContainer.getWindowVisibleDisplayFrame(r);
//					int screenHeight = scrollContainer.getRootView().getHeight();
//
//					// r.bottom is the position above soft keypad or device button.
//					// if keypad is shown, the r.bottom is smaller than that before.
//					int keypadHeight = screenHeight - r.bottom;
//
//					Log.d(TAG, "keypadHeight = " + keypadHeight);
//
//					if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
//						// keyboard is opened
//						if(!edit_user_name.hasFocus()) {
//							int nScroll = scrollContainer.getScrollY();
//							if(edit_capture_code.hasFocus() || edit_referral_code.hasFocus()) {
//								scrollContainer.setPadding(0, 0, 0, scrollContainer.getPaddingBottom() + 100);
//								scrollContainer.scrollTo(0, nScroll + 100);
//								mScroll += 100;
//							}
//						}
//					}
//					else {
//						scrollContainer.setPadding(0, 0, 0, 0);
//						// keyboard is closed
//						if (mScroll > 0) {
//							int nScroll = scrollContainer.getScrollY();
//							if(!btn_join_us.hasFocus() && !edit_referral_code.hasFocus()) {
//								scrollContainer.scrollTo(0, nScroll - mScroll);
//							}
//							mScroll = 0;
//						}
//					}
				}
			}
		});

		btn_join_us = (Button) findViewById(R.id.register_btn_join_us);
		btn_join_us.setOnClickListener(btnClickListener);
		btn_join_us.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					InputMethodManager immgr = (InputMethodManager) RegistrationActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					immgr.hideSoftInputFromWindow(btn_join_us.getWindowToken(), 0);

					scrollContainer.fullScroll(ScrollView.FOCUS_DOWN);
				}
				if (!hasFocus){
					v.animate().scaleX(1).scaleY(1).setDuration(200);
				}else{
					v.animate().scaleX(1.04f).scaleY(1.05f).setDuration(200);
				}
			}
		});

		createCcpMenu();

		new RefreshCaptureCode().execute("");
		scrollContainer.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
		scrollContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Rect r = new Rect();
						scrollContainer.getWindowVisibleDisplayFrame(r);
						int screenHeight = scrollContainer.getRootView().getHeight();

						// r.bottom is the position above soft keypad or device button.
						// if keypad is shown, the r.bottom is smaller than that before.
						int keypadHeight = screenHeight - r.bottom;

						Log.d(TAG, "keypadHeight = " + keypadHeight);

						if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
							// keyboard is opened
							if(!edit_user_name.hasFocus() && !edit_email.hasFocus()) {

								int focusPosition = getFocusObjPositionY();
								if(focusPosition > (r.bottom - 100) || focusPosition == 0) {
									int nScroll = scrollContainer.getScrollY();
									if (edit_capture_code.hasFocus() || edit_referral_code.hasFocus()) {
										RegistrationActivity.this.findViewById(R.id.bottomView).setVisibility(View.VISIBLE);
										scrollContainer.scrollTo(0, nScroll + 100);
										mScroll += 100;
									} else {
										scrollContainer.scrollTo(0, nScroll + 50);
										mScroll += 50;
									}
								}
							}
						}
						else {
							RegistrationActivity.this.findViewById(R.id.bottomView).setVisibility(View.GONE);
							// keyboard is closed
							if (mScroll > 0) {
								int nScroll = scrollContainer.getScrollY();
								if(!btn_join_us.hasFocus() && !edit_referral_code.hasFocus()) {
									scrollContainer.scrollTo(0, nScroll - mScroll);
								}
								mScroll = 0;
							}
						}
					}
				}, 100);
			}
		});
	}

	private int getFocusObjPositionY(){
		if(edit_email_verification.hasFocus()){
			int screenLocation[] = new int[2];
			edit_email_verification.getLocationInWindow(screenLocation);
			return screenLocation[1];
		}
		if(edit_password.hasFocus()){
			int screenLocation[] = new int[2];
			edit_password.getLocationInWindow(screenLocation);
			return screenLocation[1];
		}
		if(edit_confirm_password.hasFocus()){
			int screenLocation[] = new int[2];
			edit_confirm_password.getLocationInWindow(screenLocation);
			return screenLocation[1];
		}
		if(edit_phone.hasFocus()){
			int screenLocation[] = new int[2];
			edit_phone.getLocationInWindow(screenLocation);
			return screenLocation[1];
		}
		if(edit_capture_code.hasFocus()){
			int screenLocation[] = new int[2];
			edit_capture_code.getLocationInWindow(screenLocation);
			return screenLocation[1];
		}
		if(edit_referral_code.hasFocus()){
			int screenLocation[] = new int[2];
			edit_referral_code.getLocationInWindow(screenLocation);
			return screenLocation[1];
		}
		return 0;
	}

	void createCcpMenu() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.country_code_list_layout, null);

		edit_code_search = (EditText)view.findViewById(R.id.cc_edit_search);
		edit_code_search.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				code_adapter.updateList(String.valueOf(s));
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		edit_code_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					InputMethodManager immgr = (InputMethodManager) RegistrationActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					immgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				else {
					edit_code_search.setText("");

				}
			}
		});

		listview_code_list = (ListView)view.findViewById(R.id.cc_code_list);
		listview_code_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bitmap flag_icon = code_adapter.getFlagImage(position);
				if(flag_icon != null) {
					img_country_flag.setImageBitmap(flag_icon);
				}
				btn_country_code.setText(code_adapter.getCountryCode(position));
				pw_country_code.dismiss();

				btn_country_code.requestFocus();
			}
		});

		pw_country_code = new PopupWindow(this); // inflet your layout or diynamic add view
		pw_country_code.setFocusable(true);
		pw_country_code.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		pw_country_code.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		pw_country_code.setContentView(view);

		pw_country_code.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				img_code_arrow.setBackgroundResource(R.drawable.arrow_down);
				btn_country_code.requestFocus();
			}
		});

		code_adapter = new CountryCodeAdapter(this, R.layout.country_code_layout);
		listview_code_list.setAdapter(code_adapter);
		listview_code_list.requestFocus();

		listview_code_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				try {
					//View v = listview_code_list.getSelectedView();
					if (mCountryBeforeView != null) {
						TextView tv_country_code = (TextView) mCountryBeforeView.findViewById(R.id.code);
						tv_country_code.setTypeface(Typeface.DEFAULT);
						tv_country_code.setTextColor(getResources().getColor(R.color.black));
					}
					TextView tv_country_code = (TextView) view.findViewById(R.id.code);
					tv_country_code.setTypeface(Typeface.DEFAULT_BOLD);
					tv_country_code.setTextColor(getResources().getColor(android.R.color.black));
					mCountryBeforeView = view;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		listview_code_list.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				try {
					if (!hasFocus) {
						TextView tv_country_code = (TextView) mCountryBeforeView.findViewById(R.id.code);
						tv_country_code.setTypeface(Typeface.DEFAULT);
						tv_country_code.setTextColor(getResources().getColor(R.color.black));
						//mCountryBeforeView = null;
					} else {
						View view = listview_code_list.getChildAt(0);
						TextView tv_country_code = (TextView) view.findViewById(R.id.code);
						tv_country_code.setTypeface(Typeface.DEFAULT_BOLD);
						tv_country_code.setTextColor(getResources().getColor(android.R.color.black));

						mCountryBeforeView = view;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		int pos = code_adapter.searchPosition("Israel");
		if(pos == -1) pos = 0;

		String initCode = code_adapter.getCountryCode(pos);
		if(initCode != null) {
			btn_country_code.setText(initCode);
		}
		Bitmap initFlag = code_adapter.getFlagImage(pos);
		if(initFlag != null) {
			img_country_flag.setImageBitmap(initFlag);
		}
	}

	void addCheckListeners(final CustomEditText editor, final boolean required) {

		editor.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (editor.isModified()) {
					checkEditor(editor, required);
				}
			}
		});

		editor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				String text = editor.getText().toString();
				if (!hasFocus || editor.hasError()) {
					checkEditor(editor, required);
				}
			}
		});
	}

	boolean checkEditor(CustomEditText editor, boolean required) {

		if (!required) { // referral code
			if (editor.getText().toString().isEmpty()) {
				editor.setSuccess();
			}
			return true;
		}

		boolean require_error = checkRequire(editor);
		int length_error_id = checkLength(editor);
		int filter_error_id = checkFilter(editor);
		int equalto_error_id = checkEqualTo(editor);

		int error_id = -1;
		if (require_error) {
			error_id = R.string.register_required_error;
		} else if (length_error_id != -1) {
			error_id = length_error_id;
		} else if (filter_error_id != -1) {
			error_id = filter_error_id;
		} else if (equalto_error_id != -1) {
			error_id = equalto_error_id;
		}

		if (error_id == -1) {
			editor.setSuccess();
		} else {
			editor.setError(error_id);
		}
		return error_id == -1;
	}

	boolean checkRequire(CustomEditText editor) {
		String text = editor.getText().toString();

		boolean required_error = false;
		switch (editor.getId()) {
			case R.id.register_edit_name:
			case R.id.register_edit_email:
			case R.id.register_edit_email_verification:
			case R.id.register_edit_password:
			case R.id.register_edit_confirm_password:
			case R.id.register_edit_capture_code:
			case R.id.register_edit_phone:
				required_error = text.isEmpty();
				break;
		}
		return required_error;
	}

	int checkLength(CustomEditText editor) {
		String text = editor.getText().toString();

		int length_error = -1;
		switch (editor.getId()) {
			case R.id.register_edit_password:
			case R.id.register_edit_confirm_password:
				length_error = (text.length() < 4 ? R.string.register_password_length_error : -1);
				break;
			case R.id.register_edit_phone:
				length_error = (text.length() < 5 ? R.string.register_phone_length_error : -1);
				break;
		}
		return length_error;
	}

	int checkFilter(CustomEditText editor) {
		String text = editor.getText().toString();

		int filter_error = -1;
		switch (editor.getId()) {
			case R.id.register_edit_name:
				filter_error = VerifyUtil.isValidName(text) ? -1 : R.string.register_name_filter_error;
				break;
			case R.id.register_edit_email:
			case R.id.register_edit_email_verification:
				filter_error = VerifyUtil.isValidEmail(text) ? -1 : R.string.register_email_filter_error;
				break;
			case R.id.register_edit_password:
			case R.id.register_edit_confirm_password:
				filter_error = VerifyUtil.isValidPassword(text) ? -1 : R.string.register_password_filter_error;
				break;
			case R.id.register_edit_phone:
				filter_error = VerifyUtil.isValidPhoneNumber(text) ? -1 : R.string.register_phone_filter_error;
				break;
		}
		return filter_error;
	}

	int checkEqualTo(CustomEditText editor) {
		String text = editor.getText().toString();

		int equalto_error = -1;
		switch (editor.getId()) {
			case R.id.register_edit_email_verification:
				equalto_error = (text.equals(edit_email.getText().toString()) ? -1 : R.string.register_email_equalto_error);
				break;
			case R.id.register_edit_confirm_password:
				equalto_error = (text.equals(edit_password.getText().toString()) ? -1 : R.string.register_password_equalto_error);
				break;
		}
		return equalto_error;
	}

	View.OnClickListener btnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int view_id = v.getId();
			switch (view_id) {
				case R.id.register_btn_password:
				case R.id.register_btn_confirm_password:
					changeInputType(view_id);
					break;
				case R.id.register_btn_join_us:
					registration();
					break;
				case R.id.register_btn_country_code:
					displayCountryCode();
					break;
				case R.id.register_btn_get_capture_code:
					new RefreshCaptureCode().execute("");
					break;
			}
		}
	};

	private void changeInputType(int view_id) {
		EditText edit = (view_id == R.id.register_btn_password ? edit_password : edit_confirm_password);
		Button button = (view_id == R.id.register_btn_password ? btn_password_show : btn_confirm_password_show);

		if (edit.getText().toString().isEmpty())
			return;

		TransformationMethod mode = edit.getTransformationMethod();
		String str_mode = mode.toString().toLowerCase();

		if (str_mode.indexOf("password") != -1) {
			edit.setTransformationMethod(SingleLineTransformationMethod.getInstance());
			button.setText(getResources().getString(R.string.register_hide_password));
		} else {
			edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
			button.setText(getResources().getString(R.string.register_show_password));
		}
	}

	private void displayCountryCode() {
		scrollContainer.fullScroll(ScrollView.FOCUS_DOWN);

		if(!edit_code_search.getText().toString().isEmpty()) {
			edit_code_search.setText("");
			code_adapter.updateList("");
		}
		img_code_arrow.setBackgroundResource(R.drawable.arrow_up);
		pw_country_code.showAsDropDown(btn_country_code, -btn_country_code.getWidth(), 5);
	}

	private void registration() {

		String user_name = edit_user_name.getText().toString().replace("\t", "");
		String email = edit_email.getText().toString().replace("\t", "");
		String email_verification = edit_email_verification.getText().toString().replace("\t", "");
		String password = edit_password.getText().toString().replace("\t", "");
		String confirm_password = edit_confirm_password.getText().toString().replace("\t", "");
		String phone = edit_phone.getText().toString().replace("\t", "");
		String referral_code = edit_referral_code.getText().toString().replace("\t", "");
		String capture_code = edit_capture_code.getText().toString().replace("\t", "");

		boolean check_result = true;
		check_result &= checkEditor(edit_user_name, true);
		check_result &= checkEditor(edit_email, true);
		check_result &= checkEditor(edit_email_verification, true);
		check_result &= checkEditor(edit_password, true);
		check_result &= checkEditor(edit_confirm_password, true);
		check_result &= checkEditor(edit_phone, true);
		check_result &= checkEditor(edit_capture_code, true);
		check_result &= (bmp_capture_code != null);
		if (!check_result) {
			if (bmp_capture_code == null) {
				UiUtils.showToast(this, R.string.registration_no_capture_code_image);
			}
			return;
		}

		MyVolley.init(RegistrationActivity.this);

		showProgressDilog(this);

		JSONRequestResponse response = new JSONRequestResponse(RegistrationActivity.this);

		Bundle parms = new Bundle();
		parms.putString("name", user_name);
		parms.putString("email", email);
		parms.putString("reemail", email_verification);
		parms.putString("password", password);
		parms.putString("repassword", confirm_password);
		parms.putString("phonearea", btn_country_code.getText().toString());
		parms.putString("phone", phone);
		parms.putString("referral", referral_code);
		parms.putString("cookiekey", Constant.getDeviceUUID(this));
		parms.putString("captcha", capture_code);

		response.getResponse(Request.Method.GET, Constant.BASE_URL + "register.php", Constant.RETURN_CODE_REGISTRATION, this, parms, false);
	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		UiUtils.showToast(this, R.string.registration_error_other);
		dismissProgress(RegistrationActivity.this);
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {
		dismissProgress(RegistrationActivity.this);

		if (requestCode == Constant.RETURN_CODE_REGISTRATION) {
			Integer errorcode = -1;
			try {
				try {
					errorcode = response.getInt("error");
				} catch (Exception e) {
					errorcode = Constant.UNKNOWN_ERROR;
				}

				if (errorcode == Constant.SUCCESS) {
					Intent intent = new Intent(RegistrationActivity.this, VerifyCodeActivity.class);
					intent.putExtra("email", edit_email.getText().toString());
					startActivity(intent);
				} else {
					if (errorcode == 11000) {
						JSONObject resultDict = response.getJSONObject("result");
						String sub_errorCode = resultDict.getJSONArray("error").toString();
						if (sub_errorCode.contains("401")) {
							edit_user_name.setError(R.string.registration_error_11000_401);
						} else if (sub_errorCode.contains("402")) {
							edit_user_name.setError(R.string.registration_error_11000_402);
						} else if (sub_errorCode.contains("403")) {
							edit_user_name.setError(R.string.registration_error_11000_403);
						} else if (sub_errorCode.contains("404")) {
							edit_email.setError(R.string.registration_error_11000_404);
						} else if (sub_errorCode.contains("405")) {
							edit_phone.setError(R.string.registration_error_11000_405);
						} else if (sub_errorCode.contains("406")) {
							edit_phone.setError(R.string.registration_error_11000_406);
						} else if (sub_errorCode.contains("407")) {
							edit_password.setError(R.string.registration_error_11000_407);
						} else if (sub_errorCode.contains("408")) {
							edit_password.setError(R.string.registration_error_11000_408);
						} else if (sub_errorCode.contains("409")) {
							edit_confirm_password.setError(R.string.registration_error_11000_409);
						} else if (sub_errorCode.contains("421")) {
							edit_referral_code.setError(R.string.registration_error_11000_421);
						}
					} else if (errorcode == 11119) {
						Constant.showPopup_Error_register_package(RegistrationActivity.this);
					} else if (errorcode == 11111) {
						UiUtils.showToast(this, R.string.registration_error_11111);
					} else if (errorcode == 11112) {
						UiUtils.showToast(this, R.string.registration_error_11112);
					} else if (errorcode == 11113) {
						edit_email.setError(R.string.registration_error_11113);
					} else if (errorcode == 11114) {
						edit_password.setError(R.string.registration_error_11114);
					} else if (errorcode == 11007) {
						edit_capture_code.setError(R.string.registration_error_11007);
					} else if (errorcode == 11017) {
						edit_email.setError(R.string.registration_error_11017);
					} else if (errorcode == 58403) {
						UiUtils.showToast(this, R.string.registration_error_58403);
					} else if (errorcode == Constant.UNKNOWN_ERROR) {
						UiUtils.showToast(this, R.string.registration_error_unknown);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				UiUtils.showToast(this, R.string.registration_error_exception);
			}
		}
	}

	@Override
	public void SuccessResponseArray(JSONArray response, int requestCode) {
		dismissProgress(RegistrationActivity.this);
	}

	@Override
	public void SuccessResponseRaw(String response, int requestCode) {
		dismissProgress(RegistrationActivity.this);
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

	class RefreshCaptureCode extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDilog(RegistrationActivity.this);
		}

		@Override
		protected String doInBackground(String... params) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(Constant.BASE_URL + "capt.php?sid=" + params[0]);

			if (bmp_capture_code != null) {
				//bmp_capture_code.recycle();
				bmp_capture_code = null;
				System.gc();
			}

			HttpResponse response;
			try {
				response = httpclient.execute(httpget);
				if (response.getStatusLine().getStatusCode() == 200) {
					// Get hold of the response entity
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						InputStream instream = entity.getContent();
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						int bufferSize = 1024;
						byte[] buffer = new byte[bufferSize];
						int len = 0;

						try {
							while ((len = instream.read(buffer)) != -1) {
								baos.write(buffer, 0, len);
							}
							baos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						byte[] b = baos.toByteArray();
						bmp_capture_code = BitmapFactory.decodeByteArray(b, 0, b.length);
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			btn_get_capture_code.setImageBitmap(bmp_capture_code);
			dismissProgress(RegistrationActivity.this);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		regAct = null;
	}
}
