package israel13.androidtv.Activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.webkit.WebView;

import israel13.androidtv.R;

/**
 * Created by Puspak on 2017.12.01.
 */

public class VerifyResultActivity extends Activity {

	WebView wv_verify_result;
	SharedPreferences logindetails ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_verify_result);
		wv_verify_result=(WebView)findViewById(R.id.wv_verify_result);

		wv_verify_result.getSettings().setJavaScriptEnabled(true);
		wv_verify_result.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

		logindetails = getSharedPreferences("logindetails", MODE_PRIVATE);

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

		String html = "https://mobile.israel.tv/he/packages?sid="+logindetails.getString("sid","");
		wv_verify_result.loadUrl(html);
	}
}
