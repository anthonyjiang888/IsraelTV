package israel13.androidtv.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import israel13.androidtv.Utils.NetworkUtil;
import israel13.androidtv.Utils.UiUtils;
import israel13.androidtv.Utils.VerifyUtil;


/**
 * Created by Puspak on 15/06/17.
 */
public class LoginActivity extends Activity implements IJSONParseListener {

    private int REGISTRATION = 101;
    private int GET_CAPTCHA = 102;
    private int LOGIN = 103;
    private int AUTOLOGIN = 104;
    private ProgressDialog pDialog;
    private SharedPreferences logindetails;
    private EditText et_username, et_password;
    private Button btn_login, btn_forgot_password, btn_registration;
    private TextView txtDeviceID;

    public static LoginActivity logAct = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logAct = this;

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        et_username.setHint(Html.fromHtml("<i>" + "Email address " + "</i>"));
        et_password.setHint(Html.fromHtml("<i>" + "Password" + "<small>" + " " + "</small>"+"</i>"));
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_forgot_password = (Button) findViewById(R.id.btn_forgot_password);
        btn_registration = (Button) findViewById(R.id.btn_registration);
        logindetails = this.getSharedPreferences("logindetails", LoginActivity.MODE_PRIVATE);
        txtDeviceID = (TextView) findViewById(R.id.txt_deviceID);
        txtDeviceID.setText("");

        String strDeviceInfo = getResources().getString(R.string.login_device_id) + ": " + Constant.getDeviceUUID(this);
        txtDeviceID.setText(strDeviceInfo);
        if (Constant.isDeviceUniqueAvailable()) {
            AutoLogin();
        }

        try {
            if (!ResetPassword_Activity.email_forgot_password_text.equalsIgnoreCase("")) {
                et_username.setText(ResetPassword_Activity.email_forgot_password_text);
                et_password.requestFocus();
            } else {
                et_username.requestFocus();
            }
        } catch (Exception e) {
            e.printStackTrace();
            et_username.requestFocus();
        }
        et_username.setFilters(new InputFilter[]{VerifyUtil.spaceInputFilter()});

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (NetworkUtil.checkNetworkAvailable(LoginActivity.this)) {
                    if (et_username.getText().toString().length() == 0 && et_password.getText().toString().length() == 0) {
                        et_username.setError(getResources().getString(R.string.enter_email));
                        et_password.setError(getResources().getString(R.string.enter_password));
                    } else if (et_username.getText().toString().length() == 0) {
                        et_username.setError(getResources().getString(R.string.enter_userid));
                    } else if (et_password.getText().toString().length() == 0) {
                        et_password.setError(getResources().getString(R.string.enter_password));
                    } else Login();
                } else {
                    ShowErrorAlert(LoginActivity.this, "Please check your network connection..");
                }
            }
        });

        btn_login.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    view.animate().scaleX(1).scaleY(1).setDuration(200);
                }else{
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    System.gc();
                    view.animate().scaleX(1.02f).scaleY(1.08f).setDuration(200);
                }
            }
        });
        et_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //do something
                    if (et_username.getText().toString().length() == 0 && et_password.getText().toString().length() == 0) {
                        et_username.setError(getResources().getString(R.string.enter_email));
                        et_password.setError(getResources().getString(R.string.enter_password));
                    } else if (et_username.getText().toString().length() == 0) {
                        et_username.setError(getResources().getString(R.string.enter_userid));
                    } else if (et_password.getText().toString().length() == 0) {
                        et_password.setError(getResources().getString(R.string.enter_password));
                    } else {
                        Login();
                    }
                }
                return false;
            }
        });

        btn_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });
        btn_registration.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    view.animate().scaleX(1).scaleY(1).setDuration(200);
                }else{
                    view.animate().scaleX(1.05f).scaleY(1.08f).setDuration(200);
                }
            }
        });

        btn_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPassword_Activity.class));
            }
        });

        btn_forgot_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    view.animate().scaleX(1).scaleY(1).setDuration(200);
                }else{
                    view.animate().scaleX(1.05f).scaleY(1.08f).setDuration(200);
                }
            }
        });
    }

    public void ShowErrorAlert(final Context c, String text) {
        android.app.AlertDialog.Builder alertDialogBuilder;
        ContextThemeWrapper ctx = new ContextThemeWrapper(c, R.style.Theme_Sphinx);
        alertDialogBuilder = new android.app.AlertDialog.Builder(ctx);
        alertDialogBuilder.setIcon(getResources().getDrawable(R.mipmap.ic_alert));
        alertDialogBuilder.setTitle("Alert!");
        alertDialogBuilder.setMessage(text);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    void Login() {
        JSONRequestResponse mResponse = new JSONRequestResponse(LoginActivity.this);
        Bundle parms = new Bundle();
        parms.putString("user", et_username.getText().toString());
        parms.putString("pass", et_password.getText().toString());
        parms.putString("cookiekey", Constant.getDeviceUUID(this));
        parms.putString("dtype", Constant.DEVICE_TYPE);
        parms.putString("serialno", Constant.getDeviceUUID(this));
        MyVolley.init(LoginActivity.this);
        ShowProgressDilog(this);
        mResponse.getResponse(Request.Method.POST, Constant.BASE_URL + "slogin.php", LOGIN, this, parms, false);
    }

    void AutoLogin(){
        JSONRequestResponse mResponse = new JSONRequestResponse(LoginActivity.this);
        Bundle parms = new Bundle();
        parms.putString("dtype", Constant.DEVICE_TYPE);
        parms.putString("serialno", Constant.getDeviceUUID(this));
        MyVolley.init(LoginActivity.this);
        ShowProgressDilog(this);
        mResponse.getResponse(Request.Method.POST, Constant.BASE_URL + "slogin.php", AUTOLOGIN, this, parms, false);
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode) {

        DismissProgress(LoginActivity.this);
        System.out.println("error:::::" + error.toString());
        UiUtils.showToast(this, error.toString());
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {

        DismissProgress(LoginActivity.this);
        if (requestCode == GET_CAPTCHA) {

            System.out.println("Response for Captcha ::::::::::: " + response.toString());


        } else if (requestCode == REGISTRATION) {


        } else if (requestCode == LOGIN) {
            try {
                String errorcode = "";
                try {
                    errorcode = response.getString("error");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (errorcode.equalsIgnoreCase("11302")) {
                    UiUtils.showToast(this, R.string.login_error_11302);
                } else if (errorcode.equalsIgnoreCase("11301")) {
                    UiUtils.showToast(this, R.string.login_with_interactive_user);
                } else if (errorcode.equalsIgnoreCase("11009")) {
                    UiUtils.showToast(this, R.string.account_not_exists);
                } else if (errorcode.equalsIgnoreCase("429")) {
                    Constant.showPopup_Login_Block(LoginActivity.this);
                } else if (errorcode.equalsIgnoreCase("58403")) {
                    Constant.showPopup_VPN_Block(LoginActivity.this);
                } else {
                    if (!response.has("isactive") || response.getInt("isactive") == 0) {
                        Intent intent = new Intent(LoginActivity.this, VerifyCodeActivity.class);
                        intent.putExtra("email", et_username.getText().toString());
                        startActivity(intent);
                        return;
                    }

                    UiUtils.showToast(this, R.string.login_successfull);

                    SharedPreferences.Editor edit = logindetails.edit();
                    edit.putString("id", response.getString("id"));
                    edit.putString("name", response.getString("name"));
                    edit.putString("password", response.getString("password"));
                    edit.putString("expires", response.getString("expires"));

                    try {
                        JSONObject package1 = response.getJSONObject("package");

                        edit.putString("package_id", package1.getString("id"));
                        edit.putString("package_name", package1.getString("name"));
                        edit.putString("package_price", package1.getString("price"));
                        edit.putString("package_pricestr", package1.getString("pricestr"));
                        edit.putString("package_pgname", package1.getString("pgname"));
                        edit.putString("package_description", package1.getString("description"));
                    } catch (Exception e) {
                        edit.putString("package_id", "");
                        edit.putString("package_name", "");
                        edit.putString("package_price", "");
                        edit.putString("package_pricestr", "");
                        edit.putString("package_pgname", "");
                        edit.putString("package_description", "");
                    }

                    try {
                        edit.putString("regtime", response.getString("regtime"));
                    } catch (Exception e) {
                        edit.putString("regtime", "");
                    }
                    try {
                        edit.putString("phone", response.getString("phone"));
                    } catch (Exception e) {
                        edit.putString("phone", "");
                    }
                    try {
                        edit.putString("carousel", response.getString("carousel"));
                    } catch (Exception e) {
                        edit.putString("carousel", "");
                    }
                    try {
                        edit.putString("isactive", response.getString("isactive"));
                    } catch (Exception e) {
                        edit.putString("isactive", "");
                    }
                    try {
                        edit.putString("activekey", response.getString("activekey"));
                    } catch (Exception e) {
                        edit.putString("activekey", "");
                    }
                    try {
                        edit.putString("email", response.getString("email"));
                    } catch (Exception e) {
                        edit.putString("email", "");
                    }
                    try {
                        edit.putString("status", response.getString("status"));
                    } catch (Exception e) {
                        edit.putString("status", "");
                    }
                    try {
                        edit.putString("ptime", response.getString("ptime"));
                    } catch (Exception e) {
                        edit.putString("ptime", "");
                    }
                    try {
                        edit.putString("sid", response.getString("sid"));
                    } catch (Exception e) {
                        edit.putString("sid", "");
                    }

                    edit.commit();

                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(requestCode == AUTOLOGIN){
            try {
                String errorcode = "";
                try {
                    errorcode = response.getString("error");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (errorcode.equalsIgnoreCase("11302")) {
//                    UiUtils.showToast(this, R.string.login_error_11302);
                } else if (errorcode.equalsIgnoreCase("11301")) {
//                    UiUtils.showToast(this, R.string.login_with_interactive_user);
                } else if (errorcode.equalsIgnoreCase("11009")) {
//                    UiUtils.showToast(this, R.string.account_not_exists);
                } else if (errorcode.equalsIgnoreCase("429")) {
//                    Constant.showPopup_Login_Block(LoginActivity.this);
                } else if (errorcode.equalsIgnoreCase("58403")) {
//                    Constant.showPopup_VPN_Block(LoginActivity.this);
                } else {
                    UiUtils.showToast(this, R.string.login_successfull);

                    SharedPreferences.Editor edit = logindetails.edit();
                    edit.putString("id", response.getString("id"));
                    edit.putString("name", response.getString("name"));
                    edit.putString("password", response.getString("password"));
                    edit.putString("expires", response.getString("expires"));

                    try {
                        JSONObject package1 = response.getJSONObject("package");

                        edit.putString("package_id", package1.getString("id"));
                        edit.putString("package_name", package1.getString("name"));
                        edit.putString("package_price", package1.getString("price"));
                        edit.putString("package_pricestr", package1.getString("pricestr"));
                        edit.putString("package_pgname", package1.getString("pgname"));
                        edit.putString("package_description", package1.getString("description"));
                    } catch (Exception e) {
                        edit.putString("package_id", "");
                        edit.putString("package_name", "");
                        edit.putString("package_price", "");
                        edit.putString("package_pricestr", "");
                        edit.putString("package_pgname", "");
                        edit.putString("package_description", "");
                    }

                    try {
                        edit.putString("regtime", response.getString("regtime"));
                    } catch (Exception e) {
                        edit.putString("regtime", "");
                    }
                    try {
                        edit.putString("phone", response.getString("phone"));
                    } catch (Exception e) {
                        edit.putString("phone", "");
                    }
                    try {
                        edit.putString("carousel", response.getString("carousel"));
                    } catch (Exception e) {
                        edit.putString("carousel", "");
                    }
                    try {
                        edit.putString("isactive", response.getString("isactive"));
                    } catch (Exception e) {
                        edit.putString("isactive", "");
                    }
                    try {
                        edit.putString("activekey", response.getString("activekey"));
                    } catch (Exception e) {
                        edit.putString("activekey", "");
                    }
                    try {
                        edit.putString("email", response.getString("email"));
                    } catch (Exception e) {
                        edit.putString("email", "");
                    }
                    try {
                        edit.putString("status", response.getString("status"));
                    } catch (Exception e) {
                        edit.putString("status", "");
                    }
                    try {
                        edit.putString("ptime", response.getString("ptime"));
                    } catch (Exception e) {
                        edit.putString("ptime", "");
                    }
                    try {
                        edit.putString("sid", response.getString("sid"));
                    } catch (Exception e) {
                        edit.putString("sid", "");
                    }

                    edit.commit();

                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {
        DismissProgress(LoginActivity.this);
    }

    @Override
    public void SuccessResponseRaw(String response, int requestCode) {
        DismissProgress(LoginActivity.this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logAct = null;
    }
}
